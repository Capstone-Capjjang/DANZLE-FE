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
import com.example.danzle.MainActivity
import com.example.danzle.R
import com.example.danzle.databinding.ActivityCreateAccountBinding
import com.example.danzle.retrofit.getRetrofit
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
    var termsAccepted: Boolean = false

    // name's form => Activity(XML name)Binding
    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initialize binding
        binding = ActivityCreateAccountBinding.inflate(LayoutInflater.from(this))

        // set the content view
        // Instead of setContentView(R.layout.activity_create_account)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //
        binding.email.onFocusChangeListener = this
        binding.username.onFocusChangeListener = this
        binding.password1.onFocusChangeListener = this
        binding.password2.onFocusChangeListener = this


        binding.email.doAfterTextChanged {
            email = it.toString()
        }

        binding.username.doAfterTextChanged {
            username = it.toString()
        }

        binding.password1.doAfterTextChanged {
            password1 = it.toString()
        }

        binding.password2.doAfterTextChanged {
            password2 = it.toString()
        }

        binding.checkButton.doAfterTextChanged {
            termsAccepted = binding.checkButton.isChecked
        }

        binding.checkButton.setOnClickListener{
            if (binding.checkButton.isChecked){
                Log.d("CreateAccount", "checkButton")
            } else{
                Log.d("CreateAccount", "No checkButton")
            }
        }

        val createAccountService = getRetrofit().create(CreateAccountRequest::class.java)

        binding.createAccountButton.setOnClickListener {
            createAccountService.createAccountRequest(email, username, password1, password2, termsAccepted).enqueue(object: Callback<CreateAccountResponse>{
                override fun onFailure(call: Call<CreateAccountResponse>, p1: Throwable) {
                    Log.d("Debug", "Error: ${p1.message}")
                    // fail to connect with server
                    Toast.makeText(this@CreateAccount, "Network Error", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<CreateAccountResponse>,
                    response: Response<CreateAccountResponse>
                ) {// success to connect with server, get response
                    if(response.isSuccessful){
                        val signInResponse = response.body()
                        val intent = Intent(this@CreateAccount, SignIn::class.java)
                        Log.d("Debug", "success to create account")
                        startActivity(intent)
                    } else {
                        // giving some message if it is fail to SignIn
                        Toast.makeText(this@CreateAccount, "Fail to create account: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.d("Error", "${response.code()}")
                        Log.d("Error", "${response.message()}")
                    }
                }
            })
        }

    }

    // check the vaildation of Email
    private fun validateEmail(): Boolean{
        var errorMessage: String? = null
        // convert written text to String
        val email: String = binding.email.text.toString()
        if (email.isEmpty()){
            errorMessage = "Email is required"
            Log.d("createAccount", "no email")
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //checking Email is valid or not
            errorMessage = "Email Address is invalid"
            Log.d("createAccount", "email's form is wrong")
        }

        // print the error message
        if (errorMessage != null){
            binding.email.apply {
                error = errorMessage
            }
        }

        // No error
        return errorMessage == null
    }

    // check the vaildation of Usename
    private fun validateUsername(): Boolean{
        var errorMessage: String? = null
        val username: String = binding.username.text.toString()
        if (username.isEmpty()){
            errorMessage = "Username is required"
            Log.d("createAccount", "no Username")
        }

        // print the error message
        if (errorMessage != null){
            binding.username.apply {
                error = errorMessage
            }
        }

        // No error
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

        // print the error message
        if (errorMessage != null){
            binding.password1.apply {
                error = errorMessage
            }
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

        // print the error message
        if (errorMessage != null){
            binding.password2.apply {
                error = errorMessage
            }
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

        // print the error message below the view
        if (errorMessage != null){
            binding.email.apply {
                error = errorMessage
            }
        }

        return errorMessage == null
    }


    override fun onClick(view: View?) {
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            // check the validation if the each id's focus is finished
            // focus means that if it's focus, then someone writes something on that textview
            when(view.id){
                R.id.email -> {
                    if (hasFocus){
                        binding.email.error = null
                    }else{
                        validateEmail()
                    }
                }
                R.id.username -> {
                    if (hasFocus){
                        binding.username.error = null
                    }else{
                        validateUsername()
                    }
                }
                R.id.password1 -> {
                    if (hasFocus){
                        binding.password1.error = null
                    }else{
                        validatePassword()
                    }
                }
                R.id.password2 -> {
                    if (hasFocus){
                        binding.password2.error = null
                    }else{
                        validateConfirmPassword()
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
}

