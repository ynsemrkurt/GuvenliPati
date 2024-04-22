package com.example.guvenlipati.myjobs

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivityRatingBinding

class RatingActivity : AppCompatActivity() {

    lateinit var binding: ActivityRatingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToSplash.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}