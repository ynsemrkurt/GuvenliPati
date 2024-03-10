package com.example.guvenlipati.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.adapter.PetsAdapter
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
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

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferencePets: DatabaseReference
    private var fragmentContext: Context? = null
    private var user: User? = null

    private lateinit var getContent: ActivityResultLauncher<Intent>
    private var request: Int = 2020
    private var filePath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var strgRef: StorageReference
    private var imageUrl: String = ""
    private lateinit var buttonSave: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDetach() {
        super.onDetach()
        fragmentContext = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        databaseReferencePets =
            FirebaseDatabase.getInstance().getReference("pets")

        val profilePhoto = view.findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
        val userNameEdit = view.findViewById<EditText>(R.id.editTextUserName)
        val userSurname = view.findViewById<EditText>(R.id.editTextUserSurname)
        val provinceCombo = view.findViewById<AutoCompleteTextView>(R.id.provinceCombo)
        val provinceComboLayout = view.findViewById<TextInputLayout>(R.id.textInputLayout)
        val townCombo = view.findViewById<AutoCompleteTextView>(R.id.townCombo)
        val townComboLayout = view.findViewById<TextInputLayout>(R.id.textInputLayout2)
        val petRecyclerView = view.findViewById<RecyclerView>(R.id.petRecycleView)
        buttonSave = view.findViewById(R.id.buttonSave)
        val buttonChange = view.findViewById<Button>(R.id.buttonChange)
        val buttonAddProfileImage = view.findViewById<ImageButton>(R.id.buttonAddProfileImage)
        val friendsText = view.findViewById<TextView>(R.id.dostlarKahvesi)
        val loadingCardView = view.findViewById<View>(R.id.loadingCardView)
        val linearLayout = view.findViewById<View>(R.id.linearLayout)



        petRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val petList = ArrayList<Pet>()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val context = fragmentContext
                if (context != null && isAdded) {
                    user = snapshot.getValue(User::class.java)
                    if (user?.userId == firebaseUser.uid) {
                        if (user?.userPhoto!!.isEmpty()) {
                            profilePhoto.setImageResource(R.drawable.men_image)
                        } else {
                            val imageUri = Uri.parse(user?.userPhoto)
                            Glide.with(requireContext()).load(imageUri)
                                .placeholder(R.drawable.men_image)
                                .into(profilePhoto)
                        }
                        userNameEdit.setText(user?.userName)
                        userSurname.setText(user?.userSurname)
                        provinceCombo.setText(user?.userProvince)
                        townCombo.setText(user?.userTown)

                        loadingCardView.visibility = View.GONE
                        linearLayout.foreground = null
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Hata!")
            }
        })

        databaseReferencePets.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val context = fragmentContext
                if (context != null && isAdded) {
                    petList.clear()
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val pet = dataSnapShot.getValue(Pet::class.java)
                        if (pet?.userId == firebaseUser.uid) {
                            pet.let {
                                petList.add(it)
                            }
                        }
                    }

                    val petAdapter = PetsAdapter(requireContext(), petList)
                    petRecyclerView.adapter = petAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Hata!")
            }
        })

        buttonChange.setOnClickListener {
            buttonAddProfileImage.visibility = View.VISIBLE
            userNameEdit.isEnabled = true
            userSurname.isEnabled = true
            provinceComboLayout.isEnabled = true
            townComboLayout.isEnabled = true
            buttonSave.visibility = View.VISIBLE
            buttonChange.visibility = View.INVISIBLE
            petRecyclerView.visibility = View.INVISIBLE
            friendsText.visibility = View.INVISIBLE

            val provinceAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.city_array, android.R.layout.simple_dropdown_item_1line
            )
            provinceCombo.setAdapter(provinceAdapter)

            val townAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.town_array, android.R.layout.simple_dropdown_item_1line
            )
            townCombo.setAdapter(townAdapter)
        }

        buttonSave.setOnClickListener {

            if (userNameEdit.text.isEmpty()) {
                showToast("İsminizi giriniz!")
                return@setOnClickListener
            }

            if (userSurname.text.isEmpty()) {
                showToast("Soyadınızı giriniz!")
                return@setOnClickListener
            }

            if (provinceCombo.text.isEmpty() || townCombo.text.isEmpty()) {
                showToast("Lütfen konum bilgilerinizi doldurunuz!")
                return@setOnClickListener
            }

            databaseReference.updateChildren(
                mapOf(
                    "userName" to userNameEdit.text.toString(),
                    "userSurname" to userSurname.text.toString(),
                    "userProvince" to provinceCombo.text.toString(),
                    "userPhoto" to user?.userPhoto,
                    "userTown" to townCombo.text.toString()
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Düzenleme başarılı.")
                } else {
                    showToast("Düzenleme hatası: ${task.exception}")
                }
                buttonAddProfileImage.visibility = View.INVISIBLE
                userNameEdit.isEnabled = false
                userSurname.isEnabled = false
                provinceComboLayout.isEnabled = false
                townComboLayout.isEnabled = false
                buttonSave.visibility = View.INVISIBLE
                buttonChange.visibility = View.VISIBLE
                petRecyclerView.visibility = View.VISIBLE
                friendsText.visibility = View.VISIBLE
            }
        }

        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(request, result.resultCode, result.data)
            }

        storage = Firebase.storage
        strgRef = storage.reference

        buttonAddProfileImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getContent.launch(Intent.createChooser(intent, "Select Pet Image"))
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (buttonSave.visibility == View.VISIBLE) {
                showMaterialDialog()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun showMaterialDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz düzenlemeniz kaydedilmeyecek.")
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_dialog
                )
            )
            .setPositiveButton("Geri Dön") { _, _ ->
                showToast("Değişiklikler iptal edildi.")
                (activity as HomeActivity).goProfileFragment()
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
            buttonSave.isEnabled = false
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
                            user?.userPhoto = imageUrl
                        }
                    }
                    .addOnFailureListener {
                        showToast("Başarısız, lütfen yeniden deneyin!")
                    }
                buttonSave.isEnabled = true
                view?.findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
                    ?.setImageBitmap(rotatedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
