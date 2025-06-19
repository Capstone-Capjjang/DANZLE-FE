package com.example.danzle.challenge

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.SaveVideoResponse
import com.example.danzle.databinding.ActivityChallengeBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class Challenge : AppCompatActivity() {
    private lateinit var binding: ActivityChallengeBinding
    private lateinit var player: ExoPlayer

    private var sessionId = 21L
    private var songId = 20L  // 기본 songId 값 지정

    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    companion object {
        private const val TAG = "Challenge"
        private const val REQUEST_CODE_PERMISSIONS = 1001
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelButton.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "challenge")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also { startActivity(it) }
            recording?.stop()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.playerView.player = exo
            binding.playerView.alpha = 0.3f

            val uri = Uri.parse("android.resource://$packageName/${R.raw.challenge3}")
            val mediaItem = MediaItem.fromUri(uri)

            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.playWhenReady = false // fetchHighlightSession에서 시작
        }

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> if (player.playWhenReady) startRecording()
                    Player.STATE_ENDED -> {
                        stopRecording()
                        player.release()
                        Intent(this@Challenge, MainActivity::class.java).apply {
                            putExtra("navigate_to", "challenge")
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }.also { startActivity(it) }
                        finish()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
            }
        })

        if (allPermissionsGranted()) {
            startCameraAndRecorder()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        fetchHighlightSession()
    }

    private fun fetchHighlightSession() {
        val token = DanzleSharedPreferences.getAccessToken() ?: ""
        val authHeader = "Bearer $token"

        lifecycleScope.launch {
            try {
                val response = RetrofitApi.getChallengeHighlightInstance()
                    .getChallengeHighlightSession(authHeader, songId)

                if (response.isSuccessful) {
                    val body = response.body()

                    val highlightSession = body?.firstOrNull()  // ✅ 리스트에서 첫 번째 값만
                    sessionId = highlightSession?.sessionId ?: 0L

                    player.playWhenReady = true
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted()) {
            startCameraAndRecorder()
        } else {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startCameraAndRecorder() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val previewUseCase = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(binding.previewView.display.rotation)
                .build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, cameraSelector, previewUseCase, videoCapture)
            } catch (e: Exception) {
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startRecording() {
        if (recording != null) return
        val videoCapture = this.videoCapture ?: return

        val name = "challenge_record_${System.currentTimeMillis()}.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/DanzleChallenge")
            }
        }

        val outputOptions = MediaStoreOutputOptions.Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues).build()

        recording = videoCapture.output.prepareRecording(this, outputOptions).apply {
            if (ContextCompat.checkSelfPermission(this@Challenge, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                withAudioEnabled()
            }
        }.start(ContextCompat.getMainExecutor(this)) { event ->
            when (event) {
                is VideoRecordEvent.Finalize -> {
                    if (!event.hasError()) {
                        val videoUri = event.outputResults.outputUri
                        saveChallengeSessionBeforeUploading(sessionId) {
                            uploadRecordedVideo(videoUri)
                        }
                    }
                    recording = null
                }
            }
        }
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
    }

    private fun uploadRecordedVideo(videoUri: Uri) {
        lifecycleScope.launch {
            try {
                // 1. 영상 파일 복사 (tempFile 생성)
                val inputStream = contentResolver.openInputStream(videoUri) ?: return@launch
                val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.mp4")
                val outputStream = FileOutputStream(tempFile)

                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()

                val requestFile = tempFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)


                val token = DanzleSharedPreferences.getAccessToken() ?: ""
                val authHeader = "Bearer $token"

                val recordedAt = java.time.LocalDateTime.now().toString()  // ISO 8601 포맷
                val duration = player.currentPosition  // ← 실제 길이에 맞게 계산 가능

                val videoModeBody = "CHALLENGE".toRequestBody("text/plain".toMediaTypeOrNull())
                val recordedAtBody = recordedAt.toRequestBody("text/plain".toMediaTypeOrNull())
                val durationBody =
                    duration.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val sessionIdBody =
                    sessionId.toString().toRequestBody("text/plain".toMediaTypeOrNull())


                // 3. 영상 업로드 API 호출
                RetrofitApi.getSaveVideoInstance().getSaveVideo(
                    authHeader,
                    file = filePart, sessionId = sessionIdBody, videoMode = videoModeBody,recordedAt = recordedAtBody, duration = durationBody
                ).enqueue(object : Callback<SaveVideoResponse> {
                    override fun onResponse(
                        call: Call<SaveVideoResponse>,
                        response: Response<SaveVideoResponse>
                    ) {
                        if (response.isSuccessful) {
                        } else {
                            Toast.makeText(this@Challenge, "영상 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SaveVideoResponse>, t: Throwable) {
                        Toast.makeText(this@Challenge, "영상 업로드 오류", Toast.LENGTH_SHORT).show()
                    }
                })

            } catch (e: Exception) {
                Toast.makeText(this@Challenge, "영상 처리 중 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveChallengeSessionBeforeUploading(sessionId: Long, onComplete: () -> Unit) {
        val authHeader = "Bearer ${DanzleSharedPreferences.getAccessToken() ?: return}"
        RetrofitApi.getChallengeSaveInstance()
            .saveChallengeSession(authHeader, sessionId)
            .enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        onComplete()
                    } else {
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            })
    }



    override fun onDestroy() {
        super.onDestroy()
        player.release()
        cameraProvider?.unbindAll()
    }
}