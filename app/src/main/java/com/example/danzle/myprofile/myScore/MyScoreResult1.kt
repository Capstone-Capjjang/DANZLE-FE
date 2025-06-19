package com.example.danzle.myprofile.myScore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.CorrectionResultResponse
import com.example.danzle.databinding.ActivityMyScoreResult1Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScoreResult1 : AppCompatActivity() {
    private lateinit var binding: ActivityMyScoreResult1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyScoreResult1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sessionId = intent.getLongExtra("sessionId", -1L)

        if (sessionId != -1L) {
            retrofitScoreResult(sessionId)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.detailText.setOnClickListener {
            val intent = Intent(this, MyScoreResult2::class.java)
            intent.putExtra("sessionId", sessionId)
            startActivity(intent)
        }

    }

    private fun retrofitScoreResult(sessionId: Long) {

        val token = DanzleSharedPreferences.getAccessToken()
        val authHeader = "Bearer $token"
        val retrofit = RetrofitApi.getCorrectionResultInstance()

        retrofit.getCorrectionResultService(authHeader, sessionId)
            .enqueue(object : Callback<CorrectionResultResponse> {
                override fun onResponse(
                    call: Call<CorrectionResultResponse>,
                    response: Response<CorrectionResultResponse>
                ) {

                    if (response.isSuccessful) {
                        val result = response.body() ?: return

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
                        Log.d("MyScoreResult1", "응답 성공: $result")
                        Log.d("MyScoreResult1", "점수 분포: perfect=$excellent, good=$good, normal=$normal, bad=$bad, miss=$miss, total=$total")


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

                    } else {
                    }
                }

                override fun onFailure(
                    call: Call<CorrectionResultResponse>,
                    t: Throwable
                ) {
                }
            }
            )
    }
}