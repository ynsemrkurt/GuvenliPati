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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging


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


        binding.loginButton.setOnClickListener {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")

            if (binding.editTextEmail.text.toString()
                    .isEmpty() || binding.editTextPassword.text.toString().isEmpty()
            ) {
                showToast("Hiçbir alan boş bırakılamaz!")
            } else {

                binding.loginButton.visibility = View.INVISIBLE
                binding.progressCard.visibility = View.VISIBLE
                binding.buttonPaw.visibility = View.INVISIBLE
                auth.signInWithEmailAndPassword(
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
                    .addOnCompleteListener()
                    {
                        if (it.isSuccessful) {
                            FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                                val token = result
                                databaseReference.child(auth.currentUser?.uid.toString())
                                    .child("userToken").setValue(token).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            (activity as SplashActivity).goHomeActivity()
                                            binding.editTextEmail.setText("")
                                            binding.editTextPassword.setText("")
                                        } else {
                                            showToast("Başarısız Giriş!")
                                        }
                                    }
                            }
                                .addOnFailureListener { exception ->
                                    showToast("Başarısız Giriş!")
                                }
                        } else {
                            binding.editTextPassword.setTextColor(Color.RED)
                            val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
                            binding.editTextPassword.startAnimation(shake)
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.editTextPassword.text.clear()
                                binding.editTextPassword.setTextColor(Color.BLACK)
                            }, 500)
                        }
                        binding.loginButton.visibility = View.VISIBLE
                        binding.progressCard.visibility = View.INVISIBLE
                        binding.buttonPaw.visibility = View.VISIBLE
                    }
            }

        }

        binding.lockPassword.setOnClickListener {

            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.editTextPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.lockPassword.setImageResource(R.drawable.password_eye_ico)
            } else {
                binding.editTextPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.lockPassword.setImageResource(R.drawable.secret_password_eye_ico)
            }

        }

        binding.backToSplash.setOnClickListener {
            (activity as SplashActivity).goSplashFragment()
        }

        binding.textView.setOnClickListener {

            val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

            val inflater = LayoutInflater.from(context)
            val view2 = inflater.inflate(R.layout.item_forgot_password, null)
            builder.setView(view2)

            val dialog = builder.create()
            dialog.show()
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as SplashActivity).goSplashFragment()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}