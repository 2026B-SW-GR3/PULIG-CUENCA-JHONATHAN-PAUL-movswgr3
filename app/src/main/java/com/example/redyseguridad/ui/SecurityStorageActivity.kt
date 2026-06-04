package com.example.redyseguridad.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.redyseguridad.R
import com.example.redyseguridad.databinding.ActivitySecurityStorageBinding
import com.example.redyseguridad.viewmodel.SecurityStorageViewModel

class SecurityStorageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityStorageBinding
    private lateinit var viewModel: SecurityStorageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_security_storage)

        viewModel = ViewModelProvider(this, object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SecurityStorageViewModel(applicationContext) as T
            }
        }).get(SecurityStorageViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // SharedPreferences
        binding.btnSaveSharedPref.setOnClickListener {
            val key = binding.etKeySharedPref.text.toString()
            val value = binding.etValueSharedPref.text.toString()
            if (key.isNotEmpty() && value.isNotEmpty()) {
                viewModel.saveToSharedPreferences(key, value)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLoadSharedPref.setOnClickListener {
            val key = binding.etKeySharedPref.text.toString()
            if (key.isNotEmpty()) {
                viewModel.loadFromSharedPreferences(key)
            }
        }

        // DataStore
        binding.btnSaveDataStore.setOnClickListener {
            val key = binding.etKeyDataStore.text.toString()
            val value = binding.etValueDataStore.text.toString()
            if (key.isNotEmpty() && value.isNotEmpty()) {
                viewModel.saveToDataStore(key, value)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLoadDataStore.setOnClickListener {
            val key = binding.etKeyDataStore.text.toString()
            if (key.isNotEmpty()) {
                viewModel.loadFromDataStore(key)
            }
        }

        // EncryptedSharedPreferences
        binding.btnSaveEncrypted.setOnClickListener {
            val key = binding.etKeyEncrypted.text.toString()
            val value = binding.etValueEncrypted.text.toString()
            if (key.isNotEmpty() && value.isNotEmpty()) {
                viewModel.saveToEncryptedPreferences(key, value)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLoadEncrypted.setOnClickListener {
            val key = binding.etKeyEncrypted.text.toString()
            if (key.isNotEmpty()) {
                viewModel.loadFromEncryptedPreferences(key)
            }
        }

        // Clear all
        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllData()
        }
    }

    private fun observeViewModel() {
        viewModel.sharedPrefData.observe(this) { data ->
            binding.tvSharedPrefResult.text = "Valor: $data"
        }

        viewModel.dataStoreData.observe(this) { data ->
            binding.tvDataStoreResult.text = "Valor: $data"
        }

        viewModel.encryptedData.observe(this) { data ->
            binding.tvEncryptedResult.text = "Valor: $data (CIFRADO)"
        }

        viewModel.statusMessage.observe(this) { message ->
            binding.tvStatus.text = message
        }
    }
}
