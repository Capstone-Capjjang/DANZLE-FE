package com.example.danzle.myprofile.myVideo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MyVideoResponse
import com.example.danzle.databinding.ActivityCorrectionVideoRepositoryBinding
import com.example.danzle.enum.VideoMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CorrectionVideoRepository : AppCompatActivity() {

    private lateinit var binding: ActivityCorrectionVideoRepositoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCorrectionVideoRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 영상 recyclerview 서버에서 가져오기
        loadCorrectionVideos()

        // back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this@CorrectionVideoRepository, MyVideo::class.java))
        }

    }


    private fun setCorrectionAdapter(list: ArrayList<MyVideoResponse>) {
        val adapter = MyVideoRVAdapter(list)
        binding.correctionVideoRecyclerview.adapter = adapter
    }

    private fun loadCorrectionVideos() {
        val token = DanzleSharedPreferences.getAccessToken() ?: ""
        val authHeader = "Bearer $token"
        val userId = DanzleSharedPreferences.getUserId()

        if (token.isNullOrEmpty() || userId == null) {
            Toast.makeText(
                this@CorrectionVideoRepository,
                "You have to sign in.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Log.d("CorrectionVideoRepository", "CorrectionVideoRepository / Token: $token")

        val retrofit = RetrofitApi.getCorrectionVideoRepositoryInstance()
        retrofit.getCorrectionVideo(authHeader)
            .enqueue(object : Callback<List<MyVideoResponse>> {
                override fun onResponse(
                    call: Call<List<MyVideoResponse>>,
                    response: Response<List<MyVideoResponse>>
                ) {
                    if (response.isSuccessful) {
                        val accuracyList =
                            response.body()?.filter { it.mode == VideoMode.ACCURACY } ?: emptyList()
                        setCorrectionAdapter(ArrayList(accuracyList))
                        Log.d(
                            "CorrectionVideoRepository",
                            "CorrectionVideoRepository / Full Response Body: $accuracyList"
                        ) // 응답 전체 확인
                        Log.d("CorrectionVideoRepository", "CorrectionVideoRepository / Token: $token")
                    } else {
                        Log.d(
                            "CorrectionVideoRepository",
                            "CorrectionVideoRepository / Response Code: ${response.code()}"
                        )
                        val errorMsg = response.errorBody()?.string()
                        Log.e("CorrectionVideoRepository", "Error Body: $errorMsg")
                        Toast.makeText(
                            this@CorrectionVideoRepository,
                            "Fail to CorrectionVideoRepository: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<MyVideoResponse>>, t: Throwable) {
                    Log.d("CorrectionVideoRepository", "CorrectionVideoRepository / Error: ${t.message}")
                    Toast.makeText(this@CorrectionVideoRepository, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}