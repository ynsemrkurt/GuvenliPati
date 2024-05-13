package com.example.guvenlipati.job

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.databinding.ActivityGetJobBinding

class GetJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGetJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToHome.setOnClickListener {
            super.onBackPressed()
        }
    }
}