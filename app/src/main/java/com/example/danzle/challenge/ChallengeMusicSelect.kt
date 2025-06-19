package com.example.danzle.challenge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.danzle.R
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MusicSelectResponse
import com.example.danzle.databinding.ActivityChallengeMusicSelectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeMusicSelect : AppCompatActivity(), ChallengeMusicSelectRVAdapter.ChallengeRecyclerViewEvent {

    private lateinit var binding: ActivityChallengeMusicSelectBinding
    private lateinit var musicList: ArrayList<MusicSelectResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChallengeMusicSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.songListRecyclerView.layoutManager = LinearLayoutManager(this)
        retrofitChallengeMusicSelect()
    }

    override fun onItemClick(position: Int) {
        val selectedSong = musicList[position]
        val intent = Intent(this@ChallengeMusicSelect, Challenge::class.java)
        intent.putExtra("selected song", selectedSong)
        startActivity(intent)
    }

    private fun setMusicRecyclerviewAdapter(list: ArrayList<MusicSelectResponse>) {
        val adapter = ChallengeMusicSelectRVAdapter(list, this)
        binding.songListRecyclerView.adapter = adapter
    }

    private fun retrofitChallengeMusicSelect() {
        val retrofit = RetrofitApi.getMusicSelectInstance()
        retrofit.getMusicSelect().enqueue(object : Callback<List<MusicSelectResponse>> {
            override fun onResponse(
                call: Call<List<MusicSelectResponse>>,
                response: Response<List<MusicSelectResponse>>
            ) {
                if (response.isSuccessful) {
                    musicList = ArrayList(response.body() ?: emptyList())
                    setMusicRecyclerviewAdapter(musicList)
                } else {
                    Toast.makeText(
                        this@ChallengeMusicSelect,
                        "Fail to load music: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<MusicSelectResponse>>, t: Throwable) {
                Toast.makeText(this@ChallengeMusicSelect, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}