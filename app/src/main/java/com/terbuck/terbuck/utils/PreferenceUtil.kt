package com.terbuck.terbuck.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi

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

    // 최근 검색어 저장
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun saveRecentSearchLimited(context: Context, keyword: String) {
        val prefs = context.getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val existing = prefs.getString("keywords", "") ?: ""
        val list = existing.split("|").filter { it.isNotBlank() }.toMutableList()

        list.remove(keyword)
        list.add(0, keyword)

        if (list.size > 10) list.removeAt(list.lastIndex)

        prefs.edit().putString("keywords", list.joinToString("|")).apply()
    }

    fun getRecentSearchesLimited(context: Context): List<String> {
        val prefs = context.getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val data = prefs.getString("keywords", "") ?: ""
        return data.split("|").filter { it.isNotBlank() }
    }


    fun removeRecentSearch(context: Context, keyword: String) {
        val prefs = context.getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val existing = prefs.getString("keywords", "") ?: ""
        val list = existing.split("|").filter { it.isNotBlank() }.toMutableList()

        list.remove(keyword)

        prefs.edit().putString("keywords", list.joinToString("|")).apply()
    }
}