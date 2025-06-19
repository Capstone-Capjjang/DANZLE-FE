package com.example.danzle.practice

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
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MusicSelectResponse
import com.example.danzle.data.remote.response.auth.PracticeResponse
import com.example.danzle.data.remote.response.auth.SaveVideoResponse
import com.example.danzle.data.remote.response.auth.SilhouetteResponse
import com.example.danzle.databinding.ActivityMainPracticeBinding
import com.example.danzle.enum.PracticeMode
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


class PracticeMain : AppCompatActivity() {

    var startTime: String = ""
    var endTime: String = ""
    var timestamp: String = ""

    private lateinit var binding: ActivityMainPracticeBinding

    private lateinit var token: String
    private lateinit var authHeader: String

    // ExoPlayer는 Google이 만든 Android용 미디어 플레이어
    // player는 ExoPlayer 객체를 가리킨다.
    lateinit var player: ExoPlayer

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var currentSessionId: Long? = null

    private val selectedSong: MusicSelectResponse? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("selected song", MusicSelectResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("selected song")
        }
    }

    private lateinit var mode: PracticeMode

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = DanzleSharedPreferences.getAccessToken() ?: ""
        authHeader = "Bearer $token"

        mode = intent.getStringExtra("mode")?.let { PracticeMode.valueOf(it) } ?: PracticeMode.FULL

        // initialize exoplayer
        player = ExoPlayer.Builder(this).build()
        // assign player to this view
        binding.playerView.player = player
        // 실루엣 영상 화면에 꽉 채우게
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        binding.playerView.alpha = 0.3f
        binding.playerView.post {
            binding.playerView.bringToFront()
        }


        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY && player.playWhenReady) {
                    startRecording()
                }

                if (state == Player.STATE_ENDED) {
                    stopRecording()

                    val intent = Intent(this@PracticeMain, PracticeFinish::class.java)
                    intent.putExtra("selected song", selectedSong)
                    intent.putExtra("mode", mode)
                    startActivity(intent)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
            }
        })

        // preparation cameraX
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this@PracticeMain, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        selectedSong?.songId?.let { songId ->
            retrofitPractice(songId, mode)
        } ?: run {
            Toast.makeText(this, "선택한 곡 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 필요한 권한 요청
    // 앱이 카메라를 열려면 사용자의 권한이 필요하고 오디오를 녹음하려면 마이크 권한도 필요하다.
    // 자동 호출되는 콜백 함수
    // 시스템이 권한을 요청 -> 사용자가 응답 -> 자동으로 onRequestPermissionsResult 호출
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        // 요청 코드가 올바른지 확인
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) { // 권한이 부여되면 startCamera
                startCamera()
            } else { // 권한이 부여되지 않으면 사용자에게 권한이 부여되지 않았다고 Toast 메세지 표시
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // 카메라 준비 완료시 실행할 콜백 등록
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // 녹화용 Recorder + VideoCapture 설정
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)


            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    /*
    player.playWhenReady = true
    - 재생 준비(버퍼링 등)가 완료되면 자동으로 플레이를 시작해라

    player.playWhenReady = false
    - 준비가 되어도 자동으로 재생하지 말고 대기 상태로 있어라
 */
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun playVideo(url: String, startTime: String, endTime: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun startRecording() {
        // 이미 녹화 중이면 아무것도 하지 않음
        if (recording != null) {
            return
        }

        val videoCapture = this.videoCapture ?: return

        val name = "highlight_practice_${System.currentTimeMillis()}.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Danzle")
            }
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()

        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .apply {
                if (ContextCompat.checkSelfPermission(
                        this@PracticeMain,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { event ->
                when (event) {
                    is VideoRecordEvent.Finalize -> {
                        if (!event.hasError()) {
                            val videoUri = event.outputResults.outputUri
                            val sessionId = currentSessionId ?: return@start
                            savePracticeSessionBeforeUploading(sessionId) {
                                uploadRecordedVideo(videoUri)
                            }
                        }
                        // Finalize 후 녹화 인스턴스 정리
                        recording = null
                    }
                }
            }
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private fun savePracticeSessionBeforeUploading(sessionId: Long, onComplete: () -> Unit) {
        RetrofitApi.getPracticeSaveInstance()
            .savePracticeSession(authHeader, sessionId)
            .enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        onComplete()
                    } else {
                        Toast.makeText(this@PracticeMain, "세션 저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(this@PracticeMain, "서버와 연결되지 않았어요. 인터넷 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uploadRecordedVideo(videoUri: Uri) {
        lifecycleScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(videoUri) ?: return@launch
                val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.mp4")
                val outputStream = FileOutputStream(tempFile)

                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()

                val requestFile = tempFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

                val sessionId = currentSessionId ?: return@launch
                val recordedAt = java.time.LocalDateTime.now().toString()  // ISO 8601 포맷
                val duration = player.currentPosition  // ← 실제 길이에 맞게 계산 가능

                val saveVideoService = RetrofitApi.getSaveVideoInstance()
                val videoModeBody = "PRACTICE".toRequestBody("text/plain".toMediaTypeOrNull())
                val recordedAtBody = recordedAt.toRequestBody("text/plain".toMediaTypeOrNull())
                val durationBody =
                    duration.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val sessionIdBody =
                    sessionId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                saveVideoService.getSaveVideo(
                    authHeader,
                    file = filePart,
                    sessionId = sessionIdBody,
                    videoMode = videoModeBody,
                    recordedAt = recordedAtBody,
                    duration = durationBody
                ).enqueue(object : Callback<SaveVideoResponse> {
                    override fun onResponse(
                        call: Call<SaveVideoResponse>,
                        response: Response<SaveVideoResponse>
                    ) {
                        if (!response.isSuccessful) {
                            Toast.makeText(this@PracticeMain, "영상 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SaveVideoResponse>, t: Throwable) {
                        Toast.makeText(this@PracticeMain, "네트워크 오류로 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(this@PracticeMain, "영상 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //about retrofit
    // actity 내부에서 호출할 때는 context 없이 this로 호출해도 충분하다.
    // Fragment, Adapter 등에서 호출할 때는 requireContext() 또는 전달된 context 필요하다.
    private fun retrofitPractice(songId: Long, mode: PracticeMode) {
        if (token.isNullOrEmpty()) {
            Toast.makeText(this@PracticeMain, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getPracticeInstance()
        when (mode) {
            PracticeMode.FULL -> retrofit.getFullPractice(songId, authHeader)
            PracticeMode.HIGHLIGHT -> retrofit.getHighlightPractice(songId, authHeader)
        }.enqueue(object : Callback<List<PracticeResponse>> {
            override fun onResponse(
                call: Call<List<PracticeResponse>>,
                response: Response<List<PracticeResponse>>
            ) {
                val practiceResponse = response.body()?.firstOrNull()
                if (response.isSuccessful) {
                    val practiceResponse = response.body()?.firstOrNull()
                    if (practiceResponse != null) {
                        val songName = practiceResponse.song.title
                        currentSessionId = practiceResponse.sessionId

                        // 다음 요청: sessionId 기반으로 영상 URL 받기
                        retrofitSilhouetteVideo(authHeader, songName)
                    }
                } else {
                    Toast.makeText(this@PracticeMain, "연습 세션 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PracticeResponse>>, t: Throwable) {
                Toast.makeText(this@PracticeMain, "서버 연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrofitSilhouetteVideo(authHeader: String, songName: String) {
        val retrofit = RetrofitApi.getSilhouetteInstance()
        retrofit.getSilhouette(authHeader, songName)
            .enqueue(object : Callback<SilhouetteResponse> {
                override fun onResponse(
                    call: Call<SilhouetteResponse>,
                    response: Response<SilhouetteResponse>
                ) {
                    if (response.isSuccessful) {
                        val videoUrl = response.body()?.silhouetteVideoUrl
                        videoUrl?.let {
                            playVideo(videoUrl, startTime, endTime)
                        }
                    }  else {
                        Toast.makeText(this@PracticeMain, "실루엣 영상을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SilhouetteResponse>, t: Throwable) {
                    Toast.makeText(this@PracticeMain, "영상 요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
