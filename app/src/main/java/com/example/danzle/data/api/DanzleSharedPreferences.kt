package com.example.danzle.data.api

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// JWT 토큰과 사용자 정보를 저장
// 앱 어디서든 DanzleSharedPreferences.setAccessToken() 직접 접근 가능
object DanzleSharedPreferences {
    private lateinit var preferences: SharedPreferences
    private const val PREFERENCES_NAME = "DANZLE_PREFERENCES"
    private const val ACCESS_TOKEN = "ACCESS_TOKEN"
    private const val REFRESH_TOKEN = "REFRESH_TOKEN"
    private const val USER_ID = "USER_ID"
    private const val USER_EMAIL = "USER_EMAIL"

    // 앱 시작시 초기화 필수
    fun init(context: Context){
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


    // 토큰 저장/조회
    // null이면 해당 키 삭제 → 로그아웃 시 필요
    // putString()은 토큰을 문자열로 저장
    fun setAccessToken(token: String?){
        preferences.edit {
            if (token == null) remove(ACCESS_TOKEN)
            else putString(ACCESS_TOKEN, token)
        }
    }

    fun getAccessToken(): String? = preferences.getString(ACCESS_TOKEN, null)


    // Refresh token
    fun setRefreshToken(token: String?){
        preferences.edit {
            if (token == null) remove(REFRESH_TOKEN)
            else putString(REFRESH_TOKEN, token)
        }
    }

    fun getRefreshToken(): String? = preferences.getString(REFRESH_TOKEN, null)


    // UserId
    fun setUserId(id: Long?){
        preferences.edit {
            if (id == null) remove(USER_ID)
            else putLong(USER_ID, id)
        }
    }

    // 기본값으로 -1을 넣고 실제 저장된 값이 없으면 null 반환.
    fun getUserId(): Long?{
        val id = preferences.getLong(USER_ID, -1)
        return if (id != -1L) id else null
    }


    // UserEmail
    fun setUserEmail(email: String?){
        preferences.edit {
            if (email == null) remove(USER_EMAIL)
            else putString(USER_EMAIL, email)
        }
    }

    fun getUserEmail(): String? = preferences.getString(USER_EMAIL, null)


    // clear
    fun clear(){
        preferences.edit().clear().apply()
    }

}