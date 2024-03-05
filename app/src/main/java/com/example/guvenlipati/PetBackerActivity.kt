package com.example.guvenlipati

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PetBackerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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