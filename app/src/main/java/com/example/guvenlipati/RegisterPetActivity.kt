package com.example.guvenlipati

import android.content.Intent
import android.graphics.Bitmap
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

private lateinit var firebaseUser: FirebaseUser
private lateinit var databaseReference: DatabaseReference
private lateinit var auth: FirebaseAuth
private lateinit var getContent: ActivityResultLauncher<Intent>
private var request: Int = 2020
private var filePath: Uri? = null
private lateinit var storage: FirebaseStorage
private lateinit var strgRef: StorageReference
private var imageUrl: String = ""


class RegisterPetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pet)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!


        val petType = intent.getStringExtra("petType")

        val petTypeCombo = findViewById<AutoCompleteTextView>(R.id.typeCombo)

        if (petType == "dog") {
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.dog_types_array)
            )
            petTypeCombo.setAdapter(adapter)
        } else if (petType == "cat") {
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.cat_types_array)
            )
            petTypeCombo.setAdapter(adapter)
        } else if (petType == "bird") {
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.bird_types_array)
            )
            petTypeCombo.setAdapter(adapter)
        } else {
            finish()
        }


        val buttonPetFemale = findViewById<Button>(R.id.buttonPetFemale)
        val buttonPetMale = findViewById<Button>(R.id.buttonPetMale)
        val buttonPetVaccine = findViewById<Button>(R.id.buttonPetVaccine)
        val buttonPetUnVaccine = findViewById<Button>(R.id.buttonPetUnVaccine)
        val editTextPetName = findViewById<EditText>(R.id.editTextPetName)
        val editTextPetWeight = findViewById<EditText>(R.id.editTextWeight)
        val petAgeCombo = findViewById<AutoCompleteTextView>(R.id.ageCombo)
        val petTypeCombo = findViewById<AutoCompleteTextView>(R.id.typeCombo)
        var petGender: Boolean? = null
        var petVaccine: Boolean? = null
        val editTextAbout = findViewById<EditText>(R.id.editTextAbout)
        val addPetButton = findViewById<Button>(R.id.petRegisterButton)
        val buttonPaw=findViewById<ImageView>(R.id.buttonPaw2)
        val progressCard = findViewById<CardView>(R.id.progressCard)

        when (petType) {
            "dog" -> {
                val adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.dog_types_array)
                )
                petTypeCombo.setAdapter(adapter)
            }

            "cat" -> {
                val adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.cat_types_array)
                )
                petTypeCombo.setAdapter(adapter)
            }

            "bird" -> {
                val adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.bird_types_array)
                )
                petTypeCombo.setAdapter(adapter)
            }

            else -> {
                finish()
            }
        }





        buttonPetFemale.setOnClickListener {
            petGender = true
            selectMethod(buttonPetFemale, buttonPetMale)
        }

        buttonPetMale.setOnClickListener {
            petGender = false
            selectMethod(buttonPetMale, buttonPetFemale)
        }

        buttonPetVaccine.setOnClickListener {
            petVaccine = true
            selectMethod(buttonPetVaccine, buttonPetUnVaccine)
        }

        buttonPetUnVaccine.setOnClickListener {
            petVaccine = false
            selectMethod(buttonPetUnVaccine, buttonPetVaccine)
            buttonPetVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonPetVaccine.setTextColor(Color.BLACK)
            buttonPetUnVaccine.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonPetUnVaccine.setTextColor(Color.WHITE)
            selectMethod(buttonPetUnVaccine, buttonPetVaccine)
        }

        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(request, result.resultCode, result.data)
            }

        storage = Firebase.storage
        strgRef = storage.reference

        findViewById<ImageButton>(R.id.buttonAddProfileImage).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getContent.launch(Intent.createChooser(intent, "Select Profile Image"))
        }

        addPetButton.setOnClickListener {
        findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Emin Misiniz?")
        alertDialogBuilder.setMessage("Eğer geri dönerseniz kaydınız silinecektir.")

        alertDialogBuilder.setPositiveButton("Sil") { _, _ ->
            showToast("Kaydınız iptal edildi.")

            databaseReference =
                FirebaseDatabase.getInstance().getReference("pets").child(firebaseUser.uid+editTextPetName.text.toString())

            if (editTextPetName.text.toString().isEmpty()){
                showToast("Lütfen ad giriniz!")
                return@setOnClickListener
            }

            if (editTextPetWeight.text.toString().isEmpty()){
                showToast("Lütfen ağırlık giriniz!")
                return@setOnClickListener
            }

            if (petAgeCombo.text.toString().isEmpty()){
                showToast("Lütfen yaş giriniz!")
                return@setOnClickListener
            }

            if (petGender==null){
                showToast("Lütfen cinsiyet seçiniz!")
                return@setOnClickListener
            }

            if (petVaccine==null){
                showToast("Lütfen aşı bilgisi seçiniz!")
                return@setOnClickListener
            }

            if (editTextAbout.text.toString().isEmpty()){
                showToast("Lütfen tüm alanları doldurunuz!")
                return@setOnClickListener
            }

            addPetButton.visibility = View.INVISIBLE
            progressCard.visibility = View.VISIBLE
            buttonPaw.visibility = View.INVISIBLE

            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["userId"] = firebaseUser.uid
            hashMap["petPhoto"] = imageUrl
            hashMap["petSpecies"]=petType.toString()
            hashMap["petName"] = editTextPetName.text.toString()
            hashMap["petWeight"] = editTextPetWeight.text.toString()
            hashMap["petAge"] = petAgeCombo.text.toString()
            hashMap["petBreed"] = petTypeCombo.text.toString()
            hashMap["petGender"] = petGender.toString()
            hashMap["petVaccinate"] = petVaccine.toString()
            hashMap["petAbout"]=editTextAbout.text.toString()
            hashMap["petAdoptionStatus"]=false

            databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Dost Eklendi!")
                    addPetButton.visibility = View.VISIBLE
                    progressCard.visibility = View.INVISIBLE
                    buttonPaw.visibility = View.VISIBLE
                    val intent=Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    showToast("Hatalı işlem!")
                    addPetButton.visibility = View.VISIBLE
                    progressCard.visibility = View.INVISIBLE
                    buttonPaw.visibility = View.VISIBLE
                }
            }

        }
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

    fun selectMethod(selected: Button, unselected: Button) {
        selected.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselected.setTextColor(Color.BLACK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                showToast("Fotoğraf yükleniyor...")

                val inputStream = contentResolver.openInputStream(filePath!!)
                val exif = ExifInterface(inputStream!!)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val originalBitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, filePath)


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

                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, imageStream)

                val imageArray = imageStream.toByteArray()

                val ref: StorageReference = strgRef.child("pets/" + firebaseUser.uid)
                ref.putBytes(imageArray)
                    .addOnSuccessListener {
                        showToast("Fotoğraf yüklendi!")
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}