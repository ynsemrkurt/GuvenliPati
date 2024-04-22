package com.example.guvenlipati.myjobs

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guvenlipati.R
import com.example.guvenlipati.advert.PastAdvertFragment
import com.example.guvenlipati.advert.PendingAdvertFragment
import com.google.android.material.tabs.TabLayout

class MyJobsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_jobs)

       goPastJobFragment()

        findViewById<TabLayout>(R.id.tabLayout).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> goActiveJobFragment()

                    1 -> goPastJobFragment()
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

        findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun goPastJobFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, PastJobFragment()
            )
            .commit()
    }

    private fun goActiveJobFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, ActiveJobsFragment()
            )
            .commit()
    }
}