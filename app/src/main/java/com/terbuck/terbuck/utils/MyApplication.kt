package com.terbuck.terbuck.utils

import android.app.Application

class MyApplication : Application() {
    companion object {

        lateinit var preferences: PreferenceUtil

        var latitude: String? = null
        var longitude: String? = null

        var isRegisterStudentCard = false
        var isPendingStudentCard = false

        var isUniversityChanged = false
        var isStudentCardChanged = false

        var selectedStoreId: Int? = null
    }
}