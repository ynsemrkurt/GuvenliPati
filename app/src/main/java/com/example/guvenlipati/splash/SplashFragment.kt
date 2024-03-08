package com.example.guvenlipati.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R

class SplashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.loginPageButton).setOnClickListener {
            (activity as SplashActivity).goLoginFragment()
        }

        view.findViewById<Button>(R.id.signUpPageButton).setOnClickListener {
            (activity as SplashActivity).goFirstSignUpFragment()
        }

    }
}