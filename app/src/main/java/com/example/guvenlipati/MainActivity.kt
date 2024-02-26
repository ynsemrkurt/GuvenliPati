package com.example.guvenlipati

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth=FirebaseAuth.getInstance()

        if (auth.currentUser!=null){
            goHomeActivity()
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
        }

        fun goLoginFragment() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, LoginFragment())
                .commit()
        }

        fun goSplashFragment() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SplashFragment())
                .commit()
        }

        fun goHomeActivity() {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }
    }