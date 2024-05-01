package com.example.guvenlipati.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.adapter.HomeBackersAdapter
import com.example.guvenlipati.adapter.HomePetsAdapter
import com.example.guvenlipati.adapter.SelectPetsAdapter
import com.example.guvenlipati.backer.PetBackerActivity
import com.example.guvenlipati.databinding.FragmentHomeBinding
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.Rating
import com.example.guvenlipati.models.User
import com.example.guvenlipati.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReferenceUsers: DatabaseReference
    private lateinit var databaseReferencePets: DatabaseReference
    private lateinit var databaseReferenceUsers2: DatabaseReference
    private lateinit var databaseReferenceBacker: DatabaseReference
    private lateinit var binding: FragmentHomeBinding

    val ratingList= mutableListOf<Double>()
    var ratingPoint:Double=0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReferenceUsers2 =
            FirebaseDatabase.getInstance().getReference("users")
        databaseReferenceBacker =
            FirebaseDatabase.getInstance().getReference("identifies")
        databaseReferenceUsers =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
                .child("userBacker")
        databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")
        val selectPetList = ArrayList<Pet>()
        val selectBackerList = ArrayList<Backer>()
        val selectUserList = ArrayList<User>()

        binding.petRecycleViewBacker.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        binding.petRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val context = requireContext()

        databaseReferenceUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isUserBacker = snapshot.getValue(Boolean::class.java) ?: false

                if (isUserBacker) {
                    binding.buttonConstraint.visibility = View.GONE
                }
                databaseReferencePets.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapShot: DataSnapshot in snapshot.children) {
                            val pet = dataSnapShot.getValue(Pet::class.java)
                            if (pet?.userId != firebaseUser.uid) {
                                pet.let {
                                    if (it != null) {
                                        selectPetList.add(it)
                                    }
                                }
                            }
                        }
                        val petAdapter =
                            HomePetsAdapter(
                                context,
                                selectPetList
                            )
                        binding.petRecycleView.adapter = petAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {
                makeText(activity, error.message, LENGTH_SHORT).show()
            }
        })

        databaseReferenceUsers2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    if (user?.userId != firebaseUser.uid) {
                        user.let {
                            if (it != null) {
                                selectUserList.add(it)
                            }
                        }
                    }
                }
                databaseReferenceBacker.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapShot: DataSnapshot in snapshot.children) {
                            val backer = dataSnapShot.getValue(Backer::class.java)
                            if (backer?.userID != firebaseUser.uid) {
                                backer.let {
                                    if (it != null) {
                                        selectBackerList.add(it)
                                    }
                                }
                                FirebaseDatabase.getInstance().getReference("ratings").addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var totalRating = 0.0
                                        var sayac = 0

                                        snapshot.children.forEach { ratingSnapshot ->
                                            val rating = ratingSnapshot.getValue(Rating::class.java) ?: return@forEach
                                            if (rating.backerId == backer!!.userID) {
                                                sayac++
                                                totalRating += rating.rating
                                            }
                                        }

                                        if (sayac > 0) {
                                            ratingPoint = totalRating / sayac
                                        } else {
                                            ratingPoint = 0.0
                                        }

                                        ratingList.add(ratingPoint)

                                        val selectBackerAdapter =
                                            HomeBackersAdapter(
                                                context,
                                                selectBackerList,
                                                selectUserList,
                                                ratingList
                                            )

                                        binding.petRecycleViewBacker.adapter = selectBackerAdapter
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("databaseProcessRating", "Database error: ${error.toException()}")
                                    }
                                })
                            }
                        }
                        binding.scrollView.foreground = null
                        binding.loadingCardView.visibility = View.GONE
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                makeText(activity, error.message, LENGTH_SHORT).show()
            }
        })

        binding.imageButton.setOnClickListener{
            (activity as HomeActivity).goActivity(OnboardingActivity())
        }

        binding.goBackerButton.setOnClickListener {
            (activity as HomeActivity).goActivity(PetBackerActivity())
        }
        permissionNotification()
    }

    private val appPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){}

    private fun permissionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }
    }
}
