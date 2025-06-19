package com.example.danzle.practice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R

class MusicSelectPopup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_select_popup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // click <full> button -> FullPractice activity
        findViewById<Button>(R.id.full).apply {
            this.setOnClickListener {
                startActivity(
                    Intent(this@MusicSelectPopup, FullPractice::class.java)
                )
            }
        }

        // click <highlight> button -> Highlight activity
        findViewById<Button>(R.id.highlight).apply {
            this.setOnClickListener {
                Intent(this@MusicSelectPopup, HighlightPractice::class.java)
            }
        }

    }
}