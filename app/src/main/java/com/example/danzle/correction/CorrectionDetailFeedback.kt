package com.example.danzle.correction

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.CorrectionFeedbackDetailResponse
import com.example.danzle.databinding.ActivityCorrectionDetailFeedbackBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CorrectionDetailFeedback : AppCompatActivity() {

    private lateinit var binding: ActivityCorrectionDetailFeedbackBinding

    private var sessionId: Long = -1L

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

        binding.fullscreenLoading.visibility = View.VISIBLE
        binding.resultContentGroup.visibility = View.GONE

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
        sessionId = intent.getLongExtra("sessionId", sessionId)

        if (sessionId == -1L) {
            Toast.makeText(this, "잘못된 sessionId입니다.", Toast.LENGTH_SHORT).show()
        } else {
            retrofitCorrectionDetailFeedback(sessionId)
        }
    }

    private fun retrofitCorrectionDetailFeedback(sessionId: Long) {
        val token = DanzleSharedPreferences.getAccessToken() ?: ""
        val authHeader = "Bearer $token"

        val retrofit = RetrofitApi.getCorrectionDetailFeedbackInstance()
        retrofit.getCorrectionDetailFeedback(authHeader, sessionId)
            .enqueue(object : Callback<List<CorrectionFeedbackDetailResponse>> {
                override fun onResponse(
                    call: Call<List<CorrectionFeedbackDetailResponse>>,
                    response: Response<List<CorrectionFeedbackDetailResponse>>
                ) {
                    binding.fullscreenLoading.visibility = View.VISIBLE
                    binding.resultContentGroup.visibility = View.VISIBLE

                    if (response.isSuccessful) {
                        val allFeedback = response.body().orEmpty()
                        val adapter = CorrectionDetailFeedbackPagerAdapter(allFeedback)
                        binding.feedbackPager.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@CorrectionDetailFeedback,
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
                        this@CorrectionDetailFeedback,
                        "네트워크 에러: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}