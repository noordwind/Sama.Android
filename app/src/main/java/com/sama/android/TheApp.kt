package com.sama.android

import android.app.Application
import android.content.Context
import android.content.Intent
import com.sama.android.login.LoginActivity


class TheApp : Application() {

    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
