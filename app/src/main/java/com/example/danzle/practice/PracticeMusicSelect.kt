package com.example.danzle.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.danzle.R
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.PracticeMusicSelectResponse
import com.example.danzle.databinding.ActivityPracticeMusicSelectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PracticeMusicSelect : AppCompatActivity(), PracticeMusicSelectRVAdapter.RecyclerViewEvent {

    private lateinit var binding: ActivityPracticeMusicSelectBinding
    private lateinit var musicList: ArrayList<PracticeMusicSelectResponse>

    var songs: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding =
            ActivityPracticeMusicSelectBinding.inflate(LayoutInflater.from(this@PracticeMusicSelect))
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.backButton.setOnClickListener {
            finish()
        }

        retrofitPracticeMusicSelect()

        // 리사이클러뷰에 레이아웃 매니저 장착
        // 레이아웃의 배치
        binding.songListRecyclerView.layoutManager = LinearLayoutManager(this)

//        binding.searchSong.doAfterTextChanged {
//            songs = it.toString()
//        }
    }


    override fun onItemClick(position: Int) {

        val selectedSong = musicList[position]
        val intent = Intent(this, PracticeMusicSelectPopup::class.java)
//        Log.d("MusicSelect", "전달할 데이터: $selectedSong")
        intent.putExtra("selected song", selectedSong)
        // intent.putExtra("songID", selectedSong.)
        // id 받아오기

        startActivity(intent)

    }

    // recyclerview adapter
    private fun setMusicRecyclerviewAdapter(list: ArrayList<PracticeMusicSelectResponse>) {
        val adapter = PracticeMusicSelectRVAdapter(list, this)
        binding.songListRecyclerView.adapter = adapter
    }

    // about retrofit
    private fun retrofitPracticeMusicSelect() {
        val retrofit = RetrofitApi.getPracticeMusicSelectInstance()
        retrofit.getPracticeMusicSelect()
            .enqueue(object : Callback<List<PracticeMusicSelectResponse>> {
                override fun onResponse(
                    call: Call<List<PracticeMusicSelectResponse>>,
                    response: Response<List<PracticeMusicSelectResponse>>
                ) {
                    if (response.isSuccessful) {
                        musicList = ArrayList(response.body() ?: emptyList())
//                        Log.d("MusicSelect", "MusicSelect / Full Response Body: $musicList")

                        // recyclerview에 어댑터 장착
                        setMusicRecyclerviewAdapter(musicList)

                    } else {
//                        Log.d("MusicSelect", "MusicSelect / Response Code ${response.code()}")
                        Toast.makeText(
                            this@PracticeMusicSelect,
                            "Fail to MusicSelect: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<PracticeMusicSelectResponse>>,
                    t: Throwable
                ) {
//                    Log.d("MusicSelect", "MusicSelect / Error: ${t.message}")
                    Toast.makeText(this@PracticeMusicSelect, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

}