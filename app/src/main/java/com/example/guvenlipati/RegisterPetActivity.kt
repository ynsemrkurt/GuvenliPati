package com.example.guvenlipati

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterPetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pet)


        val buttonPetFemale = findViewById<Button>(R.id.buttonPetFemale)
        val buttonPetMale = findViewById<Button>(R.id.buttonPetMale)
        val buttonPetVaccine = findViewById<Button>(R.id.buttonPetVaccine)
        val buttonPetUnVaccine = findViewById<Button>(R.id.buttonPetUnVaccine)
        var petGender: Boolean? = null
        var petVaccine: Boolean? = null



        buttonPetFemale.setOnClickListener {
            petGender = true
            buttonPetFemale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonPetFemale.setTextColor(Color.WHITE)
            buttonPetMale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonPetMale.setTextColor(Color.BLACK)
        }

        buttonPetMale.setOnClickListener {
            petGender = false
            buttonPetMale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonPetMale.setTextColor(Color.WHITE)
            buttonPetFemale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonPetFemale.setTextColor(Color.BLACK)
        }

        buttonPetVaccine.setOnClickListener {
            petVaccine = true
            buttonPetVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonPetVaccine.setTextColor(Color.WHITE)
            buttonPetUnVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonPetUnVaccine.setTextColor(Color.BLACK)
        }

        buttonPetUnVaccine.setOnClickListener {
            petVaccine = false
            buttonPetVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonPetVaccine.setTextColor(Color.BLACK)
            buttonPetUnVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonPetUnVaccine.setTextColor(Color.WHITE)
        }

        findViewById<ImageButton>(R.id.backToSplash).setOnClickListener{
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Emin Misiniz?")
        alertDialogBuilder.setMessage("Eğer geri dönerseniz kaydınız silinecektir.")

        alertDialogBuilder.setPositiveButton("Sil") { _, _ ->
            showToast("Kaydınız iptal edildi.")

            val containerId = R.id.fragmentContainerView2
            val fragmentManager = supportFragmentManager
            val fragment = AddPetFragment()
            fragmentManager.beginTransaction().replace(containerId, fragment).commit()

        }

        alertDialogBuilder.setNegativeButton("İptal") { _, _ ->
            showToast("İptal Edildi")
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}