package com.suji.accountbook

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SujiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
