package com.example.guvenlipati.splash

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentFirstSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class FirstSignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentFirstSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            signUpButton.setOnClickListener { handleSignUp() }
            backToSplash.setOnClickListener { navigateToSplashFragment() }
        }

        setupBackPressedListener()
    }

    private fun handleSignUp() {
        with(binding) {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            when {
                email.isEmpty() || !controlEmail(email) -> showToast("Hatalı ya da eksik E-posta!")
                password.length < 8 -> showToast("Şifre 8 karakterden kısa olamaz!")
                password != confirmPassword -> {
                    showToast("Şifreler uyuşmuyor!")
                    highlightMismatchPasswords()
                }
                else -> signUpUser(email, password)
            }
        }
    }

    private fun signUpUser(email: String, password: String) {
        with(binding) {
            signUpButton.visibility = View.INVISIBLE
            progressCard.visibility = View.VISIBLE
            buttonPaw.visibility = View.INVISIBLE
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                (activity as? SplashActivity)?.showSecondSignUpFragment()
            } else {
                showToast("Farklı E-posta Giriniz!")
            }
            resetButtonVisibility()
        }
    }

    private fun navigateToSplashFragment() {
        (activity as? SplashActivity)?.showSplashFragment()
    }

    private fun setupBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateToSplashFragment()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun controlEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun highlightMismatchPasswords() {
        with(binding) {
            editTextPassword.setTextColor(Color.RED)
            editTextConfirmPassword.setTextColor(Color.RED)
            val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            editTextPassword.startAnimation(shake)
            editTextConfirmPassword.startAnimation(shake)
            Handler(Looper.getMainLooper()).postDelayed({
                editTextPassword.text.clear()
                editTextConfirmPassword.text.clear()
                editTextPassword.setTextColor(Color.BLACK)
                editTextConfirmPassword.setTextColor(Color.BLACK)
            }, 500)
        }
    }

    private fun resetButtonVisibility() {
        with(binding) {
            signUpButton.visibility = View.VISIBLE
            progressCard.visibility = View.INVISIBLE
            buttonPaw.visibility = View.VISIBLE
        }
    }
}
