package com.terbuck.terbuck.api

import com.terbuck.terbuck.api.request.onboarding.LoginRequest
import com.terbuck.terbuck.api.request.onboarding.SignUpRequest
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.home.HomePartnershipResponse
import com.terbuck.terbuck.api.response.home.HomeStoreResponse
import com.terbuck.terbuck.api.response.home.PartnershipDetailResponse
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

    // 홈화면 제휴 업체 정보 조회
    @GET("/shops/home")
    fun getHomeStoreInfo(
        @Header("authorization") token: String,
        @Query("university") university: String,
        @Query("category") category: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?
    ): Call<BaseResponse<HomeStoreResponse>>

    // 홈화면 파트너십 정보 조회
    @GET("/partnership/home")
    fun getHomePartnershipInfo(
        @Header("authorization") token: String,
        @Query("university") university: String
    ): Call<BaseResponse<HomePartnershipResponse>>

    // 홈화면 새로운 파트너십 정보 조회
    @GET("/partnership/home_new")
    fun getHomePartnershipNewInfo(
        @Header("authorization") token: String,
        @Query("university") university: String
    ): Call<BaseResponse<HomePartnershipResponse>>

    // 파트너십 상세 정보 조회
    @GET("/partnership/{partnership_id}")
    fun getPartnershipDetailInfo(
        @Header("authorization") token: String,
        @Path("partnership_id") partnershipId: Int
    ): Call<BaseResponse<PartnershipDetailResponse>>
}