package com.example.guvenlipati

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun showToast(message: String) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        auth = FirebaseAuth.getInstance()

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val progressCard = view.findViewById<CardView>(R.id.progressCard)
        val buttonPaw = view.findViewById<ImageView>(R.id.buttonPaw)
        val userEmail = view.findViewById<EditText>(R.id.editTextEmail)
        val userPassword = view.findViewById<EditText>(R.id.editTextPassword)


        loginButton.setOnClickListener {

            if (userEmail.text.toString().isEmpty() || userPassword.text.toString().isEmpty()) {
                showToast("Hiçbir alan boş bırakılamaz!")
            } else {

                loginButton.visibility = View.INVISIBLE
                progressCard.visibility = View.VISIBLE
                buttonPaw.visibility = View.INVISIBLE

                auth.signInWithEmailAndPassword(
                    userEmail.text.toString(),
                    userPassword.text.toString()
                )
                    .addOnCompleteListener()
                    {
                        if (it.isSuccessful) {
                            (activity as MainActivity).goHomeActivity()
                            userEmail.setText("")
                            userPassword.setText("")
                        } else {
                            userPassword.setTextColor(Color.RED)
                            val shake= AnimationUtils.loadAnimation(requireContext(),R.anim.shake)
                            userPassword.startAnimation(shake)
                            Handler(Looper.getMainLooper()).postDelayed({
                                userPassword.text.clear()
                                userPassword.setTextColor(Color.BLACK)
                            }, 500)
                        }
                        loginButton.visibility = View.VISIBLE
                        progressCard.visibility = View.INVISIBLE
                        buttonPaw.visibility = View.VISIBLE
                    }
            }

        }

        view.findViewById<ImageButton>(R.id.lockPassword).setOnClickListener {
            val userPassword = view.findViewById<EditText>(R.id.editTextPassword)
            val lockPassword = view.findViewById<ImageButton>(R.id.lockPassword)

            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                userPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                lockPassword.setImageResource(R.drawable.password_eye_ico)
            } else {
                userPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                lockPassword.setImageResource(R.drawable.secret_password_eye_ico)
            }

        }

        view.findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
            (activity as MainActivity).goSplashFragment()
        }

        view.findViewById<TextView>(R.id.textView).setOnClickListener {

            if (userEmail.text.toString().isEmpty()) {
                showToast("Lütfen email adresinizi giriniz!")
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(userEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Lütfen email kutunuzu kontrol ediniz!")
                    } else {
                        showToast("Başarısız!")
                    }
                }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).goSplashFragment()
        }


    }
}