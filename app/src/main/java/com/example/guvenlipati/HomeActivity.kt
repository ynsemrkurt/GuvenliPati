package com.example.guvenlipati

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navigationBar=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        navigationBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_home -> {
                    goHomeFragment()
                    true
                }
                R.id.menu_add_friend -> {
                    goAddPetFragment()
                    true
                }
                else -> false
            }
        }
    }


    fun goAddPetFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, AddPetFragment()
            )
            .commit()
    }
    fun goHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, HomeFragment()
            )
            .commit()
    }

    fun goRegisterPetActivity(petType: String) {
        val intent=Intent(this,RegisterPetActivity::class.java)
        intent.putExtra("petType", petType)
        startActivity(intent)
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            //finishAffinity() uygulamayı arkaplanda çalıştırmaya devam eder System.exit(0) ise bellekten de siler
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Çıkmak için tekrar geri tuşuna basın", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
    //EMREYİ YEDİM
}