package com.example.guvenlipati.backer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivityPetBackerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PetBackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPetBackerBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("identifies").child(auth.currentUser!!.uid)
    }
    private val databaseReference2: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("users").child(auth.currentUser!!.uid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupBackNavigation()
        goSplash()
    }

    private fun setupBinding() {
        binding = ActivityPetBackerBinding.inflate(layoutInflater).apply { setContentView(root) }
        binding.backToHome.setOnClickListener { showExitConfirmationDialog() }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback { showExitConfirmationDialog() }
    }

    private fun goSplash() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, BackerSplashFragment())
            .commit()
    }

    fun goRegisterBackerFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, RegisterBackerFragment())
            .commit()
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz kaydınız silinecektir.")
            .setBackground(ContextCompat.getDrawable(this, R.drawable.background_dialog))
            .setPositiveButton("Sil") { _, _ -> deleteUserRegistration() }
            .setNegativeButton("İptal") { _, _ -> showToast("İptal Edildi") }
            .show()
    }

    private fun deleteUserRegistration() {
        databaseReference.removeValue()
        databaseReference2.child("userBacker").setValue(false)
        showToast("Kaydınız iptal edildi.")
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
