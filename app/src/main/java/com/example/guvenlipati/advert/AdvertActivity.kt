package com.example.guvenlipati.advert

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivityAdvertBinding
import com.example.guvenlipati.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout

class AdvertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert)


        findViewById<TabLayout>(R.id.tabLayout).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> showToast("Geçmiş İlanlar seçildi")

                    1 -> showToast("Aktif İlanlar seçildi")

                    2 -> showToast("Bekleyen İlanlar seçildi")
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}