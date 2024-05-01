package com.example.guvenlipati.advert

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivityAdvertBinding
import com.example.guvenlipati.databinding.ActivityHomeBinding
import com.example.guvenlipati.home.JobsSplashFragment
import com.example.guvenlipati.payment.PaymentFragment
import com.google.android.material.tabs.TabLayout

class AdvertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert)

        goPendingAdvertFragment()

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val statusType = intent.getStringExtra("statusType")

        when (statusType) {
            "pending" -> {
                goPaymentAdvertFragment()
                tabLayout.selectTab(tabLayout.getTabAt(1))
            }
            "active" -> {
                goActiveAdvertFragment()
                tabLayout.selectTab(tabLayout.getTabAt(2))
            }
            else -> {
                goPendingAdvertFragment()
                tabLayout.selectTab(tabLayout.getTabAt(0))
            }
        }


        findViewById<TabLayout>(R.id.tabLayout).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> goPendingAdvertFragment()

                    1 -> goPaymentAdvertFragment()

                    2 -> goActiveAdvertFragment()

                    3 -> goPastAdvertFragment()
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

    private fun goPastAdvertFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, PastAdvertFragment()
            )
            .commit()
    }

    private fun goActiveAdvertFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, ActiveAdvertFragment()
            )
            .commit()
    }

    private fun goPendingAdvertFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, PendingAdvertFragment()
            )
            .commit()
    }

    private fun goPaymentAdvertFragment(){
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, PaymentAdvertFragment()
            )
            .commit()
    }
}