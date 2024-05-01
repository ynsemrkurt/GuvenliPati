package com.example.guvenlipati.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivitySettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.confirmPasswordButton.setOnClickListener {
            changePassword()
        }

        binding.backToSplash.setOnClickListener {
            onBackPressed()
            finish()
        }
        val contactUsButton = binding.contactUsButton

        contactUsButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "yunusemre-kurt@outlook.com", null)
            )
            startActivity(Intent.createChooser(intent, "Send email..."))
        }



    }

    private fun changePassword() {
        val currentPassword = binding.editTextCurrentPassword.text.toString()
        val newPassword = binding.editTextNewPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurunuz", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Yeni şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
            return
        }

        val user = mAuth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updatePasswordTask ->
                                if (updatePasswordTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Şifre başarıyla değiştirildi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Şifre değiştirilirken bir hata oluştu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Mevcut şifre yanlış",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
