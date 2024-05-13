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
        setupSignUpButton()
        setupBackToSplashButton()
        setupBackPressedListener()
    }

    private fun setupSignUpButton() {
        binding.signUpButton.setOnClickListener {
            if (binding.editTextEmail.text.toString().trim().isEmpty() || !controlEmail(binding.editTextEmail.text.toString().trim())) {
                showToast("Hatalı ya da eksik E-posta!")
                return@setOnClickListener
            }

            if (binding.editTextPassword.text.toString().trim().length < 8) {
                showToast("Şifre 8 karakterden kısa olamaz!")
                return@setOnClickListener
            }

            if (binding.editTextPassword.text.toString().trim() != binding.editTextConfirmPassword.text.toString().trim()) {
                showToast("Şifreler uyuşmuyor!")
                highlightMismatchPasswords()
                return@setOnClickListener
            }

            binding.signUpButton.visibility = View.INVISIBLE
            binding.progressCard.visibility = View.VISIBLE
            binding.buttonPaw.visibility = View.INVISIBLE

            auth.createUserWithEmailAndPassword(
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    (activity as SplashActivity).goSecondSignUpFragment()
                } else {
                    showToast("Farklı E-posta Giriniz!")
                }
                resetButtonVisibility()
            }
        }
    }

    private fun setupBackToSplashButton() {
        binding.backToSplash.setOnClickListener {
            (activity as SplashActivity).goSplashFragment()
        }
    }

    private fun setupBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as SplashActivity).goSplashFragment()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun controlEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun highlightMismatchPasswords() {
        binding.editTextPassword.setTextColor(Color.RED)
        binding.editTextConfirmPassword.setTextColor(Color.RED)
        val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        binding.editTextPassword.startAnimation(shake)
        binding.editTextConfirmPassword.startAnimation(shake)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.editTextPassword.text.clear()
            binding.editTextConfirmPassword.text.clear()
            binding.editTextPassword.setTextColor(Color.BLACK)
            binding.editTextConfirmPassword.setTextColor(Color.BLACK)
        }, 500)
    }

    private fun resetButtonVisibility() {
        binding.signUpButton.visibility = View.VISIBLE
        binding.progressCard.visibility = View.INVISIBLE
        binding.buttonPaw.visibility = View.VISIBLE
    }
}
