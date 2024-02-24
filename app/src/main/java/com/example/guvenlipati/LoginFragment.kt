package com.example.guvenlipati

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.loginButton).setOnClickListener{

            auth = FirebaseAuth.getInstance()

            val userEmail= view.findViewById<EditText>(R.id.editTextEmail)
            val userPassword = view.findViewById<EditText>(R.id.editTextPassword)

            if (userEmail.text.toString().isEmpty() || userPassword.text.toString().isEmpty())
            {
                showToast("Hiçbir alan boş bırakılamaz!")
            }
            else
            {
               auth.signInWithEmailAndPassword(
                   userEmail.text.toString(),
                   userPassword.text.toString()
               )
                   .addOnCompleteListener()
                   {
                       if (it.isSuccessful)
                       {
                            showToast("Giriş başarılı!")
                            userEmail.setText("")
                            userPassword.setText("")
                       }
                       else
                       {
                            showToast("Hatalı Giriş!")
                            view.findViewById<EditText>(R.id.editTextPassword).setText("")
                       }
                   }
            }
        }
    }

    private fun showToast(message:String){
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
    }
}