package com.example.danzle.practice

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.exoplayer.ExoPlayer
import com.example.danzle.R
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.request.auth.HighlightPracticeRequest
import com.example.danzle.data.remote.response.auth.HighlightPracticeResponse
import com.example.danzle.databinding.ActivityHighlightPracticeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val token: String = ""

class HighlightPractice : AppCompatActivity() {

    var startTime: String = ""
    var endTime: String = ""
    var timestamp: String = ""

    private lateinit var binding: ActivityHighlightPracticeBinding

    lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHighlightPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initialize exoplayer
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player



    }

    // about retrofit
//    private fun retrofitHighlightPractice(songInfo: HighlightPracticeRequest,context: Context){
//        val retrofit = RetrofitApi.getHighlightPracticeInstance()
//        retrofit.getHighlightPractice(songInfo)
//            .enqueue(object : Callback<HighlightPracticeResponse>{
//                override fun onResponse(call: Call<HighlightPracticeResponse>, response: Response<HighlightPracticeResponse>) {
//                    if (response.isSuccessful){
//                        val highlightPracticeResponse = response.body()
//                        highlightPracticeResponse?.let { data ->
//                            // data means response.body
//                            startTime = data.startTime
//                            endTime = data.endTime
//                            timestamp = data.timestamp
//
//                            Log.d("Degug","Start: $startTime, End: $endTime")
//                            playVideo(startTime, endTime)
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<HighlightPracticeResponse>, t: Throwable) {
//                    Log.d("Debug", "HighlightPractice / Error: ${t.message}")
//                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun playVideo(startTime: String, endTime: String) {
//
//    }
}