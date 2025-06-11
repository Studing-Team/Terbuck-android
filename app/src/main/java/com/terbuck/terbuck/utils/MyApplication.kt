package com.terbuck.terbuck.utils

import android.app.Application

class MyApplication : Application() {
    companion object {

        lateinit var preferences: PreferenceUtil
    }
}