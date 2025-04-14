package com.example.danzle.data.api
import com.example.danzle.correction.MediapipeService
import com.example.danzle.practice.SilhouettePracticeService
import com.example.danzle.correction.CorrectionMusicSelectService
import com.example.danzle.correction.CorrectionService
import com.example.danzle.correction.SilhouetteCorrectionService
import com.example.danzle.myprofile.MyProfileService
import com.example.danzle.myprofile.editProfile.ChangePasswordService
import com.example.danzle.myprofile.editProfile.ChangeUsernameService
import com.example.danzle.myprofile.myVideo.ChallengeVideoRepositoryService
import com.example.danzle.myprofile.myVideo.MyVideoService
import com.example.danzle.myprofile.myVideo.PracticeVideoRepositoryService
import com.example.danzle.practice.HighlightPracticeService
import com.example.danzle.practice.PracticeMusicSelectService
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
    private const val BASE_URL = "http://3.34.125.216:8080"
    //private const val BASE_URL = "http://3.39.234.248:8080"
    //private const val BASE_URL = "http://43.200.171.252:8080"

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

    // RefreshToken
    private val authService: AuthService by lazy {
        danzleRetrofit.create(AuthService::class.java)
    }

    fun getAuthInstance(): AuthService{
        return authService
    }

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

    // PracticeMusicSelect
    private val practiceMusicSelectService: PracticeMusicSelectService by lazy {
        danzleRetrofit.create(PracticeMusicSelectService::class.java)
    }
    fun getPracticeMusicSelectInstance(): PracticeMusicSelectService{
        return practiceMusicSelectService
    }

    // CorrectionMusicSelect
    private val correctionMusicSelectService: CorrectionMusicSelectService by lazy {
        danzleRetrofit.create(CorrectionMusicSelectService::class.java)
    }
    fun getCorrectionMusicSelectInstance(): CorrectionMusicSelectService{
        return correctionMusicSelectService
    }

    // HighlightPractice
    private val highlightPracticeService: HighlightPracticeService by lazy {
        danzleRetrofit.create(HighlightPracticeService::class.java)
    }
    fun getHighlightPracticeInstance(): HighlightPracticeService{
        return highlightPracticeService
    }

    // PracticeSilhouette
    private val silhouettePracticeService: SilhouettePracticeService by lazy {
        danzleRetrofit.create(SilhouettePracticeService::class.java)
    }
    fun getPracticeSilhouetteInstance(): SilhouettePracticeService {
        return silhouettePracticeService
    }

    // Correction
    private val correctionService: CorrectionService by lazy {
        danzleRetrofit.create(CorrectionService::class.java)
    }
    fun getCorrectionInstance(): CorrectionService {
        return correctionService
    }

    // SilhouetteCorrection
    private val silhouetteCorrectionService: SilhouetteCorrectionService by lazy {
        danzleRetrofit.create(SilhouetteCorrectionService::class.java)
    }
    fun getSilhouetteCorrectionInstance(): SilhouetteCorrectionService {
        return silhouetteCorrectionService
    }

    // Mediapipe
    private val mediapipeService: MediapipeService by lazy {
        danzleRetrofit.create(MediapipeService::class.java)
    }
    fun getMediapipeInstance(): MediapipeService {
        return mediapipeService
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
    fun getPracticeVideoRepositoryInstance(): PracticeVideoRepositoryService{
        return danzleRetrofit.create(PracticeVideoRepositoryService::class.java)
    }

    // challenge repository
    fun getChallengeVideoRepositoryInstance(): ChallengeVideoRepositoryService{
        return danzleRetrofit.create(ChallengeVideoRepositoryService::class.java)
    }


}


class NullOnEmptyConverterFactory  : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> = object : Converter<ResponseBody, Any?> {
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


