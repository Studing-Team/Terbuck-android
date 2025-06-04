package com.terbuck.terbuck.api.response.onboarding

data class LoginResponse(
    val redirect: Boolean,
    val accessToken: String?,
    val refreshToken: String?,
)