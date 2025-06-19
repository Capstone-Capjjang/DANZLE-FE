package com.example.danzle.data.api

import com.example.danzle.BuildConfig
import com.example.danzle.challenge.ChallengeHighlightService
import com.example.danzle.challenge.ChallengeSaveService
import com.example.danzle.challenge.ChallengeService
import com.example.danzle.commonService.MusicSelectService
import com.example.danzle.correction.CorrectionSaveService
import com.example.danzle.commonService.SaveVideoService
import com.example.danzle.commonService.SilhouetteService
import com.example.danzle.correction.CorrectionDetailFeedbackService
import com.example.danzle.correction.CorrectionResultService
import com.example.danzle.correction.CorrectionService
import com.example.danzle.correction.PoseAnalysis
import com.example.danzle.myprofile.MyProfileService
import com.example.danzle.myprofile.editProfile.ChangePasswordService
import com.example.danzle.myprofile.editProfile.ChangeUsernameService
import com.example.danzle.myprofile.myScore.MyScoreService
import com.example.danzle.myprofile.myVideo.ChallengeVideoRepositoryService
import com.example.danzle.myprofile.myVideo.CorrectionVideoRepositoryService
import com.example.danzle.myprofile.myVideo.MyVideoService
import com.example.danzle.myprofile.myVideo.PracticeVideoRepositoryService
import com.example.danzle.practice.PracticeSaveService
import com.example.danzle.practice.PracticeService
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
import java.util.concurrent.TimeUnit


object RetrofitApi {

    private val BASE_URL = BuildConfig.BASE_URL

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(240, TimeUnit.SECONDS) // 서버 연결까지 기다리는 최대 시간
            .readTimeout(240, TimeUnit.SECONDS)    // 서버 응답 읽기까지 기다리는 최대 시간
            .writeTimeout(240, TimeUnit.SECONDS)   // 서버에 요청 쓰기까지 기다리는 최대 시간
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

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

    fun getAuthInstance(): AuthService {
        return authService
    }

    // SingIn
    private val signInService: SignInsService by lazy {
        danzleRetrofit.create(SignInsService::class.java)
    }

    fun getSignInInstance(): SignInsService {
        return signInService
    }

    // CreateAccount
    private val createAccountService: CreateAccountService by lazy {
        danzleRetrofit.create(CreateAccountService::class.java)
    }

    fun getCreateAccountInstance(): CreateAccountService {
        return createAccountService
    }

    // MusicSelect
    private val musicSelectService: MusicSelectService by lazy {
        danzleRetrofit.create(MusicSelectService::class.java)
    }

    fun getMusicSelectInstance(): MusicSelectService {
        return musicSelectService
    }


    // Practice
    private val practiceService: PracticeService by lazy {
        danzleRetrofit.create(PracticeService::class.java)
    }

    fun getPracticeInstance(): PracticeService {
        return practiceService
    }

    // Correction
    private val correctionService: CorrectionService by lazy {
        danzleRetrofit.create(CorrectionService::class.java)
    }

    fun getCorrectionInstance(): CorrectionService {
        return correctionService
    }

    // Correction result
    private val correctionResultService: CorrectionResultService by lazy {
        danzleRetrofit.create(CorrectionResultService::class.java)
    }

    fun getCorrectionResultInstance(): CorrectionResultService {
        return correctionResultService
    }

    // Silhouette
    private val silhouetteService: SilhouetteService by lazy {
        danzleRetrofit.create(SilhouetteService::class.java)
    }

    fun getSilhouetteInstance(): SilhouetteService {
        return silhouetteService
    }

    // sending pose information
    private val poseAnalysisService: PoseAnalysis by lazy {
        danzleRetrofit.create(PoseAnalysis::class.java)
    }

    fun getPoseAnalysisInstance(): PoseAnalysis {
        return poseAnalysisService
    }

    // save Practice video
    private val savePracticeService: PracticeSaveService by lazy {
        danzleRetrofit.create(PracticeSaveService::class.java)
    }

    fun getPracticeSaveInstance(): PracticeSaveService {
        return savePracticeService
    }

    // save Correction video
    private val saveCorrectionService: CorrectionSaveService by lazy {
        danzleRetrofit.create(CorrectionSaveService::class.java)
    }

    fun getCorrectionSaveInstance(): CorrectionSaveService {
        return saveCorrectionService
    }

    // save Challenge video
    private val saveChallengeService: ChallengeSaveService by lazy {
        danzleRetrofit.create(ChallengeSaveService::class.java)
    }

    fun getChallengeSaveInstance(): ChallengeSaveService {
        return saveChallengeService
    }

    // save video
    private val saveVideoService: SaveVideoService by lazy {
        danzleRetrofit.create(SaveVideoService::class.java)
    }

    fun getSaveVideoInstance(): SaveVideoService {
        return saveVideoService
    }

    // detail feedback
    private val correctionDetailFeedback: CorrectionDetailFeedbackService by lazy {
        danzleRetrofit.create(CorrectionDetailFeedbackService::class.java)
    }

    fun getCorrectionDetailFeedbackInstance(): CorrectionDetailFeedbackService {
        return correctionDetailFeedback
    }

    // challenge
    private val challengeService: ChallengeService by lazy {
        danzleRetrofit.create(ChallengeService::class.java)
    }

    fun getChallengeInstance(): ChallengeService {
        return challengeService
    }

    // ChallengeHighlightService
    private val challengeHighlightService: ChallengeHighlightService by lazy {
        danzleRetrofit.create(ChallengeHighlightService::class.java)
    }

    fun getChallengeHighlightInstance(): ChallengeHighlightService {
        return challengeHighlightService
    }

    // ForgotPassword1
    private val forgotPassword1Service: ForgotPassword1Service by lazy {
        danzleRetrofit.create(ForgotPassword1Service::class.java)
    }

    fun getForgotPassword1Instance(): ForgotPassword1Service {
        return forgotPassword1Service
    }

    // MyProfile, EditProfile
    private val myProfileService: MyProfileService by lazy {
        danzleRetrofit.create(MyProfileService::class.java)
    }

    fun getMyProfileServiceInstance(): MyProfileService {
        return myProfileService
    }

    // ChangeUsername
    private val changeUserNameService: ChangeUsernameService by lazy {
        danzleRetrofit.create(ChangeUsernameService::class.java)
    }

    fun getChangeUsernameInstance(): ChangeUsernameService {
        return changeUserNameService
    }

    // ChangePassword
    private val changePasswordService: ChangePasswordService by lazy {
        danzleRetrofit.create(ChangePasswordService::class.java)
    }

    fun getChangePasswordInstance(): ChangePasswordService {
        return changePasswordService
    }

    // video main repository
    private val myVideoService: MyVideoService by lazy {
        danzleRetrofit.create(MyVideoService::class.java)
    }

    fun getMyVideoInstance(): MyVideoService {
        return myVideoService
    }

    // practice repository
    private val practiceVideoRepositoryService: PracticeVideoRepositoryService by lazy {
        danzleRetrofit.create(PracticeVideoRepositoryService::class.java)
    }

    fun getPracticeVideoRepositoryInstance(): PracticeVideoRepositoryService {
        return practiceVideoRepositoryService
    }

    // correction repository
    private val correctionVideoRepositoryService: CorrectionVideoRepositoryService by lazy {
        danzleRetrofit.create(CorrectionVideoRepositoryService::class.java)
    }

    fun getCorrectionVideoRepositoryInstance(): CorrectionVideoRepositoryService {
        return correctionVideoRepositoryService
    }

    // challenge
    private val challengeVideoRepositoryService: ChallengeVideoRepositoryService by lazy {
        danzleRetrofit.create(ChallengeVideoRepositoryService::class.java)
    }

    fun getChallengeVideoRepositoryInstance(): ChallengeVideoRepositoryService {
        return challengeVideoRepositoryService
    }

    // ScoreSave
    private val myScoreService: MyScoreService by lazy {
        danzleRetrofit.create(MyScoreService::class.java)
    }

    fun getMyScoreInstance(): MyScoreService {
        return myScoreService
    }
}


class NullOnEmptyConverterFactory : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> = object : Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter =
            retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

        override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
            try {
                nextResponseBodyConverter.convert(value)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}


