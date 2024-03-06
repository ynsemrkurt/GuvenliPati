package com.example.guvenlipati

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth


class FirstSignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signUpButton = view.findViewById<Button>(R.id.signUpButton)
        val progressCard = view.findViewById<CardView>(R.id.progressCard)
        val buttonPaw = view.findViewById<ImageView>(R.id.buttonPaw)

        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {

            auth = FirebaseAuth.getInstance()

            val userEmail = view.findViewById<EditText>(R.id.editTextEmail)
            val userPassword = view.findViewById<EditText>(R.id.editTextPassword)
            val userConfirmPassword = view.findViewById<EditText>(R.id.editTextConfirmPassword)

            if (userEmail.text.toString().isEmpty() || !controlEmail(userEmail.text.toString())) {
                showToast("Hatalı ya da eksik E-posta!")
                return@setOnClickListener
            }

            if (userPassword.text.toString().length < 8) {
                showToast("Şifre 8 karakterden kısa olamaz!")
                return@setOnClickListener
            }

            if (userPassword.text.toString() != userConfirmPassword.text.toString()) {
                showToast("Şifreler uyuşmuyor!")
                userPassword.setTextColor(Color.RED)
                userConfirmPassword.setTextColor(Color.RED)
                val shake= AnimationUtils.loadAnimation(requireContext(),R.anim.shake)
                userPassword.startAnimation(shake)
                userConfirmPassword.startAnimation(shake)
                Handler(Looper.getMainLooper()).postDelayed({
                    userPassword.text.clear()
                    userConfirmPassword.text.clear()
                    userPassword.setTextColor(Color.BLACK)
                    userConfirmPassword.setTextColor(Color.BLACK)
                }, 500)
                return@setOnClickListener
            }

            signUpButton.visibility = View.INVISIBLE
            progressCard.visibility = View.VISIBLE
            buttonPaw.visibility = View.INVISIBLE

            auth.createUserWithEmailAndPassword(
                userEmail.text.toString(),
                userPassword.text.toString()
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        (activity as MainActivity).goSecondSignUpFragment()
                    } else {
                        showToast("Farklı E-posta Giriniz!")
                    }
                    signUpButton.visibility = View.VISIBLE
                    progressCard.visibility = View.INVISIBLE
                    buttonPaw.visibility = View.VISIBLE
                }
        }

        view.findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
            (activity as MainActivity).goSplashFragment()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).goSplashFragment()
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun controlEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}