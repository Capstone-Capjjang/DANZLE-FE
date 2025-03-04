package com.example.danzle.practice

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.R
import com.example.danzle.databinding.ActivityPracticeMusicSelectBinding
import java.util.zip.Inflater

class PracticeMusicSelect : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeMusicSelectBinding

    var songs: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeMusicSelectBinding.inflate(LayoutInflater.from(this@PracticeMusicSelect))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        binding.searchSong.doAfterTextChanged {
//            songs = it.toString()
//        }

    }
}