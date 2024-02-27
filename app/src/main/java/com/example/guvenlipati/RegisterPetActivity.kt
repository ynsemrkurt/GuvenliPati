package com.example.guvenlipati

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterPetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pet)

        val petType = intent.getStringExtra("petType")

        val petTypeCombo=findViewById<AutoCompleteTextView>(R.id.typeCombo)

        if (petType=="dog"){
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.dog_types_array))
            petTypeCombo.setAdapter(adapter)
        }
        else if (petType=="cat"){
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.cat_types_array))
            petTypeCombo.setAdapter(adapter)
        }
        else if (petType=="bird"){
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.bird_types_array))
            petTypeCombo.setAdapter(adapter)
        }
        else{
            finish()
        }


        val buttonPetFemale = findViewById<Button>(R.id.buttonPetFemale)
        val buttonPetMale = findViewById<Button>(R.id.buttonPetMale)
        val buttonPetVaccine = findViewById<Button>(R.id.buttonPetVaccine)
        val buttonPetUnVaccine = findViewById<Button>(R.id.buttonPetUnVaccine)

        var petGender: Boolean? = null
        var petVaccine: Boolean? = null



        buttonPetFemale.setOnClickListener {
            petGender = true
            selectMethod(buttonPetFemale,buttonPetMale)
        }

        buttonPetMale.setOnClickListener {
            petGender = false
            selectMethod(buttonPetMale,buttonPetFemale)
        }

        buttonPetVaccine.setOnClickListener {
            petVaccine = true
            selectMethod(buttonPetVaccine,buttonPetUnVaccine)
        }

        buttonPetUnVaccine.setOnClickListener {
            petVaccine = false
            buttonPetVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonPetVaccine.setTextColor(Color.BLACK)
            buttonPetUnVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonPetUnVaccine.setTextColor(Color.WHITE)
            selectMethod(buttonPetUnVaccine,buttonPetVaccine)
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

    fun selectMethod(selected:Button,unselected:Button) {
        selected.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselected.setTextColor(Color.BLACK)
    }

}