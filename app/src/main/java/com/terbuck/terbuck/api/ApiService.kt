package com.terbuck.terbuck.api

import com.terbuck.terbuck.api.request.onboarding.LoginRequest
import com.terbuck.terbuck.api.request.onboarding.SignUpRequest
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.onboarding.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // OAuth로그인
    @POST("/auth/kakao")
    fun login(
        @Body request: LoginRequest
    ): Call<BaseResponse<LoginResponse>>

    // 회원가입
    @POST("/member/signin")
    fun signUp(
        @Header("authorization") token: String,
        @Body request: SignUpRequest
    ): Call<BaseResponse<String>>
}