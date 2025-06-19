package com.example.danzle.startPage

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
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
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.request.auth.SignInRequest
import com.example.danzle.data.remote.response.auth.MyProfileResponse
import com.example.danzle.databinding.ActivitySignInBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val token: String = ""

class SignIn : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

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
            if (binding.emailLayout.error != null) {
                validateEmail()
            }
        }

        // writing password
        binding.password.doAfterTextChanged {
            // assign value on password variable whenever user writes password
            password = it.toString()
            if (binding.passwordLayout.error != null) {
                validatePassword()
            }
        }

        // click ForgotPassword text, then ForgotPassword
        binding.forgotPasswordTextview.setOnClickListener {
            startActivity(Intent(this@SignIn, ForgotPassword1::class.java))
        }

        // click remember checkbox
        binding.checkButton.setOnClickListener {
            if (binding.checkButton.isChecked) {
            } else {
            }
        }

        // click button, then Sign In
        binding.signinButton.setOnClickListener {
            val userData = SignInRequest(email, password)
            retrofitSignIn(userData)
        }

        binding.createAccount.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }


    private fun validateEmail(): Boolean {
        val email = binding.email.text.toString()

        return when {
            email.isEmpty() -> {
                binding.emailLayout.error = "Email is required"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailLayout.error = "Email Address is invalid"
                false
            }

            else -> {
                binding.emailLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.password.text.toString()

        return when {
            password.isEmpty() -> {
                binding.passwordLayout.error = "Password is required"
                false
            }

            else -> {
                binding.passwordLayout.error = null
                true
            }
        }
    }

    // 사용 안 하고 있지만 지우면 오류 발생
    override fun onClick(view: View?) {

    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.email -> {
                    if (hasFocus) {
                        binding.emailLayout.error = null
                    } else {
                        validateEmail()
                    }
                }

                R.id.password -> {
                    if (hasFocus) {
                        binding.passwordLayout.error = null
                    } else {
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
    private fun retrofitSignIn(userInfo: SignInRequest) {
        val retrofit = RetrofitApi.getSignInInstance()
        retrofit.userLogin(userInfo)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val accessToken =
                            response.headers()["Authorization"]?.removePrefix("Bearer ")
                        val refreshToken = response.headers()["Refresh-Token"]
                        val userId = response.headers()["userId"]


                        // SharedPreferences에 저장
                        DanzleSharedPreferences.setAccessToken(accessToken)
                        DanzleSharedPreferences.setRefreshToken(refreshToken)
                        DanzleSharedPreferences.setUserId(userId)

                        // get User information
                        // /login 요청 -> accessToken, refreshToken 저장
                        // accessToken으로 /user/profile 요청
                        // 받은 MyProfileResponse에서 id, email 등을 SharedPreferences에 저장
                        accessToken?.let { token ->
                            val authHeader = "Bearer $token"
                            RetrofitApi.getMyProfileServiceInstance()
                                .getMyProfile(authHeader)
                                .enqueue(object : Callback<MyProfileResponse> {
                                    override fun onResponse(
                                        call: Call<MyProfileResponse>,
                                        response: Response<MyProfileResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            val user = response.body()
                                            user?.let {
                                                DanzleSharedPreferences.setUserEmail(it.email)
                                            }
                                        } else {
                                            Toast.makeText(
                                                this@SignIn,
                                                "사용자 정보를 불러올 수 없습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<MyProfileResponse>, t: Throwable
                                    ) {
                                        Toast.makeText(
                                            this@SignIn,
                                            "네트워크 오류로 사용자 정보를 가져올 수 없습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                        startActivity(Intent(this@SignIn, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignIn, "Fail to SingIn: ${response.message()}", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@SignIn, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
