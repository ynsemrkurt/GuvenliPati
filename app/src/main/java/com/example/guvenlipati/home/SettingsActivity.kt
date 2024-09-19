package com.example.guvenlipati.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guvenlipati.databinding.ActivitySettingsBinding
import com.example.guvenlipati.extensions.createEmailIntent
import com.example.guvenlipati.extensions.isMatching
import com.example.guvenlipati.extensions.isValidPassword
import com.example.guvenlipati.extensions.showToast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            confirmPasswordButton.setOnClickListener { handleChangePasswordClick() }
            backToSplash.setOnClickListener { finish() }
            notificationChange.setOnClickListener { requestNotificationPermission() }
            contactUsButton.setOnClickListener { sendEmail() }
            whoIsBEGTECH.setOnClickListener { openAboutUsActivity() }
        }
    }

    private fun handleChangePasswordClick() {
        with(binding) {
            val currentPassword = editTextCurrentPassword.text.toString()
            val newPassword = editTextNewPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            when {
                areFieldsEmpty(currentPassword, newPassword, confirmPassword) -> showToast("Lütfen tüm alanları doldurunuz!")
                !newPassword.isValidPassword() -> showToast("Şifre en az 8 karakter olmalıdır ve boşluk içermemelidir!")
                !newPassword.isMatching(confirmPassword) -> showToast("Şifreler uyuşmuyor!")
                currentPassword == newPassword -> showToast("Mevcut şifre ile yeni şifre aynı olamaz!")
                else -> changePassword(currentPassword, newPassword)
            }
        }
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val currentUser = auth.currentUser ?: return
        val email = currentUser.email ?: return
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        currentUser.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    updatePassword(currentUser, newPassword)
                } else {
                    showToast("Mevcut şifre yanlış")
                }
            }
    }

    private fun updatePassword(user: FirebaseUser, newPassword: String) {
        user.updatePassword(newPassword)
            .addOnCompleteListener { updatePasswordTask ->
                if (updatePasswordTask.isSuccessful) {
                    showToast("Şifre başarıyla değiştirildi")
                    finish()
                } else {
                    showToast("Şifre değiştirilirken bir hata oluştu")
                }
            }
    }

    private fun areFieldsEmpty(vararg fields: String) = fields.any { it.isEmpty() }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        } else {
            showToast("Bildirim izni gerekli değil!")
        }
    }


    private fun sendEmail() {
        val intent = createEmailIntent("yunusemre-kurt@outlook.com")
        startActivity(Intent.createChooser(intent, "E-posta gönder..."))
    }

    private fun openAboutUsActivity() {
        startActivity(Intent(this, AboutUsActivity::class.java))
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION_CODE = 1001
    }
}
