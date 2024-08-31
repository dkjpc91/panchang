package com.mithilakshar.mithilapanchang.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "ringtone")
data class Ringtone(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val title: String,
    val selectedRingtone: Int,
    val dateTimeInMillis: Long
)