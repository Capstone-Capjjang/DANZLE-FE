package com.example.danzle.challenge

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.danzle.R
import com.example.danzle.databinding.ActivityChallengeBinding

class Challenge : AppCompatActivity() {
    private lateinit var binding: ActivityChallengeBinding
    lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // ExoPlayer 초기화
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
        binding.playerView.alpha = 0.3f
        binding.playerView.bringToFront()

        // 리소스 URI로부터 MediaItem 생성
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.challenge}")
        val mediaItem = MediaItem.fromUri(videoUri)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}