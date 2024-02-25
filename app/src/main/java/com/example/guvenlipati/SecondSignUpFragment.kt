package com.example.guvenlipati

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
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
import android.graphics.Bitmap as Bitmap1

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
        val progressCard=view.findViewById<CardView>(R.id.progressCard)


        val spinnerProvince: Spinner = view.findViewById(R.id.spinnerProvince)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.city_array,
            android.R.layout.simple_spinner_item
        )
        spinnerProvince.adapter = adapter

        val spinnerTown: Spinner = view.findViewById(R.id.spinnerTown)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.town_array,
            android.R.layout.simple_spinner_item
        )
        spinnerTown.adapter = adapter2

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
            val userProvince = spinnerProvince.selectedItem.toString()
            val userTown = spinnerTown.selectedItem.toString()


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

            saveProfileButton.visibility=View.INVISIBLE
            progressCard.visibility=View.VISIBLE

            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["userId"] = firebaseUser.uid
            hashMap["userPhoto"] = imageUrl
            hashMap["userName"] = userName.text.toString()
            hashMap["userSurname"] = userSurname.text.toString()
            hashMap["userGender"] = userGender.toString()
            hashMap["userProvince"] = userProvince
            hashMap["userTown"] = userTown

            databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Profil kaydı başarılı :)")
                } else {
                    showToast("Hatalı işlem!")
                }
                saveProfileButton.visibility=View.VISIBLE
                progressCard.visibility=View.INVISIBLE
            }
        }

        view.findViewById<ImageButton>(R.id.buttonAddProfileImage).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getContent.launch(Intent.createChooser(intent, "Select Profile Image"))
        }

        view.findViewById<ImageButton>(R.id.backToSplash).setOnClickListener{
            showAlertDialog()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showAlertDialog()
        }
    }
    private fun deleteUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Kullanıcı silindi.")
                (activity as MainActivity).goSplashFragment()
                showToast("Kayıt Silindi")
            } else {
                showToast("Silme işlemi başarısız!")
                task.exception
            }
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())

        alertDialogBuilder.setTitle("Geri dönmek istediğinize emin misiniz?")
        alertDialogBuilder.setMessage("Eğer geri dönerseniz kaydınız silinecektir.")

        alertDialogBuilder.setPositiveButton("Sil") { _, _ ->
            deleteUserData()
        }

        alertDialogBuilder.setNegativeButton("İptal") { _, _ ->
            showToast("İptal Edildi")
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                showToast("Image is uploading...")

                val originalBitmap: Bitmap1 =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
                val imageStream = ByteArrayOutputStream()
                originalBitmap.compress(Bitmap1.CompressFormat.PNG, 30, imageStream)
                val imageArray = imageStream.toByteArray()

                val ref: StorageReference = strgRef.child("image/" + firebaseUser.uid)
                ref.putBytes(imageArray)
                    .addOnSuccessListener {
                        showToast("Uploaded image!")
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                        }
                    }
                    .addOnFailureListener {
                        showToast("Failed, please try again!")
                    }

                view?.findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
                    ?.setImageBitmap(originalBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}