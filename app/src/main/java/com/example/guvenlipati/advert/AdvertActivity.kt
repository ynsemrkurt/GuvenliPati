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

        setupInitialFragment()
        setupTabLayout()
        setupBackButton()
    }

    private fun setupInitialFragment() {
        val statusType = intent.getStringExtra("statusType")
        val (fragment, tabPosition) = when (statusType) {
            "pending" -> PaymentAdvertFragment() to 1
            "active" -> ActiveAdvertFragment() to 2
            else -> PendingAdvertFragment() to 0
        }
        goFragment(fragment)
        selectTab(tabPosition)
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                goFragment(
                    when (tab.position) {
                        0 -> PendingAdvertFragment()
                        1 -> PaymentAdvertFragment()
                        2 -> ActiveAdvertFragment()
                        3 -> PastAdvertFragment()
                        else -> PendingAdvertFragment()
                    }
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.backToSplash).setOnClickListener { finish() }
    }

    private fun goFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun selectTab(position: Int) {
        tabLayout.getTabAt(position)?.select()
    }
}
