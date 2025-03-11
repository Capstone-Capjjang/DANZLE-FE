package com.example.danzle.myprofile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.R
import com.example.danzle.databinding.ActivityMyProfileMainBinding
import com.example.danzle.myprofile.editProfile.EditProfile
import com.example.danzle.myprofile.infoApp.InfoApp
import com.example.danzle.myprofile.logout.FragmentLogout
import com.example.danzle.myprofile.myScore.MyScore
import com.example.danzle.myprofile.myVideo.MyVideo


class MyProfileMain : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyProfileMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // converting screen when clicking
        // editProfile
        binding.editProfile.setOnClickListener {
            startActivity(Intent(this@MyProfileMain, EditProfile::class.java))
        }

        // myVideo
        binding.myVideo.setOnClickListener {
            startActivity(Intent(this@MyProfileMain, MyVideo::class.java))
        }

        // myScore
        binding.myScore.setOnClickListener {
            startActivity(Intent(this@MyProfileMain, MyScore::class.java))
        }

        // infoApp
//        binding.infoApp.setOnClickListener { view ->
//            // logout -> call the dialog
//            val layoutInflater = LayoutInflater.from()
//            val view = layoutInflater.inflate(R.layout.cancel_dialog, null)
//
//            val builder: AlertDialog.Builder = AlertDialog.Builder()
//            builder
//                .setView(view)
//                .create()
//
//        }

        binding.logout.setOnClickListener {
            startActivity(Intent(this@MyProfileMain, FragmentLogout::class.java))
        }


        //
    }
}