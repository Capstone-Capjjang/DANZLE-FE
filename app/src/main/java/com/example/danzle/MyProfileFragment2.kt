package com.example.danzle

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MyProfileResponse
import com.example.danzle.databinding.FragmentMyProfileBinding
import com.example.danzle.myprofile.editProfile.EditProfile
import com.example.danzle.myprofile.myScore.MyScore
import com.example.danzle.myprofile.myVideo.MyVideo
import com.example.danzle.startPage.FirstStart
import com.example.danzle.startPage.token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyProfileFragment2 : AppCompatActivity() {

    private lateinit var binding: FragmentMyProfileBinding

    var username: String = ""
    var email: String = ""
    var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = FragmentMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // converting screen when clicking
        // editProfile
        binding.editProfile.setOnClickListener { startActivity(Intent(this@MyProfileFragment2, EditProfile::class.java)) }

        // myVideo
        binding.myVideo.setOnClickListener { startActivity(Intent(this@MyProfileFragment2, MyVideo::class.java)) }

        // myScore
        binding.myScore.setOnClickListener { startActivity(Intent(this@MyProfileFragment2, MyScore::class.java)) }

        // logout
        // click button -> logout dialog -> (ok) start first
        binding.logout.setOnClickListener {
            showLogoutDialog()
        }


    }

    private fun showLogoutDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.logout_dialog, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        alertDialog.setCancelable(false)
        alertDialog.window!!.attributes.windowAnimations = R.style.dialogAniamtion
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        alertDialog.window!!.setGravity(Gravity.BOTTOM)

        // click cancelButton
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        // click logoutButton
        logoutButton.setOnClickListener {
            val intent = Intent(this@MyProfileFragment2, FirstStart::class.java)

            // clear activity log
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    // getting some information from server
    private fun retrofitMyProfileMain(){
       val retrofit = RetrofitApi.getMyProfileServiceInstance()
        retrofit.getMyProfile(token)
            .enqueue(object : Callback<MyProfileResponse>{
                override fun onResponse(call: Call<MyProfileResponse>, response: Response<MyProfileResponse>) {
                    if (response.isSuccessful){
                        val myProfileResponse = response.body()

                        username = myProfileResponse!!.username
                        email = myProfileResponse!!.email

                        // about account information
                        binding.usernameTextview.text = username
                        binding.emailTextview.text = email

                    }
                }

                override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {
                    Log.d("Debug", "MyProfile / Error: ${t.message}")
                    Toast.makeText(this@MyProfileFragment2, "Error to retrieve data", Toast.LENGTH_SHORT).show()

                }
            })
    }

}