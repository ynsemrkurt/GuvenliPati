package com.example.guvenlipati

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
    fun goFragment1(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView3, FirstSignUpFragment
                ())
            .commit()
    }
    fun goFragment2(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView3, SecondSignUpFragment())
            .commit()
    }
    fun goFragment3(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView3, LoginFragment())
            .commit()
    }
}