package com.example.guvenlipati.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.example.guvenlipati.R
import com.example.guvenlipati.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            goHomeActivity()
            finish()
        }
    }
    fun goFirstSignUpFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, FirstSignUpFragment
                    ()
            )
            .commit()
    }

    fun goSecondSignUpFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, SecondSignUpFragment())
            .commit()
        animation()
    }

    fun goLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, LoginFragment())
            .commit()
        animation()
    }

    fun goSplashFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, SplashFragment())
            .commit()
        animation()
    }

    fun goHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun animation(){
        val fragmentContainerView: FragmentContainerView = findViewById(R.id.fragmentContainerView)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fragmentContainerView.startAnimation(fadeIn)
    }


}