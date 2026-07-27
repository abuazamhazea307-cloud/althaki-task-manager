package com.example

import android.app.Application
import com.example.debug.StartupTracer

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StartupTracer.mark("APP_PROCESS_CREATED")
    }
}
