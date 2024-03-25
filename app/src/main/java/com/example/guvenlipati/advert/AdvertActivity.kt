package com.example.guvenlipati.advert

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guvenlipati.R
import com.google.android.material.tabs.TabLayout

class AdvertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_advert)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                val position = tab.position
//                when (position) {
//                    0 ->                         // Geçmiş İlanlar Tab'ı seçildiğinde yapılacak işlemler
//                        showToast("Geçmiş İlanlar seçildi")
//
//                    1 ->                         // Aktif İlanlar Tab'ı seçildiğinde yapılacak işlemler
//                        showToast("Aktif İlanlar seçildi")
//
//                    2 ->                         // Bekleyen İlanlar Tab'ı seçildiğinde yapılacak işlemler
//                        showToast("Bekleyen İlanlar seçildi")
//                }
//            }

    }
}