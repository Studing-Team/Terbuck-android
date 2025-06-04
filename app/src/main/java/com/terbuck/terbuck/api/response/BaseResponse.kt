package com.terbuck.terbuck.api.response

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)