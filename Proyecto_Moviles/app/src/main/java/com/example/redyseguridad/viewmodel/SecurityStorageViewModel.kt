package com.example.redyseguridad.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redyseguridad.storage.DataStoreManager
import com.example.redyseguridad.storage.EncryptedSharedPreferencesManager
import com.example.redyseguridad.storage.SharedPreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecurityStorageViewModel(context: Context) : ViewModel() {

    private val sharedPreferencesManager = SharedPreferencesManager(context)
    private val dataStoreManager = DataStoreManager(context)
    private val encryptedSharedPreferencesManager = EncryptedSharedPreferencesManager(context)

    // LiveData para SharedPreferences
    private val _sharedPrefData = MutableLiveData<String>()
    val sharedPrefData: LiveData<String> = _sharedPrefData

    // LiveData para DataStore
    private val _dataStoreData = MutableLiveData<String>()
    val dataStoreData: LiveData<String> = _dataStoreData

    // LiveData para EncryptedSharedPreferences
    private val _encryptedData = MutableLiveData<String>()
    val encryptedData: LiveData<String> = _encryptedData

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    // SharedPreferences Operations
    fun saveToSharedPreferences(key: String, value: String) {
        viewModelScope.launch {
            try {
                sharedPreferencesManager.saveString(key, value)
                _statusMessage.value = "Guardado en SharedPreferences: $value"
                _sharedPrefData.value = value
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun loadFromSharedPreferences(key: String) {
        viewModelScope.launch {
            try {
                val value = sharedPreferencesManager.getString(key)
                _sharedPrefData.value = value
                _statusMessage.value = "Cargado de SharedPreferences: $value"
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    // DataStore Operations
    fun saveToDataStore(key: String, value: String) {
        viewModelScope.launch {
            try {
                dataStoreManager.saveString(key, value)
                _statusMessage.value = "Guardado en DataStore: $value"
                _dataStoreData.value = value
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun loadFromDataStore(key: String) {
        viewModelScope.launch {
            try {
                dataStoreManager.getStringFlow(key)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.Lazily,
                        initialValue = ""
                    )
                    .collect { value ->
                        _dataStoreData.value = value
                        _statusMessage.value = "Cargado de DataStore: $value"
                    }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    // EncryptedSharedPreferences Operations
    fun saveToEncryptedPreferences(key: String, value: String) {
        viewModelScope.launch {
            try {
                encryptedSharedPreferencesManager.saveString(key, value)
                _statusMessage.value = "Guardado en EncryptedSharedPreferences (Cifrado)"
                _encryptedData.value = value
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun loadFromEncryptedPreferences(key: String) {
        viewModelScope.launch {
            try {
                val value = encryptedSharedPreferencesManager.getString(key)
                _encryptedData.value = value
                _statusMessage.value = "Cargado de EncryptedSharedPreferences (Cifrado)"
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    // Clear all data
    fun clearAllData() {
        viewModelScope.launch {
            try {
                sharedPreferencesManager.clear()
                dataStoreManager.clear()
                encryptedSharedPreferencesManager.clear()
                _sharedPrefData.value = ""
                _dataStoreData.value = ""
                _encryptedData.value = ""
                _statusMessage.value = "Todos los datos han sido eliminados"
            } catch (e: Exception) {
                _statusMessage.value = "Error al limpiar datos: ${e.message}"
            }
        }
    }
}
