package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bands")
data class BandEntity(
    @PrimaryKey val id: String,
    val name: String
)
