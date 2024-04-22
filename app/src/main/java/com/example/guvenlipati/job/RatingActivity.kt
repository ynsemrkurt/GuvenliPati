package com.example.guvenlipati.job

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guvenlipati.R

class RatingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}