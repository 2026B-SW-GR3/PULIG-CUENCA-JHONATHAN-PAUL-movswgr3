package com.example.redyseguridad.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson

class EncryptedSharedPreferencesManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @PublishedApi
    internal val gson = Gson()

    fun saveString(key: String, value: String) {
        encryptedSharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return encryptedSharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        encryptedSharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return encryptedSharedPreferences.getInt(key, defaultValue)
    }

    fun <T> saveObject(key: String, value: T) {
        val json = gson.toJson(value)
        saveString(key, json)
    }

    inline fun <reified T> getObject(key: String): T? {
        val json = getString(key)
        return if (json.isNotEmpty()) {
            gson.fromJson(json, T::class.java)
        } else {
            null
        }
    }

    fun clear() {
        encryptedSharedPreferences.edit().clear().apply()
    }

    fun remove(key: String) {
        encryptedSharedPreferences.edit().remove(key).apply()
    }
}
