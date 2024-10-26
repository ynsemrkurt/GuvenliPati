package com.example.guvenlipati.splash

import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentSecondSignUpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate
import android.graphics.Bitmap as UploadBitmap

class SecondSignUpFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private var filePath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUrl: String = ""
    private lateinit var binding: FragmentSecondSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeFirebase()
        setupGenderSelection()
        setupImagePicker()
        setupSaveProfileButton()
        setupBackNavigation()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showMaterialDialog()
        }
    }

    private fun initializeFirebase() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storage = Firebase.storage
        storageReference = storage.reference
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
    }

    private fun setupGenderSelection() {
        var userGender: Boolean? = null

        binding.apply {
            buttonFemale.setOnClickListener {
                userGender = true
                updateGenderSelection(buttonFemale, buttonMale)
            }
            buttonMale.setOnClickListener {
                userGender = false
                updateGenderSelection(buttonMale, buttonFemale)
            }
        }
    }

    private fun updateGenderSelection(selectedButton: Button, unselectedButton: Button) {
        selectedButton.setBackgroundResource(R.drawable.sign2_edittext_bg2)
        selectedButton.setTextColor(Color.WHITE)
        unselectedButton.setBackgroundResource(R.drawable.sign2_edittext_bg)
        unselectedButton.setTextColor(Color.BLACK)
    }

    private fun setupImagePicker() {
        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(2020, result.resultCode, result.data)
            }

        binding.buttonAddProfileImage.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            getContent.launch(Intent.createChooser(intent, "Select Profile Image"))
        }
    }

    private fun setupSaveProfileButton() {
        binding.saveProfileButton.setOnClickListener {
            if (!isInputValid()) return@setOnClickListener

            binding.apply {
                saveProfileButton.visibility = View.INVISIBLE
                progressCard.visibility = View.VISIBLE
                buttonPaw.visibility = View.INVISIBLE
            }

            val userData = collectUserData()
            saveUserData(userData)
        }
    }

    private fun isInputValid(): Boolean {
        val userNameRegex = Regex("^[a-zA-ZğüşöçĞÜŞıİÖÇ ]+$")
        val surnameRegex = Regex("^[a-zA-ZğüşöçĞÜŞıİÖÇ ]+$")

        return when {
            !userNameRegex.matches(binding.editTextUserName.text.toString()) -> {
                showToast("İsminiz sadece harf içermelidir!")
                false
            }

            !surnameRegex.matches(binding.editTextUserSurname.text.toString()) -> {
                showToast("Soyadınız sadece harf içermelidir!")
                false
            }

            binding.editTextUserName.text.isBlank() || binding.editTextUserSurname.text.isBlank() -> {
                showToast("Lütfen Ad Soyad alanları doldurunuz!")
                false
            }

            binding.provinceCombo.text.isBlank() || binding.townCombo.text.isBlank() -> {
                showToast("Lütfen il bilgisi seçiniz!")
                false
            }

            imageUrl.isEmpty() -> {
                showToast("Lütfen profil fotoğrafınızı seçiniz!")
                false
            }

            else -> true
        }
    }

    private fun collectUserData(): HashMap<String, Any> {
        val currentDate = LocalDate.now()
        return hashMapOf(
            "userId" to firebaseUser.uid,
            "userPhoto" to imageUrl,
            "userName" to binding.editTextUserName.text.toString(),
            "userSurname" to binding.editTextUserSurname.text.toString(),
            "userGender" to (binding.buttonFemale.isSelected),
            "userProvince" to binding.provinceCombo.text.toString(),
            "userTown" to binding.townCombo.text.toString(),
            "userBacker" to false,
            "userRegisterDate" to "${currentDate.dayOfMonth}/${currentDate.monthValue}/${currentDate.year}"
        )
    }

    private fun saveUserData(userData: HashMap<String, Any>) {
        databaseReference.setValue(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    databaseReference.child("userToken").setValue(token)
                }
                (activity as SplashActivity).goHomeActivity()
            } else {
                showToast("Hatalı işlem!")
            }
            binding.apply {
                saveProfileButton.visibility = View.VISIBLE
                progressCard.visibility = View.INVISIBLE
                buttonPaw.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBackNavigation() {
        binding.backToSplash.setOnClickListener {
            showMaterialDialog()
        }
    }

    private fun showMaterialDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz kaydınız silinecektir.")
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_dialog
                )
            )
            .setPositiveButton("Sil") { _, _ ->
                showToast("Kaydınız iptal edildi.")
                deleteUserData()
            }
            .setNegativeButton("İptal") { _, _ -> showToast("İptal Edildi") }
            .show()
    }

    private fun deleteUserData() {
        firebaseUser.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) (activity as SplashActivity).showSplashFragment()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2020 && resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            filePath = data.data
            uploadImage()
        }
    }

    private fun uploadImage() {
        try {
            val originalBitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
            val rotatedBitmap = rotateImageIfNeeded(originalBitmap)
            val imageArray = compressImage(rotatedBitmap)

            val ref = storageReference.child("image/${firebaseUser.uid}")
            ref.putBytes(imageArray).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    binding.circleImageProfilePhoto.setImageBitmap(rotatedBitmap)
                    showToast("Fotoğraf yüklendi!")
                }
            }.addOnFailureListener {
                showToast("Başarısız, lütfen yeniden deneyin!")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun rotateImageIfNeeded(bitmap: UploadBitmap): UploadBitmap {
        val inputStream = requireActivity().contentResolver.openInputStream(filePath!!)
        val exif = ExifInterface(inputStream!!)
        val rotationAngle = when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        val matrix = Matrix().apply { postRotate(rotationAngle.toFloat()) }
        return UploadBitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun compressImage(bitmap: UploadBitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(UploadBitmap.CompressFormat.JPEG, 50, outputStream)
        return outputStream.toByteArray()
    }
}
