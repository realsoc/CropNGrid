package com.realsoc.cropngrid.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

typealias GridUris = List<List<String>>
@Serializable
@Entity(tableName = "grids")
data class Grid(
    @PrimaryKey val id: String,
    val name: String?,
    @ColumnInfo(name = "utc_date") val utcDate: Long,
    @ColumnInfo(name = "miniature_uri_encoded") val miniatureUriEncoded: String,
    @ColumnInfo(name = "parts") val parts: GridUris
)

