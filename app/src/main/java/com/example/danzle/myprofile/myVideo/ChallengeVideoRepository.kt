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
import com.example.danzle.databinding.ActivityChallengeVideoRepositoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeVideoRepository : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeVideoRepositoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChallengeVideoRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 영상 recyclerview 서버에서 가져오기
        loadChallengeVideos()

        // back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this@ChallengeVideoRepository, MyVideo::class.java))
        }

    }


    private fun setChallengeAdapter(list: ArrayList<MyVideoResponse>) {
        val adapter = MyVideoRVAdapter(list)
        binding.challengeVideoRecyclerview.adapter = adapter
    }

    private fun loadChallengeVideos() {
        val token = DanzleSharedPreferences.getAccessToken()
        val userId = DanzleSharedPreferences.getUserId()

        if (token.isNullOrEmpty() || userId == null){
            Toast.makeText(this@ChallengeVideoRepository, "You have to sign in.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getChallengeVideoRepositoryInstance()
        retrofit.getChallengeVideo(token, userId)
            .enqueue(object : Callback<List<MyVideoResponse>>{
                override fun onResponse(call: Call<List<MyVideoResponse>>, response: Response<List<MyVideoResponse>>) {
                    if (response.isSuccessful){
                        val challengeList = response.body()?.filter { it.mode == VideoMode.CHALLENGE }
                        setChallengeAdapter(ArrayList(challengeList))
                        Log.d(
                            "Debug",
                            "PracticeVideoRepository / Full Response Body: $challengeList"
                        ) // 응답 전체 확인
                        Log.d("Debug", "PracticeVideoRepository / Token: $token")
                    } else{
                        Log.d(
                            "Debug",
                            "PracticeVideoRepository / Response Code: ${response.code()}"
                        )
                        Toast.makeText(
                            this@ChallengeVideoRepository,
                            "Fail to PracticeVideoRepository: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<MyVideoResponse>>, t: Throwable) {
                    Log.d("Debug", "ChallengeVideoRepository / Error: ${t.message}")
                    Toast.makeText(this@ChallengeVideoRepository, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}