package com.example.guvenlipati.backer

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.example.guvenlipati.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class PetBackerActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("identifies").child(auth.currentUser!!.uid)
        databaseReference2 = FirebaseDatabase.getInstance().getReference("users").child(auth.currentUser!!.uid)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_backer)

        val backButton = findViewById<ImageButton>(R.id.backToHome)

        backButton.setOnClickListener {
            backProcess()
        }

        this.onBackPressedDispatcher.addCallback {
            backProcess()
        }
    }

    fun goRegisterBackerFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, RegisterBackerFragment())
            .commit()
        animation()
    }
    fun goBackerPreferenceFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, BackerPreferenceFragment())
            .commit()
        animation()
    }

    private fun animation() {
        val fragmentContainerView: FragmentContainerView = findViewById(R.id.fragmentContainerView2)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fragmentContainerView.startAnimation(fadeIn)
    }

    private fun backProcess() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz kaydınız silinecektir.")
            .setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.background_dialog
                )
            )
            .setPositiveButton("Sil") { _, _ ->
                databaseReference.removeValue()
                databaseReference2.child("userBacker").setValue(false)
                showToast("Kaydınız iptal edildi.")
                finish()
            }
            .setNegativeButton("İptal") { _, _ ->
                showToast("İptal Edildi")
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}