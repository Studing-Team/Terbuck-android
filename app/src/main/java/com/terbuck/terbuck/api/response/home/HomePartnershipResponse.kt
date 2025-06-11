package com.terbuck.terbuck.api.response.home

data class HomePartnershipResponse(
    val list: List<PartnershipInfo>
)

data class PartnershipInfo(
    val id: Int,
    val name: String,
    val institution: String,
    val category: String,
)