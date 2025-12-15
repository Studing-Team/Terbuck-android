package com.terbuck.terbuck.api

import android.content.Context
import android.content.SharedPreferences

class TokenManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("access_token", accessToken)
        editor.putString("refresh_token", refreshToken)
        editor.apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    // Access 토큰 삭제
    fun deleteAccessToken() {
        val sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("access_token").commit()
        editor.apply()
    }

    // Refresh 토큰 삭제
    fun deleteRefreshToken() {
        val sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("refresh_token")
        editor.apply()
    }

    fun saveUniversity(school: String) {
        val editor = sharedPreferences.edit()
        editor.putString("school", school)
        editor.apply()
    }

    fun getUniversity(): String? {
        return sharedPreferences.getString("school", "")
    }

    fun saveIsSignUp(isSignUp: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isSignUp", isSignUp)
        editor.apply()
    }

    fun getIsSignUp(): Boolean {
        return sharedPreferences.getBoolean("isSignUp", true)
    }

    fun saveIsUniversityRegistered(isRegistered: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isRegistered", isRegistered)
        editor.apply()
    }

    fun getIsUniversityRegistered(): Boolean {
        return sharedPreferences.getBoolean("isRegistered", false)
    }

    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}