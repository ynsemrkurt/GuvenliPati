package com.example.guvenlipati

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun goFragment1() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, FirstSignUpFragment
                    ()
            )
            .commit()
    }

    fun goFragment2() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, SecondSignUpFragment())
            .commit()
    }

    fun goFragment3() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, LoginFragment())
            .commit()
    }
}