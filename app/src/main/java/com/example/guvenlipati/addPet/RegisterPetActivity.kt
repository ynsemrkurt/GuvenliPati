package com.example.guvenlipati.addPet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.ActivityRegisterPetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import java.util.UUID

class RegisterPetActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private var request: Int = 2020
    private var filePath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var strgRef: StorageReference
    private var imageUrl: String = ""
    private lateinit var binding: ActivityRegisterPetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pet)
        binding = ActivityRegisterPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!


        val petType = intent.getStringExtra("petType")
        var petGender: Boolean? = null
        var petVaccine: Boolean? = null


        when (petType) {
            "dog" -> {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.dog_types_array)
                )
                binding.typeCombo.setAdapter(adapter)
            }

            "cat" -> {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.cat_types_array)
                )
                binding.typeCombo.setAdapter(adapter)
            }

            "bird" -> {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.bird_types_array)
                )
                binding.typeCombo.setAdapter(adapter)
            }

            else -> {
                finish()
            }
        }


        binding.backToSplash.setOnClickListener {
            showMaterialDialog()
        }


        binding.buttonPetFemale.setOnClickListener {
            petGender = true
            selectMethod(binding.buttonPetFemale, binding.buttonPetMale)
        }

        binding.buttonPetMale.setOnClickListener {
            petGender = false
            selectMethod(binding.buttonPetMale, binding.buttonPetFemale)
        }

        binding.buttonPetVaccine.setOnClickListener {
            petVaccine = true
            selectMethod(binding.buttonPetVaccine, binding.buttonPetUnVaccine)
            binding.vaccine.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            binding.unVaccine.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }

        binding.buttonPetUnVaccine.setOnClickListener {
            petVaccine = false
            selectMethod(binding.buttonPetUnVaccine, binding.buttonPetVaccine)
            binding.unVaccine.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            binding.vaccine.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }

        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(request, result.resultCode, result.data)
            }

        storage = Firebase.storage
        strgRef = storage.reference

        binding.buttonAddProfileImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getContent.launch(Intent.createChooser(intent, "Select Profile Image"))
        }

        binding.petRegisterButton.setOnClickListener {
            val petId = UUID.randomUUID().toString()
            databaseReference = FirebaseDatabase.getInstance().getReference("pets").child(petId)

            if (binding.editTextPetName.text.trim().toString().isEmpty()) {
                showToast("Lütfen dostunuzun adını giriniz!")
                return@setOnClickListener
            }

            if (!isAboutValid(binding.editTextPetName.text.toString())) {
                showToast("Lütfen sadece sayılardan oluşmayan geçerli bir ad giriniz!")
                return@setOnClickListener
            }

            if (binding.editTextWeight.text.trim().toString().isEmpty()) {
                showToast("Lütfen dostunuzun ağırlığını giriniz!")
                return@setOnClickListener
            }

            if (binding.editTextWeight.text.trim().toString().toInt() > 100) {
                showToast("Ağırlık 100 kg'den fazla olamaz!")
                return@setOnClickListener
            }

            if (binding.editTextAge.text.trim().toString().isEmpty()) {
                showToast("Lütfen dostunuzun doğum yılını giriniz!")
                return@setOnClickListener
            }

            if (binding.editTextAge.text.toString()
                    .toInt() > LocalDate.now().year || binding.editTextAge.text.toString()
                    .toInt() < 2000
            ) {
                showToast("Dostunuzun doğum yılı 2000 ve ${LocalDate.now().year} arasında olmalıdır!")
                return@setOnClickListener
            }

            if (binding.typeCombo.text.toString().isEmpty()) {
                showToast("Lütfen dostunuzun türünü seçiniz!")
                return@setOnClickListener
            }

            if (petGender == null) {
                showToast("Lütfen dostunuzun cinsiyetini seçiniz!")
                return@setOnClickListener
            }

            if (petVaccine == null) {
                showToast("Lütfen dostunuzun aşı bilgisini seçiniz!")
                return@setOnClickListener
            }

            if (binding.editTextAbout.text.trim().toString().isEmpty()) {
                showToast("Lütfen bir açıklama giriniz!")
                return@setOnClickListener
            }

            if (!isAboutValid(binding.editTextAbout.text.toString())) {
                showToast("Lütfen geçerli bir açıklama giriniz!")
                return@setOnClickListener
            }

            if (imageUrl == "") {
                showToast("Lütfen bir fotoğraf seçiniz!")
                return@setOnClickListener
            }

            showProgress()

            val hashMap = mutableMapOf(
                "userId" to firebaseUser.uid,
                "petPhoto" to imageUrl,
                "petSpecies" to petType.toString(),
                "petName" to binding.editTextPetName.text.toString(),
                "petWeight" to binding.editTextWeight.text.toString(),
                "petBirthYear" to binding.editTextAge.text.toString(),
                "petBreed" to binding.typeCombo.text.toString(),
                "petGender" to (petGender ?: "Bilinmiyor"),
                "petVaccinate" to (petVaccine ?: "Bilinmiyor"),
                "petAbout" to binding.editTextAbout.text.toString(),
                "petAdoptionStatus" to false,
                "petId" to petId
            )


            databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    hideProgress()
                    showBottomSheet()
                } else {
                    showToast("Hatalı işlem!")
                    hideProgress()
                }
            }
        }
    }

    private fun showMaterialDialog() {
        MaterialAlertDialogBuilder(this).setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz kaydınız silinecektir.")
            .setBackground(ContextCompat.getDrawable(this, R.drawable.background_dialog))
            .setPositiveButton("Sil") { _, _ ->
                showToast("Kaydınız iptal edildi.")
                finish()
            }.setNegativeButton("İptal") { _, _ ->
                showToast("İptal Edildi")
            }.show()
    }


    private fun selectMethod(selected: Button, unselected: Button) {
        selected.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselected.setTextColor(Color.BLACK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                showToast("Fotoğraf yükleniyor...")

                val inputStream = contentResolver.openInputStream(filePath!!)
                val exif = ExifInterface(inputStream!!)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )

                val originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)


                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }


                val matrix = Matrix().apply { postRotate(rotationAngle.toFloat()) }
                val rotatedBitmap = Bitmap.createBitmap(
                    originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
                )

                val imageStream = ByteArrayOutputStream()

                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, imageStream)

                val imageArray = imageStream.toByteArray()
                val imageFileName = "image_${System.currentTimeMillis()}.jpg"
                val ref: StorageReference = strgRef.child("pets/${firebaseUser.uid}/$imageFileName")
                ref.putBytes(imageArray).addOnSuccessListener {
                    showToast("Fotoğraf yüklendi!")
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                    }
                }.addOnFailureListener {
                    showToast("Başarısız, lütfen yeniden deneyin!")
                }

                binding.circleImageProfilePhoto.setImageBitmap(rotatedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_pet, null)
        view.findViewById<Button>(R.id.backToMain).setOnClickListener {
            finish()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showMaterialDialog()
    }


    private fun showProgress() {
        binding.petRegisterButton.visibility = View.INVISIBLE
        binding.progressCard.visibility = View.VISIBLE
        binding.buttonPaw2.visibility = View.INVISIBLE
    }

    private fun hideProgress() {
        binding.petRegisterButton.visibility = View.VISIBLE
        binding.progressCard.visibility = View.INVISIBLE
        binding.buttonPaw2.visibility = View.VISIBLE
    }


    private fun isAboutValid(address: String): Boolean {
        return address.any { !it.isDigit() }
    }
}



