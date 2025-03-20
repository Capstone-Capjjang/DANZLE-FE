package com.example.danzle.myprofile.editProfile

import android.content.Context
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
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.R
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.request.auth.ChangePasswordRequest
import com.example.danzle.data.remote.request.auth.ChangeUsernameRequest
import com.example.danzle.data.remote.response.auth.ChangePasswordResponse
import com.example.danzle.data.remote.response.auth.ChangeUsernameResponse
import com.example.danzle.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    var currentPassword: String = ""
    var newPassword: String = ""
    var confirmNewPassword: String = ""
    var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // backbutton
        binding.backButton.setOnClickListener { startActivity(Intent(this@ChangePassword, EditProfile::class.java)) }

        // assign after writing
        binding.cuurentPassword.doAfterTextChanged {
            currentPassword = it.toString()
        }

        binding.newPassword.doAfterTextChanged {
            newPassword = it.toString()
        }

        binding.confirmPassword.doAfterTextChanged {
            confirmNewPassword = it.toString()
        }

        // click button
        binding.changePasswordButton.setOnClickListener {
            val changePasswordData = ChangePasswordRequest(currentPassword, newPassword, confirmNewPassword)
            retrofitChangePassword(changePasswordData, this)
        }

    }

    private fun showConfirmDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.profile_updated_dialog, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        val confirmButton = view.findViewById<Button>(R.id.confirmButton)

        alertDialog.window!!.attributes.windowAnimations = R.style.dialogAniamtion
        alertDialog.window!!.setGravity(Gravity.BOTTOM)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        confirmButton.setOnClickListener {
            val intent = Intent(this@ChangePassword, EditProfile::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    // about retrofit
    private fun retrofitChangePassword(changePasswordInfo: ChangePasswordRequest, context: Context){
        val retrofit = RetrofitApi.getChangePasswordInstance()
        retrofit.getChangePassword(token, changePasswordInfo)
            .enqueue((object : Callback<ChangePasswordResponse> {
                override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                    if (response.isSuccessful){
                        val changePasswordResponse = response.body()
                        Log.d("Debug", "ChangePassword / Full Response Body: $changePasswordResponse")

                        // confirm dial
                        // if it is success, then show dialog
                        showConfirmDialog()
                    } else{
                        Log.d("Debug", "ChangePassword / Response Code: ${response.code()}")
                        Toast.makeText(context, "Fail to ChangePassword: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    Log.d("Debug", "ChangePassword / Error: ${t.message}")
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }))
    }

}