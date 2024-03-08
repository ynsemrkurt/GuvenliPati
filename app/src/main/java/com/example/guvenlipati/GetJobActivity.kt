package com.example.guvenlipati

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class GetJobActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_job)

        val backToHome = findViewById<ImageButton>(R.id.backToHome)

        backToHome.setOnClickListener{
            super.onBackPressed()
        }

    }
}