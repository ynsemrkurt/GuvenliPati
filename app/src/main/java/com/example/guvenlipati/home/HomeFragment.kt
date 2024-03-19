package com.example.guvenlipati.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.adapter.HomePetsAdapter
import com.example.guvenlipati.adapter.SelectPetsAdapter
import com.example.guvenlipati.databinding.FragmentHomeBinding
import com.example.guvenlipati.models.Pet
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
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReferenceUsers =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
                .child("userBacker")
        databaseReferencePets=FirebaseDatabase.getInstance().getReference("pets")
        val selectPetList = ArrayList<Pet>()

        binding.petRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val context=requireContext()

        databaseReferenceUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isUserBacker = snapshot.getValue(Boolean::class.java) ?: false

                if (isUserBacker) {
                    binding.buttonConstraint.visibility = View.GONE
                }
                databaseReferencePets.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapShot: DataSnapshot in snapshot.children) {
                            val pet = dataSnapShot.getValue(Pet::class.java)
                            if (pet?.userId!=firebaseUser.uid){
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
                        binding.petRecycleView.adapter=petAdapter
                        binding.scrollView.foreground = null
                        binding.loadingCardView.visibility = View.GONE
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        binding.goBackerButton.setOnClickListener {
            (activity as HomeActivity).goPetBackerActivity()
        }
    }
}
