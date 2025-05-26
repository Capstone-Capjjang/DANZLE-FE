package com.example.danzle.correction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.CorrectionResultResponse
import com.example.danzle.databinding.ActivityCorrectionResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CorrectionResult : AppCompatActivity() {
    private lateinit var binding: ActivityCorrectionResultBinding

    private lateinit var token: String
    private lateinit var authHeader: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCorrectionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = DanzleSharedPreferences.getAccessToken() ?: ""
        authHeader = "Bearer $token"

        binding.cancelButton.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "correction")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also {
                startActivity(it)
            }
        }

        val sessionId = intent.getLongExtra("sessionId", -1L)

        if (sessionId != -1L) {
            retrofitCorrectionResult(authHeader, sessionId)
        }

        binding.feedback.setOnClickListener {
            val intent = Intent(this, CorrectionDetailFeedback::class.java)
            intent.putExtra("sessionId", sessionId)
            startActivity(intent)
        }
    }

    private fun retrofitCorrectionResult(authHeader: String, sessionId: Long) {

        //binding.progressBar.visibility = View.VISIBLE
        //binding.resultContentGroup.visibility = View.GONE

        val retrofit = RetrofitApi.getCorrectionResultInstance()

        retrofit.getCorrectionResultService(authHeader, sessionId)
            .enqueue(object : Callback<CorrectionResultResponse> {
                override fun onResponse(
                    call: Call<CorrectionResultResponse>,
                    response: Response<CorrectionResultResponse>
                ) {
//                    binding.progressBar.visibility = View.GONE
//                    binding.resultContentGroup.visibility = View.VISIBLE

                    if (response.isSuccessful) {
                        val result = response.body() ?: return
                        Log.d("CorrectionResult", "Response success: $result")

                        val excellent = result.excellent
                        val good = result.good
                        val normal = result.normal
                        val bad = result.bad
                        val miss = result.miss

                        binding.countExcellent.text = excellent.toString()
                        binding.countGood.text = good.toString()
                        binding.countNormal.text = normal.toString()
                        binding.countBad.text = bad.toString()
                        binding.countMiss.text = miss.toString()

                        val total = excellent + good + normal + bad + miss
                        val maxBarWidthDp = 100

                        if (total > 0) {
                            binding.progressExcellent.max = total
                            binding.progressExcellent.progress = excellent

                            binding.progressGood.max = total
                            binding.progressGood.progress = good

                            binding.progressNormal.max = total
                            binding.progressNormal.progress = normal

                            binding.progressBad.max = total
                            binding.progressBad.progress = bad

                            binding.progressMiss.max = total
                            binding.progressMiss.progress = miss
                        }

                        if (result == null) {
                            Log.e("CorrectionResult", "Response body is null")
                            return
                        }

                        val level = result.resultLevel ?: "Unknown"

                        // ContextCompat.getColor()는 버전 상관없이 색상을 안전하게 적용해준다.
                        when (level) {
                            "Excellent" -> {
                                binding.badgeResultText.text = "Excellent"
                                binding.badgeResultText.background.setTint(
                                    ContextCompat.getColor(
                                        this@CorrectionResult,
                                        R.color.scorePerfectBackground
                                    )
                                )
                                binding.badgeResultText.setTextColor(
                                    ContextCompat.getColor(
                                        this@CorrectionResult, R.color.scorePerfectText
                                    )
                                )
                            }

                            "Good" -> {
                                binding.badgeResultText.text = "Good"
                                binding.badgeResultText.background.setTint(
                                    ContextCompat.getColor(
                                        this@CorrectionResult,
                                        R.color.scoreGoodBackground
                                    )
                                )
                                binding.badgeResultText.setTextColor(
                                    ContextCompat.getColor(
                                        this@CorrectionResult, R.color.scoreGoodText
                                    )
                                )
                            }

                            "Normal" -> {
                                binding.badgeResultText.text = "Normal"
                                binding.badgeResultText.background.setTint(
                                    ContextCompat.getColor(
                                        this@CorrectionResult,
                                        R.color.scoreNormalBackground
                                    )
                                )
                                binding.badgeResultText.setTextColor(
                                    ContextCompat.getColor(
                                        this@CorrectionResult, R.color.scoreNormalText
                                    )
                                )
                            }

                            "Bad" -> {
                                binding.badgeResultText.text = "Bad"
                                binding.badgeResultText.background.setTint(
                                    ContextCompat.getColor(
                                        this@CorrectionResult,
                                        R.color.scoreBadBackground
                                    )
                                )
                                binding.badgeResultText.setTextColor(
                                    ContextCompat.getColor(
                                        this@CorrectionResult, R.color.scoreBadText
                                    )
                                )
                            }

                            else -> {
                                binding.badgeResultText.text = "Miss"
                                binding.badgeResultText.background.setTint(
                                    ContextCompat.getColor(
                                        this@CorrectionResult,
                                        R.color.scoreMissBackground
                                    )
                                )
                                binding.badgeResultText.setTextColor(
                                    ContextCompat.getColor(
                                        this@CorrectionResult, R.color.grayText
                                    )
                                )
                            }
                        }

                    } else {
                        Log.e("CorrectionResult", "Response failed with code: ${response.code()}")
                        Log.e("CorrectionResult", "Error body: ${response.errorBody()?.string()}")
                    }

                }

                override fun onFailure(
                    call: Call<CorrectionResultResponse>,
                    t: Throwable
                ) {
                    // binding.progressBar.visibility = View.GONE
                    Log.e("CorrectionResult", "Network failure: ${t.message}")
                }
            }
            )

    }
}