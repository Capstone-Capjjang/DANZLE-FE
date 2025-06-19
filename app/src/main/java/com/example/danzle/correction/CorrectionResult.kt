package com.example.danzle.correction

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
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


        binding.cancelButton.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "correction")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also {
                startActivity(it)
            }
        }

        binding.fullscreenLoading.visibility = View.VISIBLE
        binding.resultContentGroup.visibility = View.GONE


        val sessionId = intent.getLongExtra("sessionId", -1L)

        if (sessionId != -1L) {
            retrofitCorrectionResult(sessionId)
        }

        binding.feedback.setOnClickListener {
            val intent = Intent(this, CorrectionDetailFeedback::class.java)
            intent.putExtra("sessionId", sessionId)
            startActivity(intent)
        }

        binding.tyeAgainButton.setOnClickListener {
            startActivity(Intent(this@CorrectionResult, Correction::class.java))
        }

        binding.otherSongsButton.setOnClickListener {
            startActivity(Intent(this@CorrectionResult, CorrectionMusicSelect::class.java))
        }

    }

    private fun retrofitCorrectionResult(sessionId: Long) {

        binding.progressBar.visibility = View.GONE
        binding.resultContentGroup.visibility = View.VISIBLE

        val token = DanzleSharedPreferences.getAccessToken()
        val authHeader = "Bearer $token"
        val retrofit = RetrofitApi.getCorrectionResultInstance()

        retrofit.getCorrectionResultService(authHeader, sessionId)
            .enqueue(object : Callback<CorrectionResultResponse> {
                override fun onResponse(
                    call: Call<CorrectionResultResponse>,
                    response: Response<CorrectionResultResponse>
                ) {
                    // 서버 응답이 도착하면 로딩 숨기기
                    binding.fullscreenLoading.visibility = View.VISIBLE
                    binding.resultContentGroup.visibility = View.VISIBLE

                    if (response.isSuccessful) {
                        val result = response.body() ?: return

                        Glide.with(this@CorrectionResult)
                            .load(result.song.coverImagePath)
                            .into(binding.songImage)
                        binding.songTitle.text = result.song.title
                        binding.singer.text = result.song.artist

                        val excellent = result.perfect
                        val good = result.good
                        val normal = result.normal
                        val bad = result.bad
                        val miss = result.miss

                        binding.countPerfect.text = excellent.toString()
                        binding.countGood.text = good.toString()
                        binding.countNormal.text = normal.toString()
                        binding.countBad.text = bad.toString()
                        binding.countMiss.text = miss.toString()

                        val total = excellent + good + normal + bad + miss

                        if (total > 0) {
                            binding.progressPerfect.max = total
                            binding.progressPerfect.progress = excellent

                            binding.progressGood.max = total
                            binding.progressGood.progress = good

                            binding.progressNormal.max = total
                            binding.progressNormal.progress = normal

                            binding.progressBad.max = total
                            binding.progressBad.progress = bad

                            binding.progressMiss.max = total
                            binding.progressMiss.progress = miss
                        }

                        val level = result.resultLevel ?: "Unknown"

                        // ContextCompat.getColor()는 버전 상관없이 색상을 안전하게 적용해준다.
                        when (level) {
                            "Perfect" -> {
                                binding.badgeResultText.text = "Perfect"
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
                        Toast.makeText(
                            this@CorrectionResult,
                            "서버에서 결과를 받아오지 못했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<CorrectionResultResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@CorrectionResult,
                        "네트워크 오류로 결과를 가져올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            )
    }
}