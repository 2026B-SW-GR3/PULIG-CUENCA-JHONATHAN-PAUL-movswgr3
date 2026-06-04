package com.example.redyseguridad.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.redyseguridad.R
import com.example.redyseguridad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Módulo 1: REST API
        binding.btnRestApi.setOnClickListener {
            startActivity(Intent(this, RestApiActivity::class.java))
        }

        // Módulo 3: Almacenamiento Seguro
        binding.btnSecurityStorage.setOnClickListener {
            startActivity(Intent(this, SecurityStorageActivity::class.java))
        }
    }
}
