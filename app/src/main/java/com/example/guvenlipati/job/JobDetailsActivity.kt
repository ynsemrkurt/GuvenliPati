package com.example.guvenlipati.job

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.R

class JobDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        val jobId = intent.getStringExtra("job")
        val fragment = JobDetailsFragment()
        val args = Bundle()
        args.putString("jobId", jobId)
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()

        findViewById<ImageButton>(R.id.backToHome).setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}
