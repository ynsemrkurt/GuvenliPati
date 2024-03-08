package com.example.guvenlipati.home

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import com.example.guvenlipati.backer.PetBackerActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.addPet.RegisterPetActivity
import com.example.guvenlipati.splash.SplashActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth=FirebaseAuth.getInstance()
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navButton = findViewById<ImageView>(R.id.menu_nav)

        navButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        val navigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        navigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    goHomeFragment()
                    true
                }

                R.id.menu_jobs -> {
                    goJobsFragment()
                    true
                }

                R.id.menu_add_friend -> {
                    goAddPetFragment()
                    true
                }

                R.id.menu_profile -> {
                    goProfileFragment()
                    true
                }

                else -> false
            }
        }
    }


    private fun goAddPetFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, AddPetFragment()
            )
            .commit()
        animation()
    }


    fun goHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, HomeFragment()
            )
            .commit()
        animation()
    }

    fun goRegisterPetActivity(petType: String) {
        val intent = Intent(this, RegisterPetActivity::class.java)
        intent.putExtra("petType", petType)
        startActivity(intent)
    }

    fun goProfileFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, ProfileFragment()
            )
            .commit()
        animation()
    }

    fun goPetBackerActivity(){
        val intent = Intent(this, PetBackerActivity::class.java)
        startActivity(intent)
        animation()
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun animation(){
        val fragmentContainerView: FragmentContainerView = findViewById(R.id.fragmentContainerView2)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fragmentContainerView.startAnimation(fadeIn)
    }

    private fun goJobsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, JobsSplashFragment()
            )
            .commit()
        animation()
    }
}