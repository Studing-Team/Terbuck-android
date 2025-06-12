package com.terbuck.terbuck.api.response.terbuck

data class MapStoreListResponse(
    val list: List<MapStoreInfo>
)

data class MapStoreInfo(
    val shopId: Int,
    val category: String,
    val name: String,
    val thumbnailImage: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val benefitCount: Int
)
