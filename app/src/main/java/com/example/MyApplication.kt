package com.example

import android.app.Application
import android.app.Activity
import android.os.Bundle
import com.example.debug.StartupTracer

class MyApplication : Application() {
    companion object {
        var isAppInForeground: Boolean = false
            private set
        private var activeActivities = 0
    }

    override fun onCreate() {
        super.onCreate()
        StartupTracer.mark("APP_PROCESS_CREATED")
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {
                activeActivities++
                isAppInForeground = activeActivities > 0
            }
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
                activeActivities--
                isAppInForeground = activeActivities > 0
            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
