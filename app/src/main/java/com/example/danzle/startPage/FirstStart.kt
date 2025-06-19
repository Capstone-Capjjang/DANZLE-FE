package com.example.danzle.startPage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.MainActivity
import com.example.danzle.R

class FirstStart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_first)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // convert to Signin activity
        findViewById<Button>(R.id.signin).apply {
            this.setOnClickListener {
                startActivity(
                    Intent(this@FirstStart, SignIn::class.java)
                )
            }
        }

        // convert to CreateAccount activity
        findViewById<Button>(R.id.createAccount).apply {
            this.setOnClickListener {
                startActivity(
                    Intent(this@FirstStart, CreateAccount::class.java)
                )
            }
        }

        // convert to Main activity
        // the situation using application without signin
        findViewById<Button>(R.id.notSignin).apply {
            this.setOnClickListener {
                startActivity(
                    Intent(this@FirstStart, MainActivity::class.java)
                )
            }
        }

    }
}