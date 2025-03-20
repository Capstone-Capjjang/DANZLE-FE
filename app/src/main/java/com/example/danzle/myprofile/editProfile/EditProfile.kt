package com.example.danzle.myprofile.editProfile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R
import com.example.danzle.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    var username: String = ""
    var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // converting screen when clicking
        //username
        binding.username.setOnClickListener {startActivity(Intent(this@EditProfile, ChangeUsername::class.java))}

        // change password
        binding.changePassword.setOnClickListener {startActivity(Intent(this@EditProfile, ChangePassword::class.java))}

        // delete account
        // click button -> delete account -> (ok) start first
        binding.delteAccount.setOnClickListener {
            startActivity(Intent(this@EditProfile, DeleteAccount::class.java))
        }



    }

    private fun retrofitEditProfile(){

    }
}