package com.example.danzle.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R
import com.example.danzle.databinding.ActivityMusicSelectPopupBinding

class MusicSelectPopup : AppCompatActivity() {

    private lateinit var binding: ActivityMusicSelectPopupBinding

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

        // click <full> button -> FullPractice activity
        binding.full.setOnClickListener {
            startActivity(Intent(this@MusicSelectPopup, FullPractice::class.java))
        }

        // click <highlight> button -> Highlight activity
        binding.highlight.setOnClickListener {
            startActivity(Intent(this@MusicSelectPopup, HighlightPractice::class.java))
        }

    }
}