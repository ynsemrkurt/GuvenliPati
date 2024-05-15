package com.example.guvenlipati.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
            handleChangePasswordClick()
        }

        binding.backToSplash.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.notificationChange.setOnClickListener {
            requestNotificationPermission()
        }

        binding.contactUsButton.setOnClickListener {
            sendEmail()
        }

        binding.whoIsBEGTECH.setOnClickListener {
            openAboutUsActivity()
        }
    }

    private fun handleChangePasswordClick() {
        val currentPassword = binding.editTextCurrentPassword.text.toString()
        val newPassword = binding.editTextNewPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()

        if (areFieldsEmpty(currentPassword, newPassword, confirmPassword)) {
            showToast("Lütfen tüm alanları doldurunuz")
            return
        }

        if (!isPasswordValid(newPassword, confirmPassword)) {
            showToast("Şifre en az 8 karakter olmalıdır ve boşluk içermemelidir")
            return
        }

        if (!arePasswordsMatching(newPassword, confirmPassword)) {
            showToast("Şifreler uyuşmuyor")
            return
        }

        mAuth.currentUser?.let { currentUser ->
            currentUser.email?.let { email ->
                val credential = EmailAuthProvider.getCredential(email, currentPassword)

                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            currentUser.updatePassword(newPassword)
                                .addOnCompleteListener { updatePasswordTask ->
                                    if (updatePasswordTask.isSuccessful) {
                                        showToast("Şifre başarıyla değiştirildi")
                                        finish()
                                    } else {
                                        showToast("Şifre değiştirilirken bir hata oluştu")
                                    }
                                }
                        } else {
                            showToast("Mevcut şifre yanlış")
                        }
                    }
            }
        }
    }

    private fun arePasswordsMatching(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    private fun areFieldsEmpty(vararg fields: String): Boolean {
        return fields.any { it.isEmpty() }
    }

    private fun isPasswordValid(vararg passwords: String): Boolean {
        return passwords.all { it.length >= 8 && !it.contains(" ") }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION_CODE
            )
        } else {
            showToast("Bildirim izni zaten verilmiş!")
        }
    }

    private fun sendEmail() {
        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts("mailto", "yunusemre-kurt@outlook.com", null)
        )
        startActivity(Intent.createChooser(intent, "Send email..."))
    }

    private fun openAboutUsActivity() {
        val intent = Intent(this, AboutUsActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION_CODE = 1001
    }
}
