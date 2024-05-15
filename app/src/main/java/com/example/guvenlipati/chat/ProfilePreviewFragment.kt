package com.example.guvenlipati.chat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.adapter.HomePetsAdapter
import com.example.guvenlipati.databinding.FragmentProfilePreviewBinding
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfilePreviewFragment : Fragment() {
    lateinit var binding: FragmentProfilePreviewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var databaseReferencePets: DatabaseReference
    private var fragmentContext: Context? = null
    private var user: User? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDetach() {
        super.onDetach()
        fragmentContext = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

        val friendUserId = activity?.intent?.getStringExtra("userId").toString()
        reference = FirebaseDatabase.getInstance().getReference("users").child(friendUserId)
        databaseReferencePets =
            FirebaseDatabase.getInstance().getReference("pets")

        binding.petRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val petList = ArrayList<Pet>()

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                if (user!!.userPhoto.isEmpty()) {
                    Glide.with(requireContext()).load(R.drawable.men_image).into(binding.circleImageProfilePhoto)
                }else {
                    Glide.with(requireContext()).load(user?.userPhoto).into(binding.circleImageProfilePhoto)
                }
                binding.editTextUserName.setText(user?.userName)
                binding.editTextUserSurname.setText(user?.userSurname)
                binding.provinceCombo.setText(user?.userProvince)
                binding.townCombo.setText(user?.userTown)

                if (user?.userGender == true) {
                    binding.buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
                    binding.buttonFemale.setTextColor(Color.WHITE)
                    binding.buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg)
                    binding.buttonMale.setTextColor(Color.BLACK)
                } else {
                    binding.buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
                    binding.buttonMale.setTextColor(Color.WHITE)
                    binding.buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg)
                    binding.buttonFemale.setTextColor(Color.BLACK)
                }
                binding.previewScrollView.foreground = null
                binding.loadingCardView.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        databaseReferencePets.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val context = fragmentContext
                if (context != null && isAdded) {
                    petList.clear()
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val pet = dataSnapShot.getValue(Pet::class.java)
                        if (pet?.userId == friendUserId) {
                            pet.let {
                                petList.add(it)
                            }
                        }
                    }
                    val petAdapter = HomePetsAdapter(requireContext(), petList)
                    binding.petRecycleView.adapter = petAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Yüklenirken Bir Hata Oluştu!")
            }
        })

        binding.backToSplash.setOnClickListener {
            requireActivity().finish()
        }

    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}