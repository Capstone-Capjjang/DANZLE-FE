package com.example.danzle.correction

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R
import com.example.danzle.databinding.ActivityCorrectioinResultBinding

class CorrectionResult : AppCompatActivity() {
    private lateinit var binding: ActivityCorrectioinResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCorrectioinResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sessionID = intent.getLongExtra("sessionId", -1L)

//        binding.moreFeedbackButton.setOnClickListener {
//            val intent = Intent(this, CorrectionDetailFeedback::class.java)
//            intent.putExtra("sessionId", sessionID)
//            startActivity(intent)
//        }


    }
}