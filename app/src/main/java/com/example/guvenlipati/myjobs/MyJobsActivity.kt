package com.example.guvenlipati.myjobs

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.R
import com.google.android.material.tabs.TabLayout

class MyJobsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_jobs)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val status = intent.getBooleanExtra("status", false)

        if (status) {
            goPastJobFragment()
            tabLayout.selectTab(tabLayout.getTabAt(2))
        }
        else {
            goActiveJobFragment()
            tabLayout.selectTab(tabLayout.getTabAt(0))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> goActiveJobFragment()
                    1 -> goPendingJobFragment()
                    2 -> goPastJobFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
            onBackPressed()
            finish()
        }
    }


    private fun goPastJobFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, PastJobFragment())
            .commit()
    }

    private fun goActiveJobFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ActiveJobsFragment())
            .commit()
    }

    private fun goPendingJobFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, PendingJobFragment())
            .commit()
    }
}
