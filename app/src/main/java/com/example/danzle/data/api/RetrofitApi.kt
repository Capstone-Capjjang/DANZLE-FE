package com.example.danzle.data.api
import com.example.danzle.myprofile.MyProfileService
import com.example.danzle.myprofile.editProfile.ChangePasswordService
import com.example.danzle.myprofile.editProfile.ChangeUsernameService
import com.example.danzle.myprofile.myVideo.MyVideoService
import com.example.danzle.practice.HighlightPracticeService
import com.example.danzle.startPage.CreateAccountService
import com.example.danzle.startPage.ForgotPassword1Service
import com.example.danzle.startPage.SignInsService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


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
        .addConverterFactory(NullOnEmptyConverterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    // SingIn
    private  val signInService: SignInsService by lazy{
        danzleRetrofit.create(SignInsService::class.java)
    }
    fun getSignInInstance(): SignInsService{
        return signInService
    }

    // CreateAccount
    private val createAccountService: CreateAccountService by lazy{
        danzleRetrofit.create(CreateAccountService::class.java)
    }
    fun getCreateAccountInstance(): CreateAccountService {
        return createAccountService
    }

    // HighlightPractice
    fun getHighlightPracticeInstance(): HighlightPracticeService {
        return danzleRetrofit.create(HighlightPracticeService::class.java)
    }

    // ForgotPassword1
    private val forgotPassword1Service: ForgotPassword1Service by lazy {
        danzleRetrofit.create(ForgotPassword1Service::class.java)
    }
    fun getForgotPassword1Instance(): ForgotPassword1Service{
        return forgotPassword1Service
    }

    // MyProfile, EditProfile
    fun getMyProfileServiceInstance(): MyProfileService {
        return danzleRetrofit.create(MyProfileService::class.java)
    }

    // ChangeUsername
    fun getChangeUsernameInstance(): ChangeUsernameService{
        return danzleRetrofit.create(ChangeUsernameService::class.java)
    }

    // ChangePassword
    fun getChangePasswordInstance(): ChangePasswordService{
        return danzleRetrofit.create(ChangePasswordService::class.java)
    }

    // video main repository
    fun getMyVideoInstance(): MyVideoService{
        return danzleRetrofit.create(MyVideoService::class.java)
    }

    // practice repository


    // challenge repository


}


class NullOnEmptyConverterFactory  : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? = object : Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
        override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
            try{
                nextResponseBodyConverter.convert(value)
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        } else{
            null
        }
    }
}


