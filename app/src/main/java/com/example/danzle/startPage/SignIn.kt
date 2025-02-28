package com.example.danzle.startPage

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.databinding.ActivitySignInBinding
import com.example.danzle.retrofit.RetrofitService
import com.example.danzle.retrofit.UserToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignIn : AppCompatActivity() {

    // declare variable for SignIn
    var email: String = ""
    var password: String = ""

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivitySignInBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // Connecting with server (Using Retrofit)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        // convert SignIn to CreateAccount
        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignIn, CreateAccount::class.java))
        }

        // making underline at <CreateAccount> TextView
        binding.createAccount.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // writing email
        binding.id.doAfterTextChanged {
            // assign value on email variable
            email = it.toString()
        }

//        findViewById<EditText>(R.id.id).doAfterTextChanged {
//            // assign value on email variable
//            email = it.toString()
//        }

        // writing password
        findViewById<EditText>(R.id.password).doAfterTextChanged {
            // assign value on password variable
            password = it.toString()
        }

        // click ForgotPassword text, then ForgotPassword
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this@SignIn, ForgotPassword1::class.java))
        }

        // click remember checkbox


        // click button, then Sign In
        findViewById<Button>(R.id.signinButton).setOnClickListener {
//            val user = HashMap<String, Any>()
//            user.put("email", email)
//            user.put("password", password)

            val request = SignInRequest(email, password)

            retrofitService.signin(request).enqueue(object: Callback<UserToken>{
                override fun onFailure(p0: Call<UserToken>, p1: Throwable) {
                    Toast.makeText(this@SignIn, "네트워크 오류 발생!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(p0: Call<UserToken>, p1: Response<UserToken>) {
                    if(p1.isSuccessful){
                        val token: UserToken = p1.body()!!
                        val intent = Intent(this@SignIn, MainActivity::class.java)
                        intent.putExtra("Token", token.token)
                        startActivity(intent)
                    } else {
                        // giving some message if it is fail to SignIn
                        Toast.makeText(this@SignIn, "로그인 실패: ${p1.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }


    }

}