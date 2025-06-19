package com.example.danzle.myprofile.myScore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MyScoreResponse
import com.example.danzle.databinding.ActivityMyScoreBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScore : AppCompatActivity(),
    MyScoreRVAdapter.RecyclerViewEvent {
    private lateinit var binding: ActivityMyScoreBinding
    private lateinit var scoreList: ArrayList<MyScoreResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        retrofitMyScore()

        binding.scoreRecyclerview.layoutManager = LinearLayoutManager(this)

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setScoreRecyclerviewAdapter(list: ArrayList<MyScoreResponse>) {
        val adapter = MyScoreRVAdapter(list, this)
        binding.scoreRecyclerview.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        val selectedScore = scoreList[position]
//        val intent = Intent(this@MyScore, MyScoreResult1::class.java)
//        intent.putExtra("selectedScore Id", selectedScore.id)
//        startActivity(intent)
    }


    private fun retrofitMyScore() {
        val retrofit = RetrofitApi.getMyScoreInstance()

        val token = DanzleSharedPreferences.getAccessToken() ?: ""
        val authHeader = "Bearer $token"

        retrofit.getMyScore(authHeader)
            .enqueue(object : Callback<List<MyScoreResponse>> {
                override fun onResponse(
                    call: Call<List<MyScoreResponse>?>,
                    response: Response<List<MyScoreResponse>?>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body() ?: return
                        Log.d("MyScore", "서버 응답 성공: $result")

                        val filtered = result.filterNot {
                            it.song.title.isNullOrBlank() &&
                                    it.song.coverImagePath.isNullOrBlank() &&
                                    it.song.artist.isNullOrBlank() &&
                                    // it.song.isNullOrBlank() &&
                                    it.avgScore == 0.0
                        }

                        if (filtered.isNotEmpty()) {
                            scoreList = ArrayList(response.body() ?: emptyList())
                            setScoreRecyclerviewAdapter(scoreList)
                            binding.scoreRecyclerview.visibility = View.VISIBLE
                        } else {
                            binding.scoreRecyclerview.adapter = null
                            binding.scoreRecyclerview.visibility = View.GONE
                        }
                    } else {
                    }
                }

                override fun onFailure(
                    call: Call<List<MyScoreResponse>?>,
                    t: Throwable
                ) {
                }
            })
    }
}