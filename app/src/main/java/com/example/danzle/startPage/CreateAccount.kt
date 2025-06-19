package com.example.danzle.startPage

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.R
import com.example.danzle.databinding.ActivityCreateAccountBinding
import com.example.danzle.retrofit.RetrofitService
import com.example.danzle.retrofit.UserToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class CreateAccount : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    // declare varaiable for CreateAccount
    var email: String = ""
    var username: String = ""
    var password1: String = ""
    var password2: String = ""

    // name's form => Activity(XML name)Binding
    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initialize binding
        binding = ActivityCreateAccountBinding.inflate(LayoutInflater.from(this))

        // set the content view
        // Instead of setContentView(R.layout.activity_create_account)
        setContentView(binding.root)


        // Not using findViewById
//        // Back to Sign In
//        findViewById<TextView>(R.id.backToSignin).setOnClickListener {
//            startActivity(Intent(this, SignIn::class.java))
//        }
//
//        // making underline
//        findViewById<TextView>(R.id.backToSignin).paintFlags = Paint.UNDERLINE_TEXT_FLAG
//
//
//        // assign variables for each EditText
//        // EditText에 들어온 내용이 email에 할당되는 것이다.
//        findViewById<EditText>(R.id.email).doAfterTextChanged { email = it.toString() }
//
//        // checking email is valid or not
//        if (!isValidEmail(email)){
//            Toast.makeText(this@CreateAccount, "Sorry, but the email is not valid", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<EditText>(R.id.username).doAfterTextChanged { username = it.toString() }
//
//        findViewById<EditText>(R.id.password1).doAfterTextChanged { password1 = it. toString() }
//
//        findViewById<EditText>(R.id.password2).doAfterTextChanged { password2 = it.toString() }

        // Connecting with server (Using Retrofit)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        findViewById<Button>(R.id.createAccountButton).setOnClickListener {
            val request = CreateAccountRequest(email, username, password1, password2, termsAccepted = true)

            retrofitService.createAccount(request).enqueue(object : Callback<UserToken> {
                override fun onFailure(p0: Call<UserToken>, p1: Throwable) {

                }

                override fun onResponse(p0: Call<UserToken>, p1: Response<UserToken>) {
                    if (p1.isSuccessful) {
                        val userToken = p1.body()!!
                        val intent = Intent(this@CreateAccount, SignIn::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@CreateAccount, "회원가입 실패: ${p1.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    // check the vaildation of Email
    private fun validateEmail(): Boolean{
        var errorMessage: String? = null
        val email: String = binding.email.text.toString()
        if (email.isEmpty()){
            errorMessage = "Email is required"
            Log.d("createAccount", "no email")
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //checking Email is valid or not
            errorMessage = "Email Address is invalid"
            Log.d("createAccount", "email's form is wrong")
        }

        if (errorMessage != null){
            binding.email.apply {
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // check the vaildation of Usename
    private fun validateUsename(): Boolean{
        var errorMessage: String? = null
        val username: String = binding.username.text.toString()
        if (username.isEmpty()){
            errorMessage = "Username is required"
            Log.d("createAccount", "no Username")
        }
        return errorMessage == null
    }

    // check the vaildation of Password
    private fun validatePassword(): Boolean{
        var errorMessage: String? = null
        val password: String = binding.password1.text.toString()
        if (password.isEmpty()){
            errorMessage = "Password is required"
            Log.d("createAccount", "no password1")
        }
        return errorMessage == null
    }

    // check the vaildation and confirmation of Password
    private fun validateConfirmPassword():Boolean{
        var errorMessage: String? = null
        val password: String = binding.password2.text.toString()
        if (password.isEmpty()){
            errorMessage = "Confirm password is required"
            Log.d("createAccount", "no password2")
        }
        return errorMessage == null
    }

    // checking the password1 and password2 is same
    private fun validatePasswordAndCOnfirmPassword(): Boolean{
        var errorMessage: String? = null
        val password1 = binding.password1.text.toString()
        val password2 = binding.password2.text.toString()

        if (password1 != password2){
            errorMessage = "Confirm password doesn't match with password"
            Log.d("createAccount", "password1 and passoword2 aren't same")
        }
        return errorMessage == null
    }


    override fun onClick(view: View?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            when(view.id){
                R.id.email -> {
                    if (hasFocus){

                    }else{
                        validateEmail()
                    }
                }
                R.id.username -> {
                    if (hasFocus){

                    }else{
                        validateUsename()
                    }
                }
                R.id.password1 -> {
                    if (hasFocus){

                    }else{
                        validatePassword()
                    }
                }
                R.id.password2 -> {
                    if (hasFocus){

                    }else{
                        validateConfirmPassword()
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        TODO("Not yet implemented")
    }
}

