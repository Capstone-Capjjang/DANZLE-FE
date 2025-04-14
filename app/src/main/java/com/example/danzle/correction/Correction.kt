package com.example.danzle.correction

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import androidx.media3.exoplayer.ExoPlayer
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.CorrectionResponse
import com.example.danzle.data.remote.response.auth.SilhouetteCorrectionResponse
import com.example.danzle.databinding.ActivityCorrectionBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Correction : AppCompatActivity() {

    private lateinit var binding: ActivityCorrectionBinding

    lateinit var player: ExoPlayer

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private var pollingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCorrectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val songId = 13L
        val sessionId = 25041402L
        val token = DanzleSharedPreferences.getAccessToken()
        val authHeader = "Bearer $token"

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
                    pollingJob?.cancel()
                    recording?.stop()
                    val intent = Intent(this@Correction, CorrectionFinish::class.java)
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
                this@Correction, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@Correction, FragmentCorrection::class.java))
        }
        retrofitCorrection(songId, sessionId)

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


            // 녹화용 Recorder + VideoCapture 설정
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Preview + VideoCapture 둘 다 바인딩
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
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

    private fun startScoringPolling(songId: Long, sessionId: Long, authHeader: String) {
        lifecycleScope.launch {
            while (player.isPlaying) {
                delay(2000) // 2초마다 점수 요청
                fetchCurrentScore(songId, sessionId, authHeader)
            }
        }
    }

    private fun fetchCurrentScore(songId: Long, sessionId: Long, authHeader: String) {
        RetrofitApi.getCorrectionInstance().getCorrection(songId, sessionId, authHeader)
            .enqueue(object : Callback<List<CorrectionResponse>> {
                override fun onResponse(
                    call: Call<List<CorrectionResponse>>,
                    response: Response<List<CorrectionResponse>>
                ) {
                    val score = response.body()?.firstOrNull()?.score ?: return
                    val feedback = when {
                        score >= 95 -> "Perfect"
                        score >= 85 -> "Good"
                        score >= 65 -> "Ok"
                        else -> "Miss"
                    }
                    binding.scoreText.text = feedback
                }

                override fun onFailure(call: Call<List<CorrectionResponse>>, t: Throwable) {
                    Log.e("Score", "실시간 점수 업데이트 실패: ${t.message}")
                }
            })
    }

    private fun retrofitCorrection(songId: Long, sessionId: Long) {
        val token = DanzleSharedPreferences.getAccessToken()
        val authHeader = "Bearer $token"

        Log.d("CorrectionAPI", "Sending request to /accuracy-session/full")
        Log.d("CorrectionAPI", "songId = $songId, sessionId = $sessionId")
        Log.d("CorrectionAPI", "authHeader = $authHeader")

        if (token.isNullOrEmpty()) {
            Toast.makeText(this@Correction, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getCorrectionInstance()
        retrofit.getCorrection(songId, sessionId, authHeader)
            .enqueue(object : Callback<List<CorrectionResponse>> {
                override fun onResponse(
                    call: Call<List<CorrectionResponse>>,
                    response: Response<List<CorrectionResponse>>
                ) {
                    Log.d("CorrectionAPI", "response.isSuccessful = ${response.isSuccessful}")
                    Log.d("CorrectionAPI", "response.code = ${response.code()}")
                    Log.d("CorrectionAPI", "response.body = ${response.body()}")


                    if (response.isSuccessful) {
                        val body = response.body()

                        // ✅ body 자체가 null이거나, 빈 리스트일 경우
                        if (body.isNullOrEmpty()) {
                            Log.w("CorrectionAPI", "응답은 성공했지만 데이터가 없습니다.")
                            Toast.makeText(this@Correction, "아직 분석된 결과가 없어요!", Toast.LENGTH_SHORT).show()
                            return
                        }


                        val correctionResponse = response.body()?.firstOrNull()
                        if (correctionResponse != null) {
                            val score = correctionResponse.score
                            val songName = correctionResponse.song.title
                            val feedback = when {
                                score >= 95 -> "Perfect"
                                score >= 85 -> "Good"
                                score >= 65 -> "Ok"
                                else -> "Miss"
                            }
                            binding.scoreText.text = feedback
                            Log.d("Correction", "Score: $score, Feedback: $feedback")

                            retrofitSilhouetteCorrectionVideo(
                                authHeader,
                                songName,
                                songId,
                                sessionId
                            )
                        }

                    }
                }

                override fun onFailure(call: Call<List<CorrectionResponse>>, t: Throwable) {
                    Log.d("Debug", "HighlightPractice / Error: ${t.message}")
                    Toast.makeText(this@Correction, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun retrofitSilhouetteCorrectionVideo(
        authHeader: String,
        songName: String,
        songId: Long,
        sessionId: Long
    ) {
        Log.d("DEBUG", "Sending request to silhouette API")
        Log.d("DEBUG", "authHeader = $authHeader")

        val retrofit = RetrofitApi.getSilhouetteCorrectionInstance()
        retrofit.getCorrectionSilhouette(authHeader, songName)
            .enqueue(object : Callback<SilhouetteCorrectionResponse> {
                override fun onResponse(
                    call: Call<SilhouetteCorrectionResponse>,
                    response: Response<SilhouetteCorrectionResponse>
                ) {
                    Log.d("DEBUG", "Response code: ${response.code()}")
                    Log.d("DEBUG", "Response body: ${response.body()}")
                    Log.d("DEBUG", "Error body: ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        val videoUrl = response.body()?.silhouetteVideoUrl
                        Log.d("SilhouetteCorrection", "Video URL: $videoUrl")
                        videoUrl?.let {
                            playVideo(videoUrl, songId, sessionId, authHeader)
                        }
                    }
                }

                override fun onFailure(call: Call<SilhouetteCorrectionResponse>, t: Throwable) {
                    Log.e("Silhouette", "Silhouette fetch error: ${t.message}")
                }
            })

    }


    private fun playVideo(videoUrl: String, songId: Long, sessionId: Long, authHeader: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        startRecording()

        pollingJob = lifecycleScope.launch {
            while (player.isPlaying) {
                delay(2000)
                fetchCurrentScore(songId, sessionId, authHeader)
            }
        }
    }

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return

        val name = "correction_recording_${System.currentTimeMillis()}.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Danzle")
            }
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .apply {
                if (ContextCompat.checkSelfPermission(
                        this@Correction, android.Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        Log.d("Recording", "Recording started")
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!event.hasError()) {
                            Log.d("Recording", "Recording saved: ${event.outputResults.outputUri}")
                        } else {
                            Log.e("Recording", "Recording error: ${event.error}")
                        }
                    }
                }
            }
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