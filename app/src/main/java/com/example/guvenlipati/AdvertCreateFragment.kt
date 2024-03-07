package com.example.guvenlipati

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.adapter.PetsAdapter
import com.example.guvenlipati.adapter.SelectPetsAdapter
import com.example.guvenlipati.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdvertCreateFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReferencePets: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advert_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")

        val petRecyclerView = view.findViewById<RecyclerView>(R.id.petRecycleView)
        petRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val selectPetList = ArrayList<Pet>()

        databaseReferencePets.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                selectPetList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val pet = dataSnapShot.getValue(Pet::class.java)
                    if (pet?.userId == firebaseUser.uid) {
                        pet.let {
                            selectPetList.add(it)
                        }
                    }
                }

                val petAdapter = SelectPetsAdapter(requireContext(), selectPetList)
                petRecyclerView.adapter = petAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }
}