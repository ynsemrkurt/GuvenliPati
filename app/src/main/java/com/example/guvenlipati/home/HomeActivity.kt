package com.example.guvenlipati.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.advert.AdvertActivity
import com.example.guvenlipati.databinding.ActivityHomeBinding
import com.example.guvenlipati.myjobs.MyJobsActivity
import com.example.guvenlipati.splash.SplashActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goFragment(HomeFragment())

        auth = FirebaseAuth.getInstance()

        binding.menuNav.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val xId = auth.currentUser?.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$xId")

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    deleteToken()
                    true
                }

                R.id.jobs -> {
                    goActivity(AdvertActivity())
                    true
                }

                R.id.myJobs -> {
                    goActivity(MyJobsActivity())
                    true
                }

                R.id.rating -> {
                    goActivity(RatingActivity())
                    true
                }

                R.id.settings -> {
                    goActivity(SettingsActivity())
                    true
                }

                else -> false
            }
        }


        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    goFragment(HomeFragment())
                    true
                }

                R.id.menu_jobs -> {
                    goFragment(JobsSplashFragment())
                    true
                }

                R.id.menu_add_friend -> {
                    goFragment(AddPetFragment())
                    true
                }

                R.id.menu_profile -> {
                    goFragment(ProfileFragment())
                    true
                }

                R.id.menu_chats -> {
                    goFragment(ChatListFragment())
                    true
                }

                else -> false
            }
        }

    }

    fun goSelectAddPetFragment() {
        binding.bottomNavigation.selectedItemId = R.id.menu_add_friend
    }

    fun goFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, fragment
            )
            .commit()
    }

    fun goActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun deleteToken() {
        FirebaseMessaging.getInstance().deleteToken()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.signOut()
                    goActivity(SplashActivity())
                    finish()
                } else {
                    showToast("Çıkış Yaparken Hata Oluştu!")
                }
            }
    }
}