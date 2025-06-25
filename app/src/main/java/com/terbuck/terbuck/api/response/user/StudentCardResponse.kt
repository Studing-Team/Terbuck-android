package com.terbuck.terbuck.api.response.user

data class StudentCardResponse(
    val name: String,
    val university: String,
    val isRegistered: Boolean,
    val studentNumber: String?,
    val imageURL: String?
)