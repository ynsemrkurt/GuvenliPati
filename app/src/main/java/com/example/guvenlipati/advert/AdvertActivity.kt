package com.example.guvenlipati.advert

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.google.android.material.tabs.TabLayout

class AdvertActivity : AppCompatActivity() {

    private val tabLayout: TabLayout by lazy { findViewById(R.id.tabLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert)

        goFragment(PendingAdvertFragment())

        val statusType = intent.getStringExtra("statusType")

        when (statusType) {
            "pending" -> {
                goFragment(PaymentAdvertFragment())
                selectTab(1)
            }

            "active" -> {
                goFragment(ActiveAdvertFragment())
                selectTab(2)
            }

            else -> {
                goFragment(PendingAdvertFragment())
                selectTab(0)
            }
        }


        findViewById<TabLayout>(R.id.tabLayout).addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                goFragment(
                    when (position) {
                        0 -> PendingAdvertFragment()

                        1 -> PaymentAdvertFragment()

                        2 -> ActiveAdvertFragment()

                        3 -> PastAdvertFragment()

                        else -> PendingAdvertFragment()
                    }
                )
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

        findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
            finish()
        }
    }

    private fun goFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun selectTab(position: Int) {
        tabLayout.selectTab(tabLayout.getTabAt(position))
    }
}