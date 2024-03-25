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
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate
import android.graphics.Bitmap as uploadBitmap

class SecondSignUpFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    private lateinit var getContent: ActivityResultLauncher<Intent>
    private var request: Int = 2020
    private var filePath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var strgRef: StorageReference
    private var imageUrl: String = ""

    private lateinit var binding: FragmentSecondSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userGender: Boolean? = null


        binding.buttonFemale.setOnClickListener {
            userGender = true
            binding.buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            binding.buttonFemale.setTextColor(Color.WHITE)
            binding.buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            binding.buttonMale.setTextColor(Color.BLACK)
        }

        binding.buttonMale.setOnClickListener {
            userGender = false
            binding.buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            binding.buttonMale.setTextColor(Color.WHITE)
            binding.buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            binding.buttonFemale.setTextColor(Color.BLACK)
        }

        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(request, result.resultCode, result.data)
            }

        storage = Firebase.storage
        strgRef = storage.reference


        binding.saveProfileButton.setOnClickListener {


            databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

            val userProvince = binding.provinceCombo.text.toString()
            val userTown = binding.townCombo.text.toString()


            if (binding.editTextUserName.text.trim().isEmpty()) {
                showToast("İsminizi giriniz!")
                return@setOnClickListener
            }

            if (binding.editTextUserSurname.text.trim().isEmpty()) {
                showToast("Soyadınızı giriniz!")
                return@setOnClickListener
            }

            if (userGender == null) {
                showToast("Cinsiyetinizi seçiniz!")
                return@setOnClickListener
            }

            if (userProvince.trim().isEmpty() || userTown.trim().isEmpty()) {
                showToast("Lütfen konum bilgilerinizi doldurunuz!")
                return@setOnClickListener
            }

            if (imageUrl==""){
                showToast("Lütfen profil fotoğrafınızı seçiniz!")
                return@setOnClickListener
            }

            binding.saveProfileButton.visibility = View.INVISIBLE
            binding.progressCard.visibility = View.VISIBLE
            binding.buttonPaw.visibility = View.INVISIBLE

            val currentDate = LocalDate.now()

            val currentDay = currentDate.dayOfMonth
            val currentMonth = currentDate.monthValue
            val currentYear = currentDate.year


            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["userId"] = firebaseUser.uid
            hashMap["userPhoto"] = imageUrl
            hashMap["userName"] = binding.editTextUserName.text.toString()
            hashMap["userSurname"] = binding.editTextUserSurname.text.toString()
            hashMap["userGender"] = userGender!!
            hashMap["userProvince"] = userProvince
            hashMap["userTown"] = userTown
            hashMap["userBacker"] = false
            hashMap["userRegisterDate"] = "$currentDay/$currentMonth/$currentYear"

            databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                        val token = result
                        databaseReference
                            .child("userToken").setValue(token) }
                    (activity as SplashActivity).goHomeActivity()
                } else {
                    showToast("Hatalı işlem!")
                }
                binding.saveProfileButton.visibility = View.VISIBLE
                binding.progressCard.visibility = View.INVISIBLE
                binding.buttonPaw.visibility = View.VISIBLE
            }
        }

        binding.buttonAddProfileImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getContent.launch(Intent.createChooser(intent, "Select Profile Image"))
        }

        binding.backToSplash.setOnClickListener {
            showMaterialDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showMaterialDialog()
        }
    }


    private fun deleteUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                (activity as SplashActivity).goSplashFragment()
            }
        }
    }

    private fun showMaterialDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz kaydınız silinecektir.").setBackground(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.background_dialog
                )
            ).setPositiveButton("Sil") { _, _ ->
                showToast("Kaydınız iptal edildi.")
                deleteUserData()
            }.setNegativeButton("İptal") { _, _ ->
                showToast("İptal Edildi")
            }.show()
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                showToast("Fotoğraf yükleniyor...")

                val inputStream = requireActivity().contentResolver.openInputStream(filePath!!)
                val exif = ExifInterface(inputStream!!)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )

                val originalBitmap =
                    MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        filePath
                    )

                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }


                val matrix = Matrix().apply { postRotate(rotationAngle.toFloat()) }
                val rotatedBitmap = uploadBitmap.createBitmap(
                    originalBitmap,
                    0,
                    0,
                    originalBitmap.width,
                    originalBitmap.height,
                    matrix,
                    true
                )

                val imageStream = ByteArrayOutputStream()

                rotatedBitmap.compress(uploadBitmap.CompressFormat.JPEG, 25, imageStream)

                val imageArray = imageStream.toByteArray()

                val ref: StorageReference = strgRef.child("image/" + firebaseUser.uid)
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
}