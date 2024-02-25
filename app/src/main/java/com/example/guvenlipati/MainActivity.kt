package com.example.guvenlipati

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
    }