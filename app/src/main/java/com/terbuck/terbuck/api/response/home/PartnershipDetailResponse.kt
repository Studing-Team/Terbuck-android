package com.terbuck.terbuck.api.response.home

data class PartnershipDetailResponse(
    val name: String,
    val institution: String,
    val imageList: List<String>?,
    val detail: String?,
    val snsLink: String?
)
