package com.terbuck.terbuck.api.response.user

data class RefreshTokenResponse(
    val accessToken: String?,
    val refreshToken: String?
)