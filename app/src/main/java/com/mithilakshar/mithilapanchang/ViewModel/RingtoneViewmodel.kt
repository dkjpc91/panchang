package com.mithilakshar.mithilapanchang.ViewModel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import com.mithilakshar.mithilapanchang.Repository.RingtoneRepository
import com.mithilakshar.mithilapanchang.Room.Ringtone

class RingtoneViewmodel (private val repository: RingtoneRepository): ViewModel() {

    val allringtone: LiveData<List<Ringtone>> = repository.allringtone

    fun insertringtone(ringtone: Ringtone) = viewModelScope.launch {
        repository.insertringtone(ringtone)
    }

    fun deleteringtone(ringtone: Ringtone) = viewModelScope.launch {
        repository.deleteringtone(ringtone)
    }

    class RingtoneViewmodelFactory(private val repository: RingtoneRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(RingtoneViewmodel::class.java))
            {
                @Suppress("UNCHECKED_CAST")
                return RingtoneViewmodel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}



