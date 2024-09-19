package com.example.guvenlipati.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            navigateToHomeActivity()
        }
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun navigateToFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    fun showFirstSignUpFragment() {
        navigateToFragment(FirstSignUpFragment())
    }

    fun showSecondSignUpFragment() {
        navigateToFragment(SecondSignUpFragment())
    }

    fun showLoginFragment() {
        navigateToFragment(LoginFragment())
    }

    fun showSplashFragment() {
        navigateToFragment(SplashFragment())
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
