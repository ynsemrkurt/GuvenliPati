package com.example.guvenlipati

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.loginPageButton).setOnClickListener {
            goFragment3()
        }

        findViewById<Button>(R.id.signUpPageButton).setOnClickListener {
            goFragment1()
        }
    }
    fun goFragment1(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, FirstSignUpFragment())
            .addToBackStack(null)
            .commit()
    }
    fun goFragment2(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, SecondSignUpFragment())
            .commit()
    }
    fun goFragment3(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, LoginFragment())
            .commit()
    }
}