package com.sama.android

import android.content.Context
import android.content.Context.MODE_PRIVATE

class Session(context: Context) {

    val sharedPreferences = context.getSharedPreferences("SESSION", MODE_PRIVATE)

    val isLoggedIn: Boolean
        get() = sharedPreferences.getString("SESSION_TOKEN", null) != null

    val sessionToken: String?
        get() = sharedPreferences.getString("SESSION_TOKEN", null)

    fun setSessionToken(token: String?): Session {
        sharedPreferences.edit().putString("SESSION_TOKEN", token).commit()
        return this
    }
}