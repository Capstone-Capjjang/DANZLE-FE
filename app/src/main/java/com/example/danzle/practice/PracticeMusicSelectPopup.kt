package com.example.danzle.practice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.danzle.R
import com.example.danzle.data.remote.response.auth.PracticeMusicSelectResponse
import com.example.danzle.databinding.ActivityMusicSelectPopupBinding

class PracticeMusicSelectPopup : AppCompatActivity() {

    private lateinit var binding: ActivityMusicSelectPopupBinding

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
        binding = ActivityMusicSelectPopupBinding.inflate(LayoutInflater.from(this))
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


        // click <full> button -> FullPractice activity
        binding.full.setOnClickListener {
            val intent = Intent(this@PracticeMusicSelectPopup, FullPractice::class.java)
            intent.putExtra("selected song", selectedSong)
            startActivity(intent)
        }

        // click <highlight> button -> Highlight activity
        binding.highlight.setOnClickListener {
            val intent = Intent(this@PracticeMusicSelectPopup, HighlightPractice::class.java)
            intent.putExtra("selected song", selectedSong)
            startActivity(intent)
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PracticeMusicSelectPopup, PracticeMusicSelect::class.java))
        }
    }
}