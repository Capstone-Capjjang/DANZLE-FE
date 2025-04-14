package com.example.danzle.practice

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.HighlightPracticeResponse
import com.example.danzle.data.remote.response.auth.SilhouettePracticeResponse
import com.example.danzle.databinding.ActivityHighlightPracticeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService


class HighlightPractice : AppCompatActivity() {

    var startTime: String = ""
    var endTime: String = ""
    var timestamp: String = ""

    private lateinit var binding: ActivityHighlightPracticeBinding

    lateinit var player: ExoPlayer

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHighlightPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initialize exoplayer
        player = ExoPlayer.Builder(this).build()
        // assign player to this view
        binding.playerView.player = player

        binding.playerView.alpha = 0.5f
        binding.playerView.post {
            binding.playerView.bringToFront()
        }


        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                Log.d("ExoPlayerState", "State changed: $state")

                // 영상 끝났을 때 화면 전환
                if (state == Player.STATE_ENDED) {
                    Log.d("ExoPlayerState", "Video ended!")

                    val intent = Intent(this@HighlightPractice, HighlightPracticeFinish::class.java)
                    startActivity(intent)
                }

            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("ExoPlayerError", "Playback error: ${error.message}")
            }
        })

        // preparation cameraX
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this@HighlightPractice, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // recording button
        binding.videoCaptureButton.setOnClickListener {
            captureVideo()
        }

        // Retrofit request
        val songId = 12L
        retrofitHighlightPractice(songId)
    }


    // Implements VideoCapture use case, including start and stop capturing.
    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.videoCaptureButton.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        this@HighlightPractice,
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.videoCaptureButton.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                        }
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(
                                TAG, "Video capture ends with error: " +
                                        "${recordEvent.error}"
                            )
                        }
                        binding.videoCaptureButton.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    // 필요한 권한 요청
    // 앱이 카메라를 열려면 사용자의 권한이 필요하고 오디오를 녹음하려면 마이크 권한도 필요하다.
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

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    //about retrofit
    // actity 내부에서 호출할 때는 context 없이 this로 호출해도 충분하다.
    // Fragment, Adapter 등에서 호출할 때는 requireContext() 또는 전달된 context 필요하다.
    private fun retrofitHighlightPractice(songId: Long) {
        // SharedPreferences에 저장된 토큰 가져옴
        val token = DanzleSharedPreferences.getAccessToken()

        val authHeader = "Bearer $token"
        //꼭 Baearer를 붙여야 한다.

        if (token.isNullOrEmpty()) {
            Toast.makeText(this@HighlightPractice, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getHighlightPracticeInstance()
        retrofit.getHighlightPractice(songId, authHeader)
            .enqueue(object : Callback<List<HighlightPracticeResponse>> {
                override fun onResponse(
                    call: Call<List<HighlightPracticeResponse>>,
                    response: Response<List<HighlightPracticeResponse>>
                ) {
                    if (response.isSuccessful) {
                        val highlightPracticeResponse = response.body()?.firstOrNull()
                        if (highlightPracticeResponse != null) {
                            val songName = highlightPracticeResponse.song.title

                            // 다음 요청: sessionId 기반으로 영상 URL 받기
                            retrofitSilhouetteVideo(authHeader, songName)
                        }

                    } else {
                        Log.e(
                            "HighlightPractice",
                            "Fail to call fetchSilhouetteVideo: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<List<HighlightPracticeResponse>>, t: Throwable) {
                    Log.d("Debug", "HighlightPractice / Error: ${t.message}")
                    Toast.makeText(this@HighlightPractice, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun retrofitSilhouetteVideo(authHeader: String, songName: String) {
        Log.d("DEBUG", "Sending request to silhouette API")
        Log.d("DEBUG", "authHeader = $authHeader")


        val retrofit = RetrofitApi.getPracticeSilhouetteInstance()
        retrofit.getPracticeSilhouette(authHeader, songName)
            .enqueue(object : Callback<SilhouettePracticeResponse> {
                override fun onResponse(
                    call: Call<SilhouettePracticeResponse>,
                    response: Response<SilhouettePracticeResponse>
                ) {
                    Log.d("DEBUG", "Response code: ${response.code()}")
                    Log.d("DEBUG", "Response body: ${response.body()}")
                    Log.d("DEBUG", "Error body: ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        val videoUrl = response.body()?.silhouetteVideoUrl
                        Log.d("Silhouette", "Video URL: $videoUrl")
                        videoUrl?.let {
                            playVideo(videoUrl, startTime, endTime)
                        }
                    }
                }

                override fun onFailure(call: Call<SilhouettePracticeResponse>, t: Throwable) {
                    Log.e("Silhouette", "Silhouette fetch error: ${t.message}")
                }
            })
    }

    private fun playVideo(url: String, startTime: String, endTime: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
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
}