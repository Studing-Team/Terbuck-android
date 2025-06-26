package com.terbuck.terbuck.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun setIsFirst(value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean("isFirst", value)
        editor.apply()
    }

    fun getIsFirst(): Boolean {
        return preferences.getBoolean("isFirst", true)
    }

    fun setIsFirstLoginView(value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean("isFirstLoginView", value)
        editor.apply()
    }

    fun getIsFirstLoginView(): Boolean {
        return preferences.getBoolean("isFirstLoginView", true)
    }

    fun setFCMToken(token: String) {
        preferences.edit().putString("FCM_TOKEN", token).apply()
    }

    fun getFCMToken(): String? =
        preferences.getString("FCM_TOKEN", null)
}