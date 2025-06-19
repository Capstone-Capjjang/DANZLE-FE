package com.example.danzle.startPage

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.databinding.ActivityStartFirstBinding

class FirstStart : AppCompatActivity() {
    private lateinit var binding: ActivityStartFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView

            if (iconView != null) {
                // 초기 스케일 그대로(1f)로 두고 애니메이션 시작
                iconView.scaleX = 1f
                iconView.scaleY = 1f

                // 축소(1f→0f) + 투명도(1f→0f) 애니메이션
                val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
                val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
                val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)

                ObjectAnimator.ofPropertyValuesHolder(iconView, scaleX, scaleY, alpha).apply {
                    duration = 800L
                    interpolator = android.view.animation.DecelerateInterpolator()
                    doOnEnd {
                        splashScreenView.remove()
                    }
                    start()
                }
            } else {
                splashScreenView.remove()
            }
        }

        binding = ActivityStartFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // convert to Signin activity
        binding.signinButton.setOnClickListener {
            startActivity(Intent(this@FirstStart, SignIn::class.java))
        }

        binding.createAccountButton.setOnClickListener {
            startActivity(Intent(this@FirstStart, CreateAccount::class.java))
        }
    }
}