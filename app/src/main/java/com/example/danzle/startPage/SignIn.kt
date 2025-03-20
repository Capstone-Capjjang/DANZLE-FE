package com.example.danzle.startPage

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.MyProfileFragment
import com.example.danzle.R
import com.example.danzle.databinding.ActivitySignInBinding
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.request.auth.SignInRequest
import com.example.danzle.data.remote.response.auth.SignInResponse
import com.example.danzle.MyProfileFragment2
import com.example.danzle.viewModel.SignViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val token: String = ""

class SignIn : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    // declare variable for SignIn
    var email: String = ""
    var password: String = ""

    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.email.onFocusChangeListener = this
        binding.password.onFocusChangeListener = this

        // convert SignIn to CreateAccount
        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignIn, CreateAccount::class.java))
        }
        // making underline at <CreateAccount> TextView
        binding.createAccount.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // assign email, password
        binding.email.doAfterTextChanged {
            // assign value on email variable whenever user writes email
            email = it.toString()
        }

        // writing password
        binding.password.doAfterTextChanged {
            // assign value on password variable whenever user writes password
            password = it.toString()
        }

        // click ForgotPassword text, then ForgotPassword
        binding.forgotPasswordTextview.setOnClickListener {
            startActivity(Intent(this@SignIn, ForgotPassword1::class.java))
        }

        // click remember checkbox
        binding.checkButton.setOnClickListener{
            if (binding.checkButton.isChecked){
                Log.d("SignIN", "checkButton")
            } else{
                Log.d("SignIn", "No checkButton")
            }
        }

        // click button, then Sign In
        binding.signinButton.setOnClickListener {
            val userData = SignInRequest(email, password)
            Log.d("Debug", "SignIn Request - Email: $email, Password: $password")  // 디버깅 추가
            retrofitSignIn(userData, this)
        }

    }

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

    private fun validatePassword(): Boolean{
        var errorMessage: String? = null
        val password: String = binding.password.text.toString()
        if (password.isEmpty()){
            errorMessage = "Password is required"
            Log.d("createAccount", "no password1")
        }

        // print the error message
        if (errorMessage != null){
            binding.password.apply {
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    override fun onClick(view: View?) {

    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            when (view.id){
                R.id.email -> {
                    if (hasFocus){
                        binding.email.error = null
                    } else{
                        validateEmail()
                    }
                }
                R.id.password -> {
                    if (hasFocus){
                        binding.email.error = null
                    } else{
                        validatePassword()
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    // about retrofit
    private fun retrofitSignIn(userInfo: SignInRequest, context: Context){
        val retrofit = RetrofitApi.getSignInInstance()
        retrofit.userLogin(userInfo)
            .enqueue(object : Callback<SignInResponse> {
                override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                    if (response.isSuccessful) {
                        val signInResponse = response.body()
                        Log.d("Debug", "SignIn / Full Response Body: $signInResponse") // 응답 전체 확인

                        val token = signInResponse?.accessToken ?: ""
                        Log.d("Debug", "SignIn / Token: $token")

                        // 로그인 성공 후 MainActivity로 이동
                        val intent = Intent(context, MyProfileFragment::class.java)
                        intent.putExtra("Token", token)
                        context.startActivity(intent)
                    } else {
                        Log.d("Debug", "SignIn / Response Code: ${response.code()}")
                        Toast.makeText(context, "Fail to SingIn: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                    Log.d("Debug", "SignIn / Error: ${t.message}")
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
