package com.mithilakshar.mithilapanchang.Repository

import androidx.lifecycle.LiveData
import com.mithilakshar.mithilapanchang.Room.Ringtone
import com.mithilakshar.mithilapanchang.Room.RingtoneDao

class RingtoneRepository (val ringtoneDao: RingtoneDao) {

    val allringtone: LiveData<List<Ringtone>> = ringtoneDao.getallringtone()
    suspend fun insertringtone(ringtone: Ringtone) {
        ringtoneDao.insertringtone(ringtone)
    }

    suspend fun deleteringtone(ringtone: Ringtone) {
        ringtoneDao.deleteringtone(ringtone)
    }



}