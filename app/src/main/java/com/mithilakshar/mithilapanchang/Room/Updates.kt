package com.mithilakshar.mithilapanchang.Room
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class Updates(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val fileName: String,
    var uniqueString: String
)