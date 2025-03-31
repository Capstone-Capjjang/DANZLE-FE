package com.example.danzle.startPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.R
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.databinding.ActivityCreateAccountBinding
import com.example.danzle.data.remote.request.auth.CreateAccountRequest
import com.example.danzle.data.remote.response.auth.CreateAccountResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        binding.createAccountButton.setOnClickListener {
            val createUserData = CreateAccountRequest(email, username, password1, password2, termsAccepted)
            retrofitCreateAccount(createUserData, this)
        }

        // click SignIn button, move to SignIn page
        binding.backToSignin.setOnClickListener {
            startActivity(Intent(this@CreateAccount, SignIn::class.java))
        }
    }

    /*
    Below Code is about the error message shown next to the EditText.
    It doesn't have the function to check before changing Activity.
 */

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
            binding.emailLayout.error = errorMessage
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
            binding.usernameLayout.error = errorMessage
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
            binding.password1Layout.error = errorMessage
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
            binding.password2Layout.error = errorMessage
        }

        return errorMessage == null
    }

    // checking the password1 and password2 is same
    private fun validatePasswordAndConfirmPassword(): Boolean{
        var errorMessage: String? = null
        val password1 = binding.password1.text.toString()
        val password2 = binding.password2.text.toString()

        if (password1 != password2){
            errorMessage = "Confirm password doesn't match with password"
            Log.d("createAccount", "password1 and passoword2 aren't same")
        }

        // print the error message below the view
        if (errorMessage != null){
            binding.emailLayout.error = errorMessage
        }

        return errorMessage == null
    }


    override fun onClick(view: View?) {}

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            // check the validation if the each id's focus is finished
            // focus means that if it's focus, then someone writes something on that textview
            when(view.id){
                R.id.email -> {
                    if (hasFocus){
                        binding.email.error = null
                    }else{
                        if(validateEmail()){
                            // do validation for its uniqueness

                        }
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
                        if(validatePassword() && binding.password1.text!!.isNotEmpty()
                            && validateConfirmPassword() && validatePasswordAndConfirmPassword()){
                            binding.password2.error = null
                        }
                    }
                }
                R.id.password2 -> {
                    if (hasFocus){
                        // meaning of no error
                        binding.password2.error = null
                    }else{
                        if(validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()){
                            binding.password1.error = null
                        }
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }


    // about retrofit
    private fun retrofitCreateAccount(userInfo: CreateAccountRequest, context: Context){
        val retrofit = RetrofitApi.getCreateAccountInstance()
        retrofit.addUser(userInfo)
            .enqueue(object : Callback<CreateAccountResponse>{
                override fun onResponse(call: Call<CreateAccountResponse>, response: Response<CreateAccountResponse>) {
                    if (response.isSuccessful){
                        // val createAccountResponse = response.body()

                        // changing the page / go to SignIn
                        startActivity(Intent(this@CreateAccount, SignIn::class.java))

                    } else{
                        Log.d("Debug", "CreateAccount / Response Code: ${response.code()}")
                        Toast.makeText(context, "Fail to CreateAccount: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<CreateAccountResponse>, t: Throwable) {
                    Log.d("Debug", "CreateAccount / Error: ${t.message}")
                }
            })
    }

}



