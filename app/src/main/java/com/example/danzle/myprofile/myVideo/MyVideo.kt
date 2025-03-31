package com.example.danzle.myprofile.myVideo

import android.content.Context
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
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.request.auth.SignInRequest
import com.example.danzle.data.remote.response.auth.MyVideoResponse
import com.example.danzle.data.remote.response.auth.SignInResponse
import com.example.danzle.databinding.ActivityMyVideoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val token: String = ""

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
            startActivity(Intent(this@MyVideo,PracticeVideoRepository::class.java))
        }
        binding.challengeMore.setOnClickListener {
            startActivity(Intent(this@MyVideo, ChallengeVideoRepository::class.java))
        }



    }


    private fun setAdapter(list: ArrayList<MyVideoResponse>){
        val mAdapter = MyVideoRVAdapter(list)

       

    }


    // about retrofit
//    private fun retrofitMyVide(userInfo: SignInRequest, context: Context){
//        val retrofit = RetrofitApi.getMyVideoInstance()
//        retrofit.getMyVideo(userInfo)
//            .enqueue(object : Callback<List<MyVideoResponse>> {
//                override fun onResponse(call: Call<List<MyVideoResponse>>, response: Response<List<MyVideoResponse>>) {
//                    if (response.isSuccessful) {
//                        val myVideoResponse = response.body()
//                        Log.d("Debug", "MyVideo / Full Response Body: $myVideoResponse") // 응답 전체 확인
//
//                        val token = myVideoResponse?.accessToken ?: ""
//                        Log.d("Debug", "MyVideo / Token: $token")
//
//                        // 로그인 성공 후 MainActivity로 이동
//                        val intent = Intent(context, MyProfileFragment::class.java)
//                        intent.putExtra("Token", token)
//                        context.startActivity(intent)
//                    } else {
//                        Log.d("Debug", "MyVideo / Response Code: ${response.code()}")
//                        Toast.makeText(context, "Fail to MyVideo: ${response.message()}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<List<MyVideoResponse>>, t: Throwable) {
//                    Log.d("Debug", "MyVideo / Error: ${t.message}")
//                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
}