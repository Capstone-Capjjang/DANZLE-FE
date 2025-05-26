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
import android.net.Uri
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
import com.example.danzle.data.remote.response.auth.CorrectionResponse
import com.example.danzle.data.remote.response.auth.MusicSelectResponse
import com.example.danzle.data.remote.response.auth.PoseAnalysisResponse
import com.example.danzle.data.remote.response.auth.SaveResponse
import com.example.danzle.data.remote.response.auth.SaveVideoResponse
import com.example.danzle.data.remote.response.auth.SilhouetteResponse
import com.example.danzle.databinding.ActivityCorrectionBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class Correction : AppCompatActivity() {

    private lateinit var binding: ActivityCorrectionBinding

    lateinit var player: ExoPlayer

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private var pollingJob: Job? = null

    private var lastSentTime = 0L

    private lateinit var token: String

    private var currentSessionId: Long? = null
    private lateinit var authHeader: String

    // 나중에 서버 수정하고 서버에서 songId 데이터 받아서 넣어주기
    private val selectedSong: MusicSelectResponse? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("selected song", MusicSelectResponse::class.java)
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

        token = DanzleSharedPreferences.getAccessToken() ?: ""
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
                    startActivity(Intent(this@Correction, CorrectionResult::class.java))
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

        retrofitCorrection()
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
        val songId = selectedSong?.songId ?: return
        val sessionId = currentSessionId ?: return

        // 1초마다 한 번만 전송
        if (currentTime - lastSentTime >= 800) {
            try {
                val bitmap = imageProxyToBitmap(imageProxy)
                sendFrameToServer(bitmap, songId, sessionId, authHeader)
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

    private fun sendFrameToServer(
        bitmap: Bitmap,
        songId: Long,
        sessionId: Long,
        authHeader: String
    ) {
        Log.d("SessionCheck", "Sending frame with sessionId = $sessionId")

        val stream = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, this)
        }.toByteArray()

        val imageRequestBody = stream.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("frame", "frame.jpg", imageRequestBody)


        // RequestBody 변환 다 제거! 그대로 Long 넘겨주기
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
                        // 여기에 점수 기반 피드백 출력
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

                                binding.scoreText.setTextColor(
                                    ContextCompat.getColor(
                                        this@Correction,
                                        colorRes
                                    )
                                )
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

    private fun playVideo(videoUrl: String) {
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
                        val videoUri = event.outputResults.outputUri
                        Log.d("Recording", "Recording saved: $videoUri")
                        val sessionId = currentSessionId ?: return@start
                        saveSessionIdBeforeUploading(sessionId) {
                            uploadRecordedVideo(videoUri)
                        }
                    } else {
                        Log.e("Recording", "Recording error: ${event.error}")
                    }
                }
            }
        }
    }

    private fun saveSessionIdBeforeUploading(sessionId: Long, onComplete: () -> Unit) {

        Log.d("SaveVideo", "Sending SaveRequest: sessionId = $sessionId")

        RetrofitApi.getSaveInstance().saveSession(authHeader,sessionId)
            .enqueue(object : Callback<SaveResponse> {
                override fun onResponse(call: Call<SaveResponse>, response: Response<SaveResponse>) {
                    if (response.isSuccessful) {
                        Log.d("SaveVideo", "Raw response: ${response.body().toString()}")
                        onComplete() // 저장 성공 후 업로드 진행
                    } else {
                        Log.e("SaveVideo", "Failed to save session: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<SaveResponse>, t: Throwable) {
                    Log.e("SaveVideo", "Error saving session: ${t.message}")
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
                val videoModeBody = "ACCURACY".toRequestBody("text/plain".toMediaTypeOrNull())
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
                        if (response.isSuccessful) {
                            Log.d("Upload", "Upload success: ${response.body()}")

                            val intent = Intent(this@Correction, CorrectionResult::class.java)
                            intent.putExtra("sessionId", sessionId)
                            startActivity(intent)
                        } else {
                            Log.e("Upload", "Upload failed: ${response.code()}")
                        }
                    }

                    override fun onFailure(
                        call: Call<SaveVideoResponse>,
                        t: Throwable
                    ) {
                        Log.e("Upload", "Upload error: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                Log.e("Upload", "Upload failed", e)
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

    private fun retrofitCorrection() {
        val songId = selectedSong?.songId ?: run {
            Toast.makeText(this@Correction, "선택한 곡 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("CorrectionAPI", "Sending request to /accuracy-session/full")
        Log.d("CorrectionAPI", "songId = $songId")
        Log.d("CorrectionAPI", "authHeader = $authHeader")

        if (token.isNullOrEmpty()) {
            Toast.makeText(this@Correction, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getCorrectionInstance()
        retrofit.getCorrection(authHeader, songId)
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
                        Log.d(
                            "CorrectionAPI",
                            "response.errorBody = ${response.errorBody()?.string()}"
                        )
                    }

                    if (response.isSuccessful) {
                        val correctionResponse = response.body()
                        // body 자체가 null이거나, 빈 리스트일 경우

                        if (correctionResponse != null) {
                            currentSessionId = correctionResponse.sessionId
                            Log.d("SessionCheck", "Received sessionId = $currentSessionId")
                            val songName = correctionResponse.songTitle

                            // 세션 받자마자 첫 프레임 전송!
                            binding.previewView.bitmap?.let {
                                sendFrameToServer(it, songId, currentSessionId!!, authHeader)
                            }

                            retrofitSilhouetteCorrectionVideo(
                                authHeader, songName
                            )
                        } else {  val code = response.code()
                            val raw = response.raw()
                            val errorBody = response.errorBody()?.string()

                            Log.e("CorrectionAPI", "Server error")
                            Log.e("CorrectionAPI", "→ Status code: $code")
                            Log.e("CorrectionAPI", "→ Raw response: $raw")
                            Log.e("CorrectionAPI", "→ Error body: $errorBody")
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
        authHeader: String, songName: String
    ) {
        Log.d("DEBUG", "Sending request to silhouette API")
        Log.d("DEBUG", "authHeader = $authHeader")

        val retrofit = RetrofitApi.getSilhouetteInstance()
        retrofit.getSilhouette(authHeader, songName)
            .enqueue(object : Callback<SilhouetteResponse> {
                override fun onResponse(
                    call: Call<SilhouetteResponse>,
                    response: Response<SilhouetteResponse>
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
                            playVideo(videoUrl)
                        }
                    }
                }

                override fun onFailure(call: Call<SilhouetteResponse>, t: Throwable) {
                    Log.e("Silhouette", "Silhouette fetch error: ${t.message}")
                }
            })
    }
}