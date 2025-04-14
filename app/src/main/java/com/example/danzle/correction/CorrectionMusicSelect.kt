package com.example.danzle.correction

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
import com.example.danzle.data.remote.response.auth.CorrectionMusicSelectResponse
import com.example.danzle.databinding.ActivityCorrectionMusicSelectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CorrectionMusicSelect : AppCompatActivity(),
    CorrectionMusicSelectRVAdapter.RecyclerViewEvent {
    private lateinit var binding: ActivityCorrectionMusicSelectBinding
    private lateinit var musicList: ArrayList<CorrectionMusicSelectResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCorrectionMusicSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        retrofitCorrectionMusicSelect()

        binding.songListRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onItemClick(position: Int) {
        val selectedSong = musicList[position]
        val intent = Intent(this@CorrectionMusicSelect, Correction::class.java)
        intent.putExtra("selected song", selectedSong)

        startActivity(intent)
    }

    private fun setMusicRecyclerviewAdapter(list: ArrayList<CorrectionMusicSelectResponse>) {
        val adapter = CorrectionMusicSelectRVAdapter(list, this)
        binding.songListRecyclerView.adapter = adapter
    }

    fun retrofitCorrectionMusicSelect() {
        val retrofit = RetrofitApi.getCorrectionMusicSelectInstance()
        retrofit.getCorrectionMusicSelect()
            .enqueue(object : Callback<List<CorrectionMusicSelectResponse>> {
                override fun onResponse(
                    call: Call<List<CorrectionMusicSelectResponse>>,
                    response: Response<List<CorrectionMusicSelectResponse>>
                ) {
                    if (response.isSuccessful) {
                        musicList = ArrayList(response.body() ?: emptyList())

                        setMusicRecyclerviewAdapter(musicList)
                    } else {
                        Toast.makeText(
                            this@CorrectionMusicSelect,
                            "Fail to MusicSelect: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<CorrectionMusicSelectResponse>>,
                    p1: Throwable
                ) {
                    Toast.makeText(this@CorrectionMusicSelect, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}