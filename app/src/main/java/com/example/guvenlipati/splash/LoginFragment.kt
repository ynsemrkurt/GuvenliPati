package com.example.guvenlipati.splash

import android.app.AlertDialog
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
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setupLoginButton()
        setupLockPasswordButton()
        setupBackToSplashButton()
        setupForgotPasswordTextView()
        setupBackPressedListener()
    }

    private fun setupLoginButton() {
        with(binding) {
            loginButton.setOnClickListener {
                if (editTextEmail.text.isEmpty() || editTextPassword.text.isEmpty()) {
                    showToast("Hiçbir alan boş bırakılamaz!")
                } else {
                    loginButton.visibility = View.INVISIBLE
                    progressCard.visibility = View.VISIBLE
                    buttonPaw.visibility = View.INVISIBLE
                    auth.signInWithEmailAndPassword(
                        editTextEmail.text.toString(),
                        editTextPassword.text.toString()
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            (activity as SplashActivity).goHomeActivity()
                            editTextEmail.setText("")
                            editTextPassword.setText("")
                        } else {
                            errorEdit(editTextPassword)
                        }
                        resetButtonVisibility()
                    }.addOnFailureListener {
                        showToast("Hatalı Giriş Bilgileri!")
                        resetButtonVisibility()
                    }
                }
            }
        }
    }

    private fun setupLockPasswordButton() {
        with(binding) {
            lockPassword.setOnClickListener {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    editTextPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    lockPassword.setImageResource(R.drawable.password_eye_ico)
                } else {
                    editTextPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    lockPassword.setImageResource(R.drawable.secret_password_eye_ico)
                }
            }
        }
    }

    private fun setupBackToSplashButton() {
        binding.backToSplash.setOnClickListener {
            (activity as SplashActivity).showSplashFragment()
        }
    }

    private fun setupForgotPasswordTextView() {
        binding.textView.setOnClickListener {
            val builder = AlertDialog.Builder(context, R.style.TransparentDialog)
            val inflater = LayoutInflater.from(context)
            val view2 = inflater.inflate(R.layout.item_forgot_password, null)
            builder.setView(view2)
            val email = view2.findViewById<EditText>(R.id.editTextMail2)
            val buttonSend = view2.findViewById<Button>(R.id.buttonSend)
            val dialog = builder.create()
            dialog.show()
            buttonSend.setOnClickListener {
                if (email.text.toString().trim().isEmpty()) {
                    showToast("Lütfen email adresinizi giriniz!")
                } else {
                    auth.sendPasswordResetEmail(email.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                showToast("Lütfen email kutunuzu kontrol ediniz!")
                                dialog.dismiss()
                            } else {
                                showToast("Geçerli bir email adresi giriniz!")
                                errorEdit(email)
                            }
                        }
                }
            }
            dialog.setOnCancelListener {
                dialog.dismiss()
            }
        }
    }

    private fun setupBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as SplashActivity).showSplashFragment()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun errorEdit(editText: EditText) {
        editText.setTextColor(Color.RED)
        val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        editText.startAnimation(shake)
        Handler(Looper.getMainLooper()).postDelayed({
            editText.text.clear()
            editText.setTextColor(Color.BLACK)
        }, 500)
    }

    private fun resetButtonVisibility() {
        with(binding) {
            loginButton.visibility = View.VISIBLE
            progressCard.visibility = View.INVISIBLE
            buttonPaw.visibility = View.VISIBLE
        }
    }
}
