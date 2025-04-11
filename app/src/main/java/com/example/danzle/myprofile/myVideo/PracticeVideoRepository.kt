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
import com.example.danzle.data.remote.response.auth.VideoMode
import com.example.danzle.databinding.ActivityPracticeVideoRepositoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PracticeVideoRepository : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeVideoRepositoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeVideoRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 영상 recyclerview 서버에서 가져오기
        loadPracticeVideos()

        // back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this@PracticeVideoRepository, MyVideo::class.java))
        }
    }


    private fun setPracticeAdapter(list: ArrayList<MyVideoResponse>) {
        val adapter = MyVideoRVAdapter(list)
        binding.practiceVideoRecyclerview.adapter = adapter
    }

    private fun loadPracticeVideos() {
        val token = DanzleSharedPreferences.getAccessToken()
        val userId = DanzleSharedPreferences.getUserId()

        if (token.isNullOrEmpty() || userId == null) {
            Toast.makeText(this@PracticeVideoRepository, "You have to sign in.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getPracticeVideoRepositoryInstance()
        retrofit.getPracticeVideo(token, userId)
            .enqueue(object : Callback<List<MyVideoResponse>>{
                override fun onResponse(call: Call<List<MyVideoResponse>>, response: Response<List<MyVideoResponse>>) {
                    if (response.isSuccessful) {
                        val practiceList =
                            response.body()?.filter { it.mode == VideoMode.PRACTICE } ?: emptyList()
                        setPracticeAdapter(ArrayList(practiceList))
                        Log.d(
                            "Debug",
                            "PracticeVideoRepository / Full Response Body: $practiceList"
                        ) // 응답 전체 확인
                        Log.d("Debug", "PracticeVideoRepository / Token: $token")
                    } else {
                        Log.d(
                            "Debug",
                            "PracticeVideoRepository / Response Code: ${response.code()}"
                        )
                        Toast.makeText(
                            this@PracticeVideoRepository,
                            "Fail to PracticeVideoRepository: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<List<MyVideoResponse>>, t: Throwable) {
                    Log.d("Debug", "PracticeVideoRepository / Error: ${t.message}")
                    Toast.makeText(this@PracticeVideoRepository, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
                }
            )

    }
}