package com.example.danzle.practice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.data.remote.response.auth.MusicSelectResponse
import com.example.danzle.databinding.ActivityPracticeFinishBinding
import com.example.danzle.enum.PracticeMode

class PracticeFinish : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeFinishBinding

    private val selectedSong: MusicSelectResponse? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("selected song", MusicSelectResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("selected song")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mode = intent.getStringExtra("mode")?.let { PracticeMode.valueOf(it) } ?: PracticeMode.FULL
        binding.titlePracticeFinish.text = when (mode) {
            PracticeMode.FULL -> "FULL"
            PracticeMode.HIGHLIGHT -> "HIGHLIGHT"
        }

        // getting some data from PracticeMusicSelect
        binding.songName.text = selectedSong?.title
        binding.artist.text = selectedSong?.artist

        Glide.with(binding.songImage.context)
            .load(selectedSong?.coverImagePath)
            .into(binding.songImage)

        binding.tryAgainButton.setOnClickListener {
            startActivity(Intent(this, PracticeMain::class.java))
            intent.putExtra("selected song", selectedSong)
            startActivity(intent)
            finish()
        }

        binding.othersongsButton.setOnClickListener {
            startActivity(Intent(this, PracticeMusicSelect::class.java))
        }

        binding.quitButton.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                // MainActivity 쪽에서 intent.getStringExtra("navigate_to")로 "practice" 값을 받을 수 있어요.
                //→ 이걸로 화면 분기나 특정 탭 이동 등을 할 수 있게 만듦
                putExtra("navigate_to", "practice")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                // FLAG_ACTIVITY_CLEAR_TOP
                // → MainActivity가 이미 백스택에 있으면, 그 위의 액티비티를 모두 제거하고 다시 포커스를 줌.
                // FLAG_ACTIVITY_SINGLE_TOP
                // → 만약 MainActivity가 맨 위에 있으면 새로 만들지 않고 onNewIntent()만 호출.
            }.also {
                startActivity(it)
                // 위에서 만든 Intent를 바로 startActivity()로 실행
            }
        }

        binding.backButton.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "practice")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also {
                startActivity(it)
            }
        }
    }
}