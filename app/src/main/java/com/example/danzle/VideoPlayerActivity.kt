package com.example.danzle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : AppCompatActivity() {

//    private lateinit var player: ExoPlayer
    private lateinit var binding: ActivityVideoPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val videoURL = intent.getStringExtra("videoPath") ?: return

        // ExoPlayer
//        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
//            binding.videoView.player = exoPlayer
//            val mediaItem = MediaItem.fromUri(videoURL)
//            exoPlayer.setMediaItem(mediaItem)
//            exoPlayer.prepare()
//            exoPlayer.play()
//        }

    }

//    override fun onStop() {
//        super.onStop()
//        player.release()
//    }

}
