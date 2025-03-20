package com.example.danzle.startPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.R
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.request.auth.ForgotPassword1Request
import com.example.danzle.data.remote.response.auth.ForgotPassword1Response
import com.example.danzle.databinding.ActivityForgotPassword1Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassword1 : AppCompatActivity() {
    var email: String = ""

    private lateinit var binding: ActivityForgotPassword1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPassword1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // click cancel
        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@ForgotPassword1, SignIn::class.java))

        }

        // assign email about writing email in edittext
        binding.emailEdittext.doAfterTextChanged {
            email = it.toString()
        }

        // click sendEmail button
        binding.sendEmailButton.setOnClickListener {
            val emailData = ForgotPassword1Request(email)
            RetrofitForgotPassword1(emailData, this).work()
        }
    }
}


// about retrofit
class RetrofitForgotPassword1(private val emailInfo: ForgotPassword1Request, private val context: Context){
    fun work(){
        val retrofit = RetrofitApi.getForgotPassword1Instance()
        retrofit.changePassword(emailInfo)
            .enqueue(object : Callback<ForgotPassword1Response>{
                override fun onResponse(call: Call<ForgotPassword1Response>, response: Response<ForgotPassword1Response>) {
                    if (response.isSuccessful){

                    }
                }

                override fun onFailure(call: Call<ForgotPassword1Response>, t: Throwable) {
                    Log.d("debug", "ForgotPassoword1 / Error: ${t.message}")
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}