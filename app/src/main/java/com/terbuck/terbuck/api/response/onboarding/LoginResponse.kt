package com.terbuck.terbuck.api.response.onboarding

data class LoginResponse(
    val redirect: Boolean,
    val id: Int,
    val accessToken: String?,
    val refreshToken: String?,
)