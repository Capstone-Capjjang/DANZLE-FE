package com.example.danzle.practice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.danzle.MainPracticeFragment
import com.example.danzle.R
import com.example.danzle.data.remote.response.auth.PracticeMusicSelectResponse
import com.example.danzle.databinding.ActivityHighlightPracticeFinishBinding

class HighlightPracticeFinish : AppCompatActivity() {

    private lateinit var binding: ActivityHighlightPracticeFinishBinding

    private val selectedSong: PracticeMusicSelectResponse? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("selected song", PracticeMusicSelectResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("selected song")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHighlightPracticeFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // getting some data from PracticeMusicSelect
        binding.songName.text = selectedSong?.title
        binding.artist.text = selectedSong?.artist
        Glide.with(binding.songImage.context)
            .load(selectedSong?.coverImagePath)
            .into(binding.songImage)

        binding.tryagainButton.setOnClickListener {
            startActivity(Intent(this, HighlightPractice::class.java))
        }

        binding.othersongsButton.setOnClickListener{
            startActivity(Intent(this, PracticeMusicSelect::class.java))
        }

        binding.quitButton.setOnClickListener{
            startActivity(Intent(this, MainPracticeFragment::class.java))
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, MainPracticeFragment::class.java))
        }
    }
}