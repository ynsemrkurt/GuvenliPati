package com.example.guvenlipati.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.guvenlipati.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        with(binding) {
            loginPageButton.setOnClickListener {
                navigateToLoginFragment()
            }
            signUpPageButton.setOnClickListener {
                navigateToFirstSignUpFragment()
            }
        }
    }

    private fun navigateToLoginFragment() {
        (activity as? SplashActivity)?.showLoginFragment()
    }

    private fun navigateToFirstSignUpFragment() {
        (activity as? SplashActivity)?.showFirstSignUpFragment()
    }
}
