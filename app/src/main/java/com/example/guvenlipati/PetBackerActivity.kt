package com.example.guvenlipati

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView

class PetBackerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_backer)
    }

    fun goRegisterBackerFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, RegisterBackerFragment())
            .commit()
        animation()
    }

    private fun animation(){
        val fragmentContainerView: FragmentContainerView = findViewById(R.id.fragmentContainerView2)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fragmentContainerView.startAnimation(fadeIn)
    }
}