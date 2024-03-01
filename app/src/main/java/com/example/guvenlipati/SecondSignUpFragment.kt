package com.example.guvenlipati

import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
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
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userGender: Boolean? = null
        val buttonFemale = view.findViewById<Button>(R.id.buttonFemale)
        val buttonMale = view.findViewById<Button>(R.id.buttonMale)
        val saveProfileButton = view.findViewById<Button>(R.id.saveProfileButton)
        val progressCard = view.findViewById<CardView>(R.id.progressCard)
        val buttonPaw = view.findViewById<ImageView>(R.id.buttonPaw)
        val provinceCombo=view.findViewById<AutoCompleteTextView>(R.id.provinceCombo)
        val townCombo=view.findViewById<AutoCompleteTextView>(R.id.townCombo)


        buttonFemale.setOnClickListener {
            userGender = true
            buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonFemale.setTextColor(Color.WHITE)
            buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonMale.setTextColor(Color.BLACK)
        }

        buttonMale.setOnClickListener {
            userGender = false
            buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonMale.setTextColor(Color.WHITE)
            buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonFemale.setTextColor(Color.BLACK)
        }

        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(request, result.resultCode, result.data)
            }

        storage = Firebase.storage
        strgRef = storage.reference


        saveProfileButton.setOnClickListener {


            databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

            val userName = view.findViewById<EditText>(R.id.editTextUserName)
            val userSurname = view.findViewById<EditText>(R.id.editTextUserSurname)
            val userProvince = provinceCombo.text.toString()
            val userTown=townCombo.text.toString()


            if (userName.text.isEmpty()) {
                showToast("İsminizi giriniz!")
                return@setOnClickListener
            }

            if (userSurname.text.isEmpty()) {
                showToast("Soyadınızı giriniz!")
                return@setOnClickListener
            }

            if (userGender == null) {
                showToast("Cinsiyetinizi seçiniz!")
                return@setOnClickListener
            }

            if (userProvince.isEmpty() || userTown.isEmpty()){
                showToast("Lütfen konum bilgilerinizi doldurunuz!")
                return@setOnClickListener
            }

            saveProfileButton.visibility = View.INVISIBLE
            progressCard.visibility = View.VISIBLE
            buttonPaw.visibility = View.INVISIBLE

            val currentDate = LocalDate.now()

            val currentDay = currentDate.dayOfMonth
            val currentMonth = currentDate.monthValue
            val currentYear = currentDate.year


            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["userId"] = firebaseUser.uid
            hashMap["userPhoto"] = imageUrl
            hashMap["userName"] = userName.text.toString()
            hashMap["userSurname"] = userSurname.text.toString()
            hashMap["userGender"] = userGender.toString()
            hashMap["userProvince"] = userProvince
            hashMap["userTown"] = userTown
            hashMap["userRegisterDate"] = "$currentDay/$currentMonth/$currentYear"

            databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    (activity as MainActivity).goHomeActivity()
                } else {
                    showToast("Hatalı işlem!")
                }
                saveProfileButton.visibility = View.VISIBLE
                progressCard.visibility = View.INVISIBLE
                buttonPaw.visibility = View.VISIBLE
            }
        }

        view.findViewById<ImageButton>(R.id.buttonAddProfileImage).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getContent.launch(Intent.createChooser(intent, "Select Profile Image"))
        }

        view.findViewById<ImageButton>(R.id.backToSplash).setOnClickListener {
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
                (activity as MainActivity).goSplashFragment()
                showToast("Kullanıcı silindi.")
            } else {
                showToast("Silme işlemi başarısız!")
                task.exception
            }
        }
    }

    private fun showMaterialDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz kaydınız silinecektir.")
            .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.background_dialog))
            .setPositiveButton("Sil") { _, _ ->
                showToast("Kaydınız iptal edildi.")
                deleteUserData()
            }
            .setNegativeButton("İptal") { _, _ ->
                showToast("İptal Edildi")
            }
            .show()
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
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val originalBitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)

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

                rotatedBitmap.compress(uploadBitmap.CompressFormat.JPEG, 30, imageStream)

                val imageArray = imageStream.toByteArray()

                val ref: StorageReference = strgRef.child("image/" + firebaseUser.uid)
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

                view?.findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
                    ?.setImageBitmap(rotatedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}