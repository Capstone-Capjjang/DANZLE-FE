package com.example.danzle.data.api
import com.example.danzle.startPage.CreateAccount
import com.example.danzle.startPage.CreateAccountService
import com.example.danzle.startPage.SignIn
import com.example.danzle.startPage.SignInsService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitApi {
    private const val BASE_URL = "http://43.200.171.252:8080"

    private val client: OkHttpClient by lazy{
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).build()
    }

//    private val gson = GsonBuilder()
//        .setLenient()
//        .create()

    private val danzleRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private  val signInService: SignInsService by lazy{
        danzleRetrofit.create(SignInsService::class.java)
    }

    fun getSignInInstance(): SignInsService{
        return signInService
    }

    private val createAccountService: CreateAccountService by lazy{
        danzleRetrofit.create(CreateAccountService::class.java)
    }

    fun getCreateAccountInstance(): CreateAccountService {
        return createAccountService
    }
}
