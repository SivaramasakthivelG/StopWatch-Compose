package com.example.snakegame

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TimeViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    fun getLapTime(): Flow<List<String?>> {
        return dataStore.data.map {
            it.asMap().map {entry ->
                entry.value as? String
            }
        }
    }

    fun updateLapTime(timeMills: Long, key: String) {
        viewModelScope.launch {
            val dataTime = stringPreferencesKey(key)
            dataStore.edit {
                it[dataTime] = timeMills.toString()
            }
        }


    }

    fun clearAll() {
        viewModelScope.launch {
            dataStore.edit {
                it.clear()
            }
        }
    }



}