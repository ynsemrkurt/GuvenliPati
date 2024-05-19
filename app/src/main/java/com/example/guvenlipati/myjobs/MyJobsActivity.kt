package com.example.guvenlipati.myjobs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivityMyJobsBinding
import com.google.android.material.tabs.TabLayout

class MyJobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyJobsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        handleIntent()
        setupListeners()
    }

    private fun setupTabs() {
        with(binding.tabLayout) {
            addTab(newTab().setText("Aktif İşler"))
            addTab(newTab().setText("Bekleyen İşler"))
            addTab(newTab().setText("Yapılmış İşler"))
        }
    }

    private fun handleIntent() {
        val status = intent.getBooleanExtra("status", false)
        if (status) {
            goPastJobFragment()
            binding.tabLayout.getTabAt(2)?.select()
        } else {
            goActiveJobFragment()
            binding.tabLayout.getTabAt(0)?.select()
        }
    }

    private fun setupListeners() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> goActiveJobFragment()
                    1 -> goPendingJobFragment()
                    2 -> goPastJobFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.backToSplash.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun goPastJobFragment() {
        replaceFragment(PastJobFragment())
    }

    private fun goActiveJobFragment() {
        replaceFragment(ActiveJobsFragment())
    }

    private fun goPendingJobFragment() {
        replaceFragment(PendingJobFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}
