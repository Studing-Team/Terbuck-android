package com.terbuck.terbuck.utils

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.terbuck.terbuck.BuildConfig

class GlobalApplication : Application() {

    companion object {
        lateinit var mixpanel: MixpanelAPI
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

        // MixPanel 초기화
        mixpanel = MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_KEY, false)
    }
}