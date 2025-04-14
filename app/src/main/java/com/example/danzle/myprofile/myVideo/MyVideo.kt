package com.example.danzle.myprofile.myVideo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.MyProfileFragment
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MyVideoResponse
import com.example.danzle.data.remote.response.auth.VideoMode
import com.example.danzle.databinding.ActivityMyVideoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyVideo : AppCompatActivity() {

    private lateinit var binding: ActivityMyVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // changing activity
        binding.practiceMore.setOnClickListener {
            startActivity(Intent(this@MyVideo, PracticeVideoRepository::class.java))
        }
        binding.challengeMore.setOnClickListener {
            startActivity(Intent(this@MyVideo, ChallengeVideoRepository::class.java))
        }

        binding.vector.setOnClickListener {
            finish()
        }

        Log.d("MyVideo", "onCreate 실행됨")
        retrofitMyVideo()

    }

    // recyclerview와 MyVideoRVAdapter 연결
    private fun setPracticeAdapter(list: ArrayList<MyVideoResponse>) {
        val adapter = MyVideoRVAdapter(list)
        binding.practiceVideoRecyclerview.adapter = adapter
    }

    private fun setChallengeAdapter(list: ArrayList<MyVideoResponse>) {
        val adapter = MyVideoRVAdapter(list)
        binding.challengeVideoRecyclerview.adapter = adapter
    }

    //about retrofit
    private fun retrofitMyVideo() {
        val token = DanzleSharedPreferences.getAccessToken()
        val authHeader = "Bearer $token"
        val userId = DanzleSharedPreferences.getUserId()

        Log.d("MyVideo", "토큰: $token / 유저 ID: $userId")

        if (token.isNullOrEmpty() || userId == null) {
            // Fragment는 requireContext, Activity는 this
            Toast.makeText(this@MyVideo, "You have to sign in.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getMyVideoInstance()
        retrofit.getMyVideo(authHeader, userId)
            .enqueue(object : Callback<List<MyVideoResponse>> {
                override fun onResponse(
                    call: Call<List<MyVideoResponse>>,
                    response: Response<List<MyVideoResponse>>
                ) {
                    if (response.isSuccessful) {
                        val myVideoList = response.body() ?: emptyList()
                        Log.d("MyVideo", "MyVideo / Full Response Body: $myVideoList") // 응답 전체 확인
                        Log.d("MyVideo", "MyVideo / Token: $token")

                        // separate data
                        // enum class로 선언되어 있어서 아래와 같이 VideoMode.PRACTICE로 불러와야 된다.
                        val practiceList = myVideoList.filter { it.mode == VideoMode.PRACTICE }
                        val challengeList = myVideoList.filter { it.mode == VideoMode.CHALLENGE }

                        // connecting to adapter
                        setPracticeAdapter(ArrayList(practiceList))
                        setChallengeAdapter(ArrayList(challengeList))

                    } else {
                        Log.d("MyVideo", "MyVideo / Response Code: ${response.code()}")
                        Toast.makeText(
                            this@MyVideo,
                            "Fail to MyVideo: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<MyVideoResponse>>, t: Throwable) {
                    Log.d("MyVideo", "MyVideo / Error: ${t.message}")
                    Toast.makeText(this@MyVideo, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}