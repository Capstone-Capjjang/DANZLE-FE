package com.example.danzle.correction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.FakeFeedbackResponse
import com.example.danzle.databinding.ActivityCorrectionDetailFeedbackBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CorrectionDetailFeedback : AppCompatActivity() {

    private lateinit var binding: ActivityCorrectionDetailFeedbackBinding

    private var sessionId: Long = -1L
    private lateinit var token: String
    private lateinit var authHeader: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCorrectionDetailFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = DanzleSharedPreferences.getAccessToken() ?: ""
        authHeader = "Bearer $token"

        binding.backButton.setOnClickListener {
            startActivity(Intent(this@CorrectionDetailFeedback, CorrectionResult::class.java))
        }

        binding.cancelButton.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "correction")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also {
                startActivity(it)
            }
        }

        sessionId = 729L
        fetchFakeFeedback(authHeader, sessionId)

        //sessionId = intent.getLongExtra("sessionId", sessionId)
        Log.d("CorrectionDetailFeedaback", "sessionId: $sessionId")
//
//        if (sessionId == -1L) {
//            Toast.makeText(this, "잘못된 sessionId입니다.", Toast.LENGTH_SHORT).show()
//        } else {
//            fetchFakeFeedback(sessionId)
//
//        }
    }

    private fun fetchFakeFeedback(authHeader: String, sessionId: Long) {
        Log.d("FakeFeedback", "Sending request with sessionId=$sessionId")

        val retrofit = RetrofitApi.getFakeFeedbackInstance()
        retrofit.getFakeFeedback(authHeader,sessionId).enqueue(object : Callback<List<FakeFeedbackResponse>> {
            override fun onResponse(
                call: Call<List<FakeFeedbackResponse>>,
                response: Response<List<FakeFeedbackResponse>>
            ) {
                Log.d("FakeFeedback", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val feedbackList = response.body().orEmpty()
                    val top3 = feedbackList.take(3)

                    val displayText = top3.firstOrNull()?.feedback ?: "피드백이 없습니다."
                    binding.feedbackContent.text = displayText

                    top3.firstOrNull()?.let { first ->
                        // 사용자 이미지
                        Glide.with(this@CorrectionDetailFeedback)
                            .load(first.userImageURL)
                            .into(binding.feedbackImage)
                    }
                } else {
                    Log.e("FakeFeedback", "Server error: ${response.code()}, ${response.errorBody()?.string()}")
                    Toast.makeText(this@CorrectionDetailFeedback, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FakeFeedbackResponse>>, t: Throwable) {
                Log.e("FakeFeedback", "Network error", t)
                Toast.makeText(this@CorrectionDetailFeedback, "네트워크 오류: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

//    private fun retrofitCorrectionDetailFeedback(sessionId: Long) {
////        val token = DanzleSharedPreferences.getAccessToken() ?: ""
////        val authHeader = "Bearer $token"
//
//        Log.d("CorrectionDetail", "Sending request with sessionId=$sessionId")
//
//        val retrofit = RetrofitApi.getCorrectionDetailFeedbackInstance()
//        retrofit.getCorrectionDetailFeedback(sessionId)
//            .enqueue(object : Callback<List<CorrectionFeedbackDetailResponse>> {
//                override fun onResponse(
//                    call: Call<List<CorrectionFeedbackDetailResponse>>,
//                    response: Response<List<CorrectionFeedbackDetailResponse>>
//                ) {
//                    Log.d("CorrectionDetail", "Response code: ${response.code()}")
//                    if (response.isSuccessful) {
//                        val allFeedback = response.body().orEmpty()
//                        val top3 = allFeedback.take(3)
//                        val displayText = top3.joinToString(separator = "\n\n") { feedback ->
//                            feedback.feedbacks.joinToString(separator = "\n")
//                        }
//                        Log.d("CorrectionDetail", "Received feedback list: $allFeedback")
//                        binding.feedbackContent.text = displayText
//
//                        top3.firstOrNull()?.let { first ->
//                            Glide.with(this@CorrectionDetailFeedback)
//                                .load(first.userImageUrl)
//                                .into(binding.feedbackImage)
//
//                            Glide.with(this@CorrectionDetailFeedback)
//                                .load(first.expertImageUrl)
//                                .into(binding.feedbackImage)
//                        }
//                    } else {
//                        Log.e(
//                            "CorrectionDetail",
//                            "Server error: code=${response.code()}, errorBody=${
//                                response.errorBody()?.string()
//                            }"
//                        )
//                        Toast.makeText(
//                            this@CorrectionDetailFeedback,
//                            "서버 오류: ${response.code()}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<List<CorrectionFeedbackDetailResponse>>, t: Throwable) {
//                    Log.e("CorrectionDetail", "Network error", t)
//                    Toast.makeText(
//                        this@CorrectionDetailFeedback,
//                        "네트워크 에러: ${t.localizedMessage}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//    }
//}