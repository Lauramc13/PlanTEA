package com.example.plantea

import android.app.Application
import android.content.Intent

class PlanTEAApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (throwable is NullPointerException &&
                throwable.stackTrace.any { it.className.contains("ConfigurationController") }
            ) {
                val intent = packageManager.getLaunchIntentForPackage(packageName)!!
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}