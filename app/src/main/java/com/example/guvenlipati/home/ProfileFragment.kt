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
import com.example.guvenlipati.databinding.FragmentProfileBinding
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private lateinit var binding: FragmentProfileBinding

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
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        databaseReferencePets =
            FirebaseDatabase.getInstance().getReference("pets")

        binding.petRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val petList = ArrayList<Pet>()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val context = fragmentContext
                if (context != null && isAdded) {
                    user = snapshot.getValue(User::class.java)
                    if (user?.userId == firebaseUser.uid) {
                        if (user?.userPhoto!!.isEmpty()) {
                            binding.circleImageProfilePhoto.setImageResource(R.drawable.men_image)
                        } else {
                            val imageUri = Uri.parse(user?.userPhoto)
                            Glide.with(requireContext()).load(imageUri)
                                .into(binding.circleImageProfilePhoto)
                        }
                        binding.editTextUserName.setText(user?.userName)
                        binding.editTextUserSurname.setText(user?.userSurname)
                        binding.provinceCombo.setText(user?.userProvince)
                        binding.townCombo.setText(user?.userTown)

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
                                    binding.petRecycleView.adapter = petAdapter
                                    binding.loadingCardView.visibility = View.GONE
                                    binding.linearLayout.foreground = null
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                showToast("Yüklenirken Bir Hata Oluştu!")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Yüklenirken Bir Hata Oluştu!")
            }
        })

        binding.buttonChange.setOnClickListener {
            binding.buttonAddProfileImage.visibility = View.VISIBLE
            binding.editTextUserName.isEnabled = true
            binding.editTextUserSurname.isEnabled = true
            binding.textInputLayout.isEnabled = true
            binding.textInputLayout2.isEnabled = true
            binding.buttonSave.visibility = View.VISIBLE
            binding.buttonChange.visibility = View.INVISIBLE
            binding.petRecycleView.visibility = View.GONE
            binding.dostlarKahvesi.visibility = View.GONE
            binding.scrollProfile.setOnTouchListener { v, event -> true }

            val provinceAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.city_array, android.R.layout.simple_dropdown_item_1line
            )
            binding.provinceCombo.setAdapter(provinceAdapter)

            val townAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.town_array, android.R.layout.simple_dropdown_item_1line
            )
            binding.townCombo.setAdapter(townAdapter)
        }

        binding.buttonSave.setOnClickListener {

            val editTextUserName = binding.editTextUserName.text.toString().trim()
            val editTextUserSurname = binding.editTextUserSurname.text.toString().trim()
            val province = binding.provinceCombo.text.toString().trim()
            val town = binding.townCombo.text.toString().trim()

            if (editTextUserName.isEmpty()) {
                showToast("İsminizi giriniz!")
                return@setOnClickListener
            }

            if (editTextUserSurname.isEmpty()) {
                showToast("Soyadınızı giriniz!")
                return@setOnClickListener
            }

            if (province.isEmpty() || town.isEmpty()) {
                showToast("Lütfen konum bilgilerinizi doldurunuz!")
                return@setOnClickListener
            }

            databaseReference.updateChildren(
                mapOf(
                    "userName" to editTextUserName,
                    "userSurname" to editTextUserSurname,
                    "userProvince" to province,
                    "userPhoto" to user?.userPhoto,
                    "userTown" to town
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Düzenleme başarılı.")
                } else {
                    showToast("Düzenleme hatası: ${task.exception}")
                }
                (activity as HomeActivity).goProfileFragment()
                binding.buttonAddProfileImage.visibility = View.INVISIBLE
                binding.editTextUserName.isEnabled = false
                binding.editTextUserSurname.isEnabled = false
                binding.textInputLayout.isEnabled = false
                binding.textInputLayout2.isEnabled = false
                binding.buttonSave.visibility = View.INVISIBLE
                binding.buttonChange.visibility = View.VISIBLE
                binding.petRecycleView.visibility = View.VISIBLE
                binding.dostlarKahvesi.visibility = View.VISIBLE
                binding.scrollProfile.setOnTouchListener { v, event -> false }
            }
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
            getContent.launch(Intent.createChooser(intent, "Select Pet Image"))
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.buttonSave.visibility == View.VISIBLE) {
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
            binding.buttonSave.isEnabled = false
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
                            binding.buttonSave.isEnabled = true
                        }
                    }
                    .addOnFailureListener {
                        showToast("Başarısız, lütfen yeniden deneyin!")
                        binding.buttonSave.isEnabled = true
                    }
                binding.circleImageProfilePhoto.setImageBitmap(rotatedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
