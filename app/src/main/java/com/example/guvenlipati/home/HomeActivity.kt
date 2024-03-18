package com.example.guvenlipati.home

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.guvenlipati.backer.PetBackerActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.addPet.RegisterPetActivity
import com.example.guvenlipati.databinding.ActivityHomeBinding
import com.example.guvenlipati.splash.SplashActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val NOTIFICATION_PERMISSION = arrayOf(
                Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.POST_NOTIFICATIONS
            )
            appPermissionLauncher.launch(NOTIFICATION_PERMISSION)
        }

        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goHomeFragment()

        auth = FirebaseAuth.getInstance()

        binding.menuNav.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }

                else -> false
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
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

                R.id.menu_chats -> {
                    goChatListFragment()
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
    }


    private fun goHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, HomeFragment()
            )
            .commit()
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
    }

    fun goPetBackerActivity() {
        val intent = Intent(this, PetBackerActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goJobsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, JobsSplashFragment()
            )
            .commit()
    }

    private fun goChatListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView2, ChatListFragment()
            )
            .commit()
    }

    private val appPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            //Zorunlu izin olmadığı için birşey yazmadık...
        }
}