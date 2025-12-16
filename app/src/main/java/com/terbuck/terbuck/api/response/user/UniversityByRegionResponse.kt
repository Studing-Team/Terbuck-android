package com.terbuck.terbuck.api.response.user

data class UniversityByRegionResponse(
    val region: Region,
    val universities: List<University>,
)

data class University(
    val id: Long,
    val name: String,
    val region: Region,
    val registered: Boolean,
)

data class Region(
    val id: Long,
    val name: String,
)