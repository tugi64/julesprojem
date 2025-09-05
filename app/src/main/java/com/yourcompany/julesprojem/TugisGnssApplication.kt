package com.yourcompany.julesprojem

import android.app.Application

class TugisGnssApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ProjectRepository.init(this)
    }
}
