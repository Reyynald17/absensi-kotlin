package com.example.absensisiswaproto

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class AbsensiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Force disable automatic force dark inversion to prevent system UiModeManager IPC queries
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Install guard against OS framework Binder SecurityExceptions (e.g. getForceInvertState cross-user check)
        setupSystemExceptionGuard()
    }

    private fun setupSystemExceptionGuard() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (isForceInvertSecurityException(throwable)) {
                Log.e("AbsensiApplication", "Caught getForceInvertState SecurityException on ${thread.name}", throwable)
            } else {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }

        // Install guard on Main Looper for framework exceptions thrown during hardware rendering / view draw
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    if (isForceInvertSecurityException(e)) {
                        Log.e("AbsensiApplication", "Handled getForceInvertState SecurityException in Main Looper", e)
                    } else {
                        throw e
                    }
                }
            }
        }
    }

    private fun isForceInvertSecurityException(throwable: Throwable?): Boolean {
        var curr = throwable
        while (curr != null) {
            val msg = curr.message
            if (curr is SecurityException) {
                if (msg == null ||
                    msg.contains("INTERACT_ACROSS_USERS") ||
                    msg.contains("INTERACT_ACROSS_USERS_FULL") ||
                    msg.contains("Call from user") ||
                    msg.contains("getForceInvertState")) {
                    return true
                }
            }
            if (msg != null) {
                if (msg.contains("INTERACT_ACROSS_USERS") ||
                    msg.contains("INTERACT_ACROSS_USERS_FULL") ||
                    msg.contains("Call from user") ||
                    msg.contains("getForceInvertState")) {
                    return true
                }
            }
            curr = curr.cause
        }
        return false
    }
}
