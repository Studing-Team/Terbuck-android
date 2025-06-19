package com.terbuck.terbuck.api.response.terbuck

data class StoreDetailResponse(
    val name: String,
    val shopLink: String,
    val imageList: List<String>,
    val address: String,
    val benefitCount: Int,
    val benefitList: List<Benefit>,
    val usagesList: List<Usage>?
)

data class Benefit(
    val title: String,
    val detailList: List<String>
)

data class Usage(
    val introduction: String
)