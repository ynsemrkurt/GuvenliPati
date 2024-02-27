package com.example.guvenlipati

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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
            buttonPetUnVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonPetUnVaccine.setTextColor(Color.WHITE)
            buttonPetVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonPetVaccine.setTextColor(Color.BLACK)
        }


    }

}