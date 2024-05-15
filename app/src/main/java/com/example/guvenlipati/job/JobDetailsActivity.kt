package com.example.guvenlipati.job

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.databinding.ActivityJobDetailsBinding

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jobId = intent.getStringExtra("job")
        val fragment = JobDetailsFragment()
        val args = Bundle().apply {
            putString("jobId", jobId)
        }
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, fragment)
            .commit()

        binding.backToHome.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}