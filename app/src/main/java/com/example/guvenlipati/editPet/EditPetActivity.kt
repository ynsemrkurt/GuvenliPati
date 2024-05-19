package com.example.guvenlipati.editPet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivityEditPetBinding
import com.example.guvenlipati.models.Pet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate

class EditPetActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var petTypeCombo: AutoCompleteTextView
    private lateinit var petBirthYear: EditText
    private lateinit var vaccineImage: ImageView
    private lateinit var unVaccineImage: ImageView
    private lateinit var buttonPetVaccine: Button
    private lateinit var buttonPetUnVaccine: Button
    private var petVaccine: Boolean? = null
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private var request: Int = 2020
    private var filePath: Uri? = null
    private var imageUrl: String = ""
    private lateinit var strgRef: StorageReference
    private lateinit var storage: FirebaseStorage

    private lateinit var pet: Pet
    private lateinit var binding: ActivityEditPetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)
        binding = ActivityEditPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonPetVaccine = binding.buttonPetVaccine
        buttonPetUnVaccine = binding.buttonPetUnVaccine
        val editTextPetName = binding.editTextPetName
        val editTextPetWeight = binding.editTextWeight
        petBirthYear = binding.editTextAge
        petTypeCombo = binding.typeCombo
        val editTextAbout = binding.editTextAbout
        val editPetButton = binding.petRegisterButton
        val backButton = binding.backToSplash
        vaccineImage = binding.vaccine
        unVaccineImage = binding.unVaccine
        val profilePhoto = binding.circleImageProfilePhoto

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

        binding.buttonEditPetImage.setOnClickListener {
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

        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            try {
                pet = dataSnapshot.getValue(Pet::class.java)!!

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
                    petBirthYear.setText(pet.petBirthYear)
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
                            val adapter = ArrayAdapter.createFromResource(
                                this@EditPetActivity,
                                R.array.dog_types_array,
                                android.R.layout.simple_dropdown_item_1line
                            )
                            petTypeCombo.setAdapter(adapter)
                        }

                        "cat" -> {
                            val adapter = ArrayAdapter.createFromResource(
                                this@EditPetActivity,
                                R.array.cat_types_array,
                                android.R.layout.simple_dropdown_item_1line
                            )
                            petTypeCombo.setAdapter(adapter)
                        }

                        "bird" -> {
                            val adapter = ArrayAdapter.createFromResource(
                                this@EditPetActivity,
                                R.array.bird_types_array,
                                android.R.layout.simple_dropdown_item_1line
                            )
                            petTypeCombo.setAdapter(adapter)
                        }

                        else -> {
                            finish()
                        }
                    }
                }
            } catch (e: Exception) {
                finish()
            }
        }.addOnFailureListener {
            finish()
        }

        editPetButton.setOnClickListener {
            if (editTextPetWeight.text.trim().isEmpty() || petBirthYear.text.trim()
                    .isEmpty() || editTextPetName.text.trim().isEmpty() || editTextAbout.text.trim()
                    .isEmpty()
            ) {
                showToast("Lütfen boş alan bırakmayınız!")
                return@setOnClickListener
            }

            if (petBirthYear.text.toString()
                    .toInt() > LocalDate.now().year || petBirthYear.text.toString()
                    .toInt() < 1990
            ) {
                showToast("Dostunuzun doğum yılı 1990 ve ${LocalDate.now().year} arasında olmalıdır!")
                return@setOnClickListener
            }

            databaseReference.updateChildren(
                mapOf(
                    "petName" to editTextPetName.text.toString().trim(),
                    "petWeight" to editTextPetWeight.text.toString().trim(),
                    "petAbout" to editTextAbout.text.toString().trim(),
                    "petBirthYear" to petBirthYear.text.toString().trim(),
                    "petBreed" to petTypeCombo.text.toString().trim(),
                    "petVaccinate" to petVaccine
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Düzenleme başarılı.")
                    finish()
                } else {
                    showToast("Düzenleme hatası: ${task.exception}")
                }
            }
        }

        backButton.setOnClickListener {
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


    private fun selectMethod(selected: Button, unselected: Button) {
        selected.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselected.setTextColor(Color.BLACK)
    }

    private fun selectVaccine(
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
            binding.petRegisterButton.isEnabled = false
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
                val imageFileName = "image_${System.currentTimeMillis()}.jpg"
                val ref: StorageReference = strgRef.child("pets/${firebaseUser.uid}/$imageFileName")
                ref.putBytes(imageArray)
                    .addOnSuccessListener {
                        showToast("Fotoğraf yüklendi!")
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            databaseReference.child("petPhoto").setValue(imageUrl)
                            binding.petRegisterButton.isEnabled = true
                        }
                    }
                    .addOnFailureListener {
                        showToast("Başarısız, lütfen yeniden deneyin!")
                    }

                binding.circleImageProfilePhoto.setImageBitmap(rotatedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}