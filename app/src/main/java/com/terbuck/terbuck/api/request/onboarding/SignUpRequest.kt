package com.terbuck.terbuck.api.request.onboarding

data class SignUpRequest(
    val university: String,
    val collegeId: Long
)