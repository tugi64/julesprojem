package com.yourcompany.julesprojem

import android.app.Application
import android.content.Context

class TugisGnssApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
