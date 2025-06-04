package com.terbuck.terbuck.api.response.home

data class HomeStoreResponse(
    val list: List<StoreInfo>
)

data class StoreInfo(
    val shopId: Int,
    val category: String,
    val name: String,
    val address: String,
    val benefitList: List<StoreBenefit>
)

data class StoreBenefit(
    val title: String,
    val detailList: List<String>
)
