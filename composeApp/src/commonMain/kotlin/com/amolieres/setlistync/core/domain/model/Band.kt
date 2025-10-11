package com.amolieres.setlistync.core.domain.model

data class Band(
    val id: String,
    val name: String,
    val members: List<Member> = emptyList()
)