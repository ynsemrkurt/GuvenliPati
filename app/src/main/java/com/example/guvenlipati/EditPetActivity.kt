package com.example.guvenlipati

import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.guvenlipati.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class EditPetActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var petTypeCombo: AutoCompleteTextView
    private lateinit var vaccineImage: ImageView
    private lateinit var unVaccineImage: ImageView
    private lateinit var buttonPetVaccine: Button
    private lateinit var buttonPetUnVaccine: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)


        buttonPetVaccine = findViewById<Button>(R.id.buttonPetVaccine)
        buttonPetUnVaccine = findViewById<Button>(R.id.buttonPetUnVaccine)
        val editTextPetName = findViewById<EditText>(R.id.editTextPetName)
        val editTextPetWeight = findViewById<EditText>(R.id.editTextWeight)
        val petAgeCombo = findViewById<AutoCompleteTextView>(R.id.ageCombo)
        petTypeCombo = findViewById(R.id.typeCombo)
        val editTextAbout = findViewById<EditText>(R.id.editTextAbout)
        val addPetButton = findViewById<Button>(R.id.petRegisterButton)
        val buttonPaw = findViewById<ImageView>(R.id.buttonPaw2)
        val progressCard = findViewById<CardView>(R.id.progressCard)
        val backButton = findViewById<ImageButton>(R.id.backToSplash)
        vaccineImage = findViewById<ImageView>(R.id.vaccine)
        unVaccineImage = findViewById<ImageView>(R.id.unVaccine)
        val profilePhoto = findViewById<CircleImageView>(R.id.circleImageProfilePhoto)



        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        val petId = intent.getStringExtra("petId")
        databaseReference =
            FirebaseDatabase.getInstance().getReference("pets").child(petId.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val pet = snapshot.getValue(Pet::class.java)
                if (pet?.petId == petId) {
                    if (pet?.petPhoto!!.isEmpty()) {
                        profilePhoto.setImageResource(R.drawable.pet_default_image)
                    } else {
                        val imageUri = Uri.parse(pet.petPhoto)
                        Glide.with(this@EditPetActivity).load(imageUri)
                            .placeholder(R.drawable.pet_default_image)
                            .into(profilePhoto)
                    }
                    editTextPetName.setText(pet.petName)
                    editTextPetWeight.setText(pet.petWeight)
                    petAgeCombo.setText(pet.petAge)
                    selectTypeArray(pet.petSpecies)
                    petTypeCombo.setText(pet.petBreed)
                    if (pet.petVaccinate==true){
                        selectVaccine(buttonPetVaccine,buttonPetUnVaccine,vaccineImage,unVaccineImage)
                    }else{
                        selectVaccine(buttonPetUnVaccine,buttonPetVaccine,unVaccineImage,vaccineImage)
                    }
                    editTextAbout.setText(pet.petAbout)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun selectTypeArray(petType: String) {
        val petTyp=petType
        when (petTyp) {
            "dog" -> {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.dog_types_array)
                )
                petTypeCombo.setAdapter(adapter)
            }

            "cat" -> {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.cat_types_array)
                )
                petTypeCombo.setAdapter(adapter)
            }

            "bird" -> {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.bird_types_array)
                )
                petTypeCombo.setAdapter(adapter)
            }
        }
    }

    fun selectVaccine(selected: Button, unselected: Button, vaccineImage: ImageView, unVaccineImage: ImageView){
        selected.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselected.setTextColor(Color.BLACK)
        vaccineImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        unVaccineImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
    }
}