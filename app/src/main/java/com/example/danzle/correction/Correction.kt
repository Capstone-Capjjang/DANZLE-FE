package com.example.danzle.correction

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
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
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.CorrectionMusicSelectResponse
import com.example.danzle.data.remote.response.auth.CorrectionResponse
import com.example.danzle.data.remote.response.auth.PoseAnalysisResponse
import com.example.danzle.data.remote.response.auth.SilhouetteCorrectionResponse
import com.example.danzle.databinding.ActivityCorrectionBinding
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class Correction : AppCompatActivity() {

    private lateinit var binding: ActivityCorrectionBinding

    lateinit var player: ExoPlayer

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private var pollingJob: Job? = null

    private var lastSentTime = 0L

    private var currentSessionId: Long? = null
    private lateinit var authHeader: String

    // 나중에 서버 수정하고 서버에서 songId 데이터 받아서 넣어주기
    private val selectedSong: CorrectionMusicSelectResponse? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("selected song", CorrectionMusicSelectResponse::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra("selected song")
        }
    }

    @OptIn(UnstableApi::class)
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

        val songId = 14L
        val token = DanzleSharedPreferences.getAccessToken()
        authHeader = "Bearer $token"


        // initialize exoplayer
        player = ExoPlayer.Builder(this).build()
        // assign player to this view
        binding.playerView.player = player
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        binding.playerView.alpha = 0.3f
        binding.playerView.post {
            binding.playerView.bringToFront()
        }


        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                Log.d("ExoPlayerState", "State changed: $state, isPlaying=${player.isPlaying}")

                if (state == Player.STATE_READY && player.playWhenReady) {
                    Log.d("Polling", "State READY & playWhenReady = true → Start polling")
                    if (pollingJob == null) {
                        //startPolling(songId, authHeader)
                    }
                }

                if (state == Player.STATE_ENDED) {
                    pollingJob?.cancel()
                    recording?.stop()
                    startActivity(Intent(this@Correction, CorrectionFinish::class.java))
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
            Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "practice")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also {
                startActivity(it)
            }
            recording?.stop()

        }
        retrofitCorrection(songId)

    }

    override fun onPause() {
        super.onPause()
        if (::player.isInitialized) {
            player.pause()
        }
    }

    override fun onDestroy() {
        if (::player.isInitialized) {
            player.release()
        }
        super.onDestroy()
    }

//    private fun startPolling(songId: Long, authHeader: String) {
//        pollingJob = lifecycleScope.launch {
//            while (player.playbackState != Player.STATE_ENDED) {
//                Log.d("Polling", "Polling fetchCurrentScore 실행")
//                fetchCurrentScore(songId, authHeader)
//                delay(1000) // 음답 요청 속도
//            }
//        }
//    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        analyzeFrameWithMediaPipe(imageProxy)
                    }
                }

            // 녹화용 Recorder + VideoCapture 설정
            val recorder =
                Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HD)).build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Preview + VideoCapture 둘 다 바인딩
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture, imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeFrameWithMediaPipe(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()

        // 1초마다 한 번만 전송
        if (currentTime - lastSentTime >= 800) {
            try {
                val bitmap = imageProxyToBitmap(imageProxy)
                sendFrameToServer(bitmap)
                lastSentTime = currentTime
            } catch (e: Exception) {
                Log.e("MediaPipe", "Error converting frame: ${e.message}")
            } finally {
                imageProxy.close()
            }
        } else {
            imageProxy.close()  // 닫아줘야 함!
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer
        val vuBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)
        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 80, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun sendFrameToServer(bitmap: Bitmap) {
        val songId = 14L

        // 서버에서 받아온 sessionId 사용. 아직 받아오지 않았다면 전송하지 않음.
        val sessionId = currentSessionId ?: run {
            Log.e("AnalyzeFrame", "현재 sessionId가 없습니다. /accuracy-session/full API 호출 실패?")
            return
        }
        Log.d("SessionCheck", "Sending frame with sessionId = $sessionId")

        val stream = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, this)
        }.toByteArray()

        val imageRequestBody = stream.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("frame", "frame.jpg", imageRequestBody)


        // ✅ RequestBody 변환 다 제거! 그대로 Long 넘겨주기
        // /analyze 요청을 보내도록 변경 (기존 uploadFrame 대신 analyzeFrame 사용)
        RetrofitApi.getPoseAnalysisInstance()  // getAnalysisInstance()는 AnalysisApiService를 반환하는 메서드로 구현
            .uploadFrame(authHeader, imagePart, songId, sessionId)
            .enqueue(object : Callback<PoseAnalysisResponse> {
                override fun onResponse(
                    call: Call<PoseAnalysisResponse>,
                    response: Response<PoseAnalysisResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.e("AnalyzeFrame", "서버 응답 실패: ${response.code()}")
                        response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            Log.e("AnalyzeFrame", "에러 상세내용: $errorMessage")
                        }
                    } else {
                        // 서버로부터 성공 응답이 오면 추가 처리를 여기에 구현 (예: UI 업데이트 등)
                        // 🎯 여기에 점수 기반 피드백 출력
                        val result = response.body()
                        val score = result?.score
                        val feedback = result?.feedback
                        if (score != null) {
                            Log.d("AnalyzeFrame", "Score: $score → Feedback: $feedback")
                            runOnUiThread {
                                binding.scoreText.text = feedback
                                val colorRes = when (feedback) {
                                    "Perfect" -> R.color.scorePerfectText
                                    "Good" -> R.color.scoreGoodText
                                    "Normal" -> R.color.scoreNormalText
                                    "Bad" -> R.color.scoreBadText
                                    else -> R.color.grayText
                                }

                                binding.scoreText.setTextColor(ContextCompat.getColor(this@Correction, colorRes))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<PoseAnalysisResponse>, t: Throwable) {
                    Log.e("AnalyzeFrame", "전송 실패: ${t.message}")
                }
            })
    }


    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

//    private fun startScoringPolling(songId: Long, sessionId: Long, authHeader: String) {
//        lifecycleScope.launch {
//            while (player.isPlaying) {
//                delay(2000) // 2초마다 점수 요청
//                fetchCurrentScore(songId, sessionId, authHeader)
//            }
//        }
//    }

//    private fun fetchCurrentScore(songId: Long, authHeader: String) {
//        RetrofitApi.getCorrectionInstance().getCorrection(songId, authHeader)
//            .enqueue(object : Callback<CorrectionResponse> {
//                override fun onResponse(
//                    call: Call<CorrectionResponse>,
//                    response: Response<CorrectionResponse>
//                ) {
//                    Log.d("Polling", "Polling fetchCurrentScore called")
//
//
//                    Log.d("Polling", "Polling fetchCurrentScore called")
//                    val feedback = response.body()?.message ?: return
//                    binding.scoreText.text = feedback
//                }
//
//                override fun onFailure(call: Call<CorrectionResponse>, t: Throwable) {
//                    Log.e("Score", "실시간 점수 업데이트 실패: ${t.message}")
//                }
//            })
//    }

    private fun retrofitCorrection(songId: Long) {
        val token = DanzleSharedPreferences.getAccessToken()
        val authHeader = "Bearer $token"

        Log.d("CorrectionAPI", "Sending request to /accuracy-session/full")
        Log.d("CorrectionAPI", "songId = $songId")
        Log.d("CorrectionAPI", "authHeader = $authHeader")

        if (token.isNullOrEmpty()) {
            Toast.makeText(this@Correction, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getCorrectionInstance()
        retrofit.getCorrection(songId, authHeader)
            .enqueue(object : Callback<CorrectionResponse> {
                override fun onResponse(
                    call: Call<CorrectionResponse>,
                    response: Response<CorrectionResponse>
                ) {
                    Log.d("CorrectionAPI", "response.isSuccessful = ${response.isSuccessful}")
                    Log.d("CorrectionAPI", "response.code = ${response.code()}")
                    Log.d("CorrectionAPI", "response.body = ${response.body()}")


                    if (response.body() == null) {
                        Log.w("CorrectionAPI", "body is NULL!")
                        Log.d("CorrectionAPI", "response.raw = ${response.raw()}")
                        Log.d("CorrectionAPI", "response.errorBody = ${response.errorBody()?.string()}")
                    }

                    if (response.isSuccessful) {
                        val correctionResponse = response.body()
                        // body 자체가 null이거나, 빈 리스트일 경우

                        if (correctionResponse != null) {
                            currentSessionId = correctionResponse.sessionId
                            Log.d("SessionCheck", "Received sessionId = $currentSessionId from /full")
                            val songName = correctionResponse.song_title // 기존: correctionResponse.song.title

                            // 🎯 세션 받자마자 첫 프레임 전송!
                            binding.previewView.bitmap?.let {
                                sendFrameToServer(it)
                            }

                            retrofitSilhouetteCorrectionVideo(
                                authHeader, songName, songId, currentSessionId!!
                            )
                        } else {
                            // body 자체가 null이거나, 빈 리스트일 경우
                            Log.w("CorrectionAPI", "응답은 성공했지만 데이터가 없습니다.")
                            Toast.makeText(this@Correction, "아직 분석된 결과가 없어요!", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }

                    }
                }

                override fun onFailure(call: Call<CorrectionResponse>, t: Throwable) {
                    Log.d("Debug", "HighlightPractice / Error: ${t.message}")
                    Toast.makeText(this@Correction, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun retrofitSilhouetteCorrectionVideo(
        authHeader: String, songName: String, songId: Long, sessionId: Long
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
                        if (videoUrl == null) {
                            Log.e("SilhouetteCorrection", "영상 URL이 null입니다.")
                        }
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
        ).setContentValues(contentValues).build()

        recording = videoCapture.output.prepareRecording(this, outputOptions).apply {
            if (ContextCompat.checkSelfPermission(
                    this@Correction, android.Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                withAudioEnabled()
            }
        }.start(ContextCompat.getMainExecutor(this)) { event ->
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
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

//    private fun getFeedbackFromScore(score: Double): String {
//        return when {
//            score >= 95 -> "Perfect"
//            score >= 85 -> "Good"
//            score >= 65 -> "Ok"
//            else -> "Miss"
//        }
//    }

}