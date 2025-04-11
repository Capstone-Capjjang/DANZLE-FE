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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.danzle.MainActivity
import com.example.danzle.MyProfileFragment
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.databinding.ActivitySignInBinding
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.request.auth.SignInRequest
import com.example.danzle.data.remote.response.auth.MyProfileResponse
import com.example.danzle.data.remote.response.auth.SignInResponse
import com.example.danzle.practice.HighlightPractice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val token: String = ""

class SignIn : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    // declare variable for SignIn
    var email: String = ""
    var password: String = ""

    private lateinit var binding: ActivitySignInBinding

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
            retrofitSignIn(userData)
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
            // 에러 메세지가 하단에 생성
            binding.emailLayout.error = errorMessage
            // 말풍선처럼 에러 메세지가 뜬다.
//            binding.email.apply {
//                error = errorMessage
//            }
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
            binding.passwordLayout.error = errorMessage
        }

        return errorMessage == null
    }

    // 사용 안 하고 있지만 지우면 오류 발생
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
    private fun retrofitSignIn(userInfo: SignInRequest){
        val retrofit = RetrofitApi.getSignInInstance()
        retrofit.userLogin(userInfo)
            .enqueue(object : Callback<SignInResponse> {
                override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                    if (response.isSuccessful) {
                        val signInResponse = response.body()
                        val bodyToken = signInResponse?.accessToken
                        val headerToken = response.headers()["Authorization"]?.removePrefix("Bearer ")?.trim()
                        val refreshToken = response.headers()["Refresh-Token"]

                        val accessToken = bodyToken ?: headerToken

                        Log.d("SignIn", "Access Token: $accessToken")
                        Log.d("SignIn", "Refresh Token: $refreshToken")


                        // SharedPreferences에 저장
                        DanzleSharedPreferences.setAccessToken(accessToken)
                        DanzleSharedPreferences.setRefreshToken(refreshToken)
                        //DanzleSharedPreferences.setUserId(userId)

                        Log.d("SignIn", "Raw Response: ${response.raw()}")
                        Log.d("SignIn", "Error Body: ${response.errorBody()?.string()}")


                        // get User information
                        // /login 요청 -> accessToken, refreshToken 저장
                        // accessToken으로 /user/profile 요청
                        // 받은 MyProfileResponse에서 id, email 등을 SharedPreferences에 저장
                        accessToken?.let { token ->
                            val authHeader = "Bearer $token"
                            RetrofitApi.getMyProfileServiceInstance()
                                .getMyProfile(authHeader)
                                .enqueue(object : Callback<MyProfileResponse>{
                                    override fun onResponse(call: Call<MyProfileResponse>, response: Response<MyProfileResponse>
                                    ) {
                                        // Log about sever response
                                        Log.d("SignIn", "Raw Body: ${response.body()}")
                                        Log.d("SignIn", "Raw Response: ${response.raw()}")
                                        Log.d("SignIn", "Error Body: ${response.errorBody()?.string()}")

                                        if (response.isSuccessful){
                                            val user = response.body()
                                            user?.let {
                                                DanzleSharedPreferences.setUserId(it.id)
                                                DanzleSharedPreferences.setUserEmail(it.email)
                                                Log.d("SignIn", "Save user information → userId: ${it.id}, email: ${it.email}")

                                            }
                                        } else{
                                            Log.e("SignIn", "fail to user information response: ${response.code()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<MyProfileResponse>, t: Throwable
                                    ) {
                                        // Log.d: debug -> 상태 확인용
                                        // Log.e: error -> 에러 보고용 (텍스트 색상이 빨강이다.)
                                        Log.e("SignIn", "유저 정보 요청 실패: ${t.message}")
                                    }
                                })
                        }


                        // 로그인 성공 후 MainActivity로 이동
                        startActivity(Intent(this@SignIn, MainActivity::class.java))
                        //startActivity(Intent(this@SignIn, HighlightPractice::class.java))
                        finish()
                    } else {
                        Log.d("Debug", "SignIn / Response Code: ${response.code()}")
                        Toast.makeText(this@SignIn, "Fail to SingIn: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                    Log.d("Debug", "SignIn / Error: ${t.message}")
                    Toast.makeText(this@SignIn, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }



}
