package com.example.danzle

import android.app.Application
import com.example.danzle.data.api.DanzleSharedPreferences

// 앱이 시작될 때 DanzleSharedPreferences.init()을 딱 한 번만 호출해준다.
class DanzleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initSharedPreferences()
    }

    private fun initSharedPreferences() {
        DanzleSharedPreferences.init(applicationContext)
    }
}