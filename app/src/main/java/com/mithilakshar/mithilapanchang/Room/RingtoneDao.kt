package com.mithilakshar.mithilapanchang.Room
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RingtoneDao  {
    @Insert
    suspend fun insertringtone(ringtone: Ringtone)
    @Delete
    suspend fun deleteringtone(ringtone: Ringtone)

    @Query("SELECT * FROM ringtone")
    fun getallringtone(): LiveData<List<Ringtone>>
}