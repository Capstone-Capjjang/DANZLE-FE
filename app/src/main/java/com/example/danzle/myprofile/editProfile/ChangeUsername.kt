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
import com.example.danzle.data.remote.request.auth.ChangeUsernameRequest
import com.example.danzle.data.remote.request.auth.SignInRequest
import com.example.danzle.data.remote.response.auth.ChangeUsernameResponse
import com.example.danzle.databinding.ActivityChangeUsernameBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeUsername : AppCompatActivity() {

    private lateinit var binding: ActivityChangeUsernameBinding
    var username: String = ""
    val token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangeUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // backbutton
        binding.backButton.setOnClickListener { startActivity(Intent(this@ChangeUsername, EditProfile::class.java)) }

        // assign after writing
        binding.changeUsernameEdittext.doAfterTextChanged {
            username = it.toString()
        }

        // click button
        binding.changeUsernameButton.setOnClickListener {
            val changeUsernameData = ChangeUsernameRequest(username)
            Log.d("Debug", "changeUsername Request - Username: $username")  // 디버깅 추가
            retrofitChangeUsername(changeUsernameData, this)
        }

    }


    // dialog
    private fun showConfirmDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.profile_updated_dialog, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        val confirmButton = view.findViewById<Button>(R.id.confirmButton)

        alertDialog.window!!.setGravity(Gravity.BOTTOM)
        alertDialog.window!!.attributes.windowAnimations = R.style.dialogAniamtion
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        confirmButton.setOnClickListener {
            val intent = Intent(this@ChangeUsername, EditProfile::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            alertDialog.dismiss()
        }

        alertDialog.show()

    }

    // check the condition of username
    private fun validateUsername(): Boolean{
        var errorMessage: String? = null
        val username: String = binding.changeUsernameEdittext.text.toString()
        if (username.isEmpty()){
            errorMessage = "Username is required"
        }

        if (errorMessage != null){
            binding.changeUsernameEdittext.apply {
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // about retrofit
    private fun retrofitChangeUsername(changeUsernameInfo: ChangeUsernameRequest, context: Context){
        val retrofit = RetrofitApi.getChangeUsernameInstance()
            retrofit.getChangeUsername(token, changeUsernameInfo)
                .enqueue((object : Callback<ChangeUsernameResponse>{
                    override fun onResponse(call: Call<ChangeUsernameResponse>, response: Response<ChangeUsernameResponse>) {
                        if (response.isSuccessful){
                            val changeUsernameResponse = response.body()
                            Log.d("Debug", "ChangeUsername / Full Response Body: $changeUsernameResponse") // 응답 전체 확인

                            // confirm dial
                            // if it is success, then show dialog
                            showConfirmDialog()
                        } else{
                            Log.d("Debug", "ChangeUsername / Response Code: ${response.code()}")
                            Toast.makeText(context, "Fail to ChangeUsername: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ChangeUsernameResponse>, t: Throwable) {
                        Log.d("Debug", "ChangeUsername / Error: ${t.message}")
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }

                }))
    }

}