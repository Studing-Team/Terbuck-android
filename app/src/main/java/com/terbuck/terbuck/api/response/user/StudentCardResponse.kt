package com.terbuck.terbuck.api.response.user

data class StudentCardResponse(
    val studentNumber: String,
    val name: String,
    val imageURL: String?
)
