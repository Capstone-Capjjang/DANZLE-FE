package com.example.danzle.practice

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.MainPracticeFragment
import com.example.danzle.R
import com.example.danzle.databinding.ActivityHighlightFinishBinding

class HighlightFinish : AppCompatActivity() {

    private lateinit var binding: ActivityHighlightFinishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHighlightFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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