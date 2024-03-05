package com.example.guvenlipati

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.guvenlipati.models.Pet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditPetActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var petTypeCombo: AutoCompleteTextView
    private lateinit var vaccineImage: ImageView
    private lateinit var unVaccineImage: ImageView
    private lateinit var buttonPetVaccine: Button
    private lateinit var buttonPetUnVaccine: Button
    var petVaccine: Boolean? = null
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private var request: Int = 2020
    private var filePath: Uri? = null
    private var imageUrl: String = ""
    private lateinit var strgRef: StorageReference
    private lateinit var storage: FirebaseStorage

    private lateinit var pet: Pet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)


        buttonPetVaccine = findViewById(R.id.buttonPetVaccine)
        buttonPetUnVaccine = findViewById(R.id.buttonPetUnVaccine)
        val editTextPetName = findViewById<EditText>(R.id.editTextPetName)
        val editTextPetWeight = findViewById<EditText>(R.id.editTextWeight)
        val petAgeCombo = findViewById<AutoCompleteTextView>(R.id.ageCombo)
        petTypeCombo = findViewById(R.id.typeCombo)
        val editTextAbout = findViewById<EditText>(R.id.editTextAbout)
        val editPetButton = findViewById<Button>(R.id.petRegisterButton)
        val backButton = findViewById<ImageButton>(R.id.backToSplash)
        vaccineImage = findViewById(R.id.vaccine)
        unVaccineImage = findViewById(R.id.unVaccine)
        val profilePhoto = findViewById<CircleImageView>(R.id.circleImageProfilePhoto)



        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        val petId = intent.getStringExtra("petId")
        databaseReference =
            FirebaseDatabase.getInstance().getReference("pets").child(petId.toString())

        storage = Firebase.storage
        strgRef = storage.reference

        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(request, result.resultCode, result.data)
            }

        findViewById<ImageButton>(R.id.buttonEditPetImage).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getContent.launch(Intent.createChooser(intent, "Select Profile Image"))
        }

        buttonPetVaccine.setOnClickListener {
            petVaccine = true
            selectMethod(buttonPetVaccine, buttonPetUnVaccine)
            vaccineImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            unVaccineImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }
        buttonPetUnVaccine.setOnClickListener {
            petVaccine = false
            selectMethod(buttonPetUnVaccine, buttonPetVaccine)
            unVaccineImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            vaccineImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                try {
                    pet = snapshot.getValue(Pet::class.java)!!
                    if (pet.petId == petId) {
                        if (pet.petPhoto.isEmpty()) {
                            profilePhoto.setImageResource(R.drawable.pet_default_image)
                        } else {
                            val imageUri = Uri.parse(pet.petPhoto)
                            if (!isDestroyed) {
                                Glide.with(this@EditPetActivity).load(imageUri)
                                    .placeholder(R.drawable.pet_default_image)
                                    .into(profilePhoto)
                            }
                        }
                        editTextPetName.setText(pet.petName)
                        editTextPetWeight.setText(pet.petWeight)
                        petAgeCombo.setText(pet.petAge)
                        selectTypeArray(pet.petSpecies)
                        petTypeCombo.setText(pet.petBreed)
                        if (pet.petVaccinate) {
                            selectVaccine(
                                buttonPetVaccine,
                                buttonPetUnVaccine,
                                vaccineImage,
                                unVaccineImage
                            )
                        } else {
                            selectVaccine(
                                buttonPetUnVaccine,
                                buttonPetVaccine,
                                unVaccineImage,
                                vaccineImage
                            )
                        }
                        editTextAbout.setText(pet.petAbout)
                        petVaccine = pet.petVaccinate

                        when (pet.petSpecies) {
                            "dog" -> {
                                val adapter = ArrayAdapter.createFromResource(this@EditPetActivity, R.array.dog_types_array, android.R.layout.simple_dropdown_item_1line)
                                petTypeCombo.setAdapter(adapter)
                            }

                            "cat" -> {
                                val adapter = ArrayAdapter.createFromResource(this@EditPetActivity, R.array.cat_types_array, android.R.layout.simple_dropdown_item_1line)
                                petTypeCombo.setAdapter(adapter)
                            }

                            "bird" -> {
                                val adapter = ArrayAdapter.createFromResource(this@EditPetActivity, R.array.bird_types_array, android.R.layout.simple_dropdown_item_1line)
                                petTypeCombo.setAdapter(adapter)
                            }

                            else -> {
                                finish()
                            }
                        }
                    }
                }catch (e: Exception){
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        editPetButton.setOnClickListener {

            if (editTextPetWeight.text.isEmpty() || petAgeCombo.text.isEmpty() || editTextPetName.text.isEmpty() || editTextAbout.text.isEmpty()) {
                showToast("Lütfen boş alan bırakmayınız!")
                return@setOnClickListener
            }

            databaseReference.child("petPhoto").setValue(pet.petPhoto)
            databaseReference.child("petName").setValue(editTextPetName.text.toString())
            databaseReference.child("petWeight").setValue(editTextPetWeight.text.toString())
            databaseReference.child("petAbout").setValue(editTextAbout.text.toString())
            databaseReference.child("petAge").setValue(petAgeCombo.text.toString())
            databaseReference.child("petBreed").setValue(petTypeCombo.text.toString())
            databaseReference.child("petVaccinate").setValue(petVaccine).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Değişiklikler kaydedildi...")
                    finish()
                } else {
                    showToast("Hatalı işlem!")
                }
            }
        }

        backButton.setOnClickListener{
            showMaterialDialog()
        }

    }

    private fun showMaterialDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz değişiklikler silinecektir.")
            .setBackground(ContextCompat.getDrawable(this, R.drawable.background_dialog))
            .setPositiveButton("Sil") { _, _ ->
                showToast("Değişiklikler silindi.")
                finish()
            }
            .setNegativeButton("İptal") { _, _ ->
            }
            .setOnCancelListener {
            }
            .show()
    }


    fun selectTypeArray(petType: String) {
        when (petType) {
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

    private fun selectMethod(selected: Button, unselected: Button) {
        selected.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselected.setTextColor(Color.BLACK)
    }

    fun selectVaccine(
        selected: Button,
        unselected: Button,
        vaccineImage: ImageView,
        unVaccineImage: ImageView
    ) {
        selected.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselected.setTextColor(Color.BLACK)
        vaccineImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        unVaccineImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                showToast("Fotoğraf yükleniyor...")

                val inputStream = this.contentResolver.openInputStream(filePath!!)
                val exif = ExifInterface(inputStream!!)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val originalBitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)

                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }


                val matrix = Matrix().apply { postRotate(rotationAngle.toFloat()) }
                val rotatedBitmap = Bitmap.createBitmap(
                    originalBitmap,
                    0,
                    0,
                    originalBitmap.width,
                    originalBitmap.height,
                    matrix,
                    true
                )

                val imageStream = ByteArrayOutputStream()

                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, imageStream)

                val imageArray = imageStream.toByteArray()

                val ref: StorageReference = strgRef.child("image/" + firebaseUser.uid)
                ref.putBytes(imageArray)
                    .addOnSuccessListener {
                        showToast("Fotoğraf yüklendi!")
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            databaseReference.child("petPhoto").setValue(imageUrl)
                        }
                    }
                    .addOnFailureListener {
                        showToast("Başarısız, lütfen yeniden deneyin!")
                    }

                findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
                    ?.setImageBitmap(rotatedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}