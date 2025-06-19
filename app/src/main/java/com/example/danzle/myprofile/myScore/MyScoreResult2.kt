package com.example.danzle.myprofile.myScore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R
import com.example.danzle.correction.CorrectionDetailFeedbackPagerAdapter
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.CorrectionFeedbackDetailResponse
import com.example.danzle.databinding.ActivityMyScoreResult2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScoreResult2 : AppCompatActivity() {
    private lateinit var binding: ActivityMyScoreResult2Binding
    private var sessionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyScoreResult2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish() // 단순히 현재 activity를 종료
        }

        sessionId = intent.getLongExtra("sessionId", sessionId)

        if (sessionId == -1L) {
            Toast.makeText(this, "잘못된 sessionId입니다.", Toast.LENGTH_SHORT).show()
        } else {
            retrofitScoreDetailFeedback(sessionId)
        }
    }

    private fun retrofitScoreDetailFeedback(sessionId: Long) {
        val token = DanzleSharedPreferences.getAccessToken() ?: ""
        val authHeader = "Bearer $token"

        val retrofit = RetrofitApi.getCorrectionDetailFeedbackInstance()
        retrofit.getCorrectionDetailFeedback(authHeader, sessionId)
            .enqueue(object : Callback<List<CorrectionFeedbackDetailResponse>> {
                override fun onResponse(
                    call: Call<List<CorrectionFeedbackDetailResponse>>,
                    response: Response<List<CorrectionFeedbackDetailResponse>>
                ) {
                    if (response.isSuccessful) {
                        val allFeedback = response.body().orEmpty()
                        val top3 = allFeedback.take(3)

                        val adapter = CorrectionDetailFeedbackPagerAdapter(top3)
                        binding.feedbackPager.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@MyScoreResult2,
                            "서버 오류: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<CorrectionFeedbackDetailResponse>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@MyScoreResult2,
                        "네트워크 에러: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            )
    }
}