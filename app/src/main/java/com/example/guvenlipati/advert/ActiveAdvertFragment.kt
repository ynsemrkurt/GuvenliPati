package com.example.guvenlipati.advert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.ActiveOfferAdapter
import com.example.guvenlipati.OfferAdapter
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActiveAdvertFragment : Fragment() {

    lateinit var binding: com.example.guvenlipati.databinding.FragmentActiveAdvertBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=com.example.guvenlipati.databinding.FragmentActiveAdvertBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val activeAdvertRecycleView = binding.activeAdvertRecycleView
        activeAdvertRecycleView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val jobList = ArrayList<Job>()
        val petList = ArrayList<Pet>()
        val userList = ArrayList<User>()
        val offerList = ArrayList<Offer>()
        val backerList = ArrayList<Backer>()

        val adapter = ActiveOfferAdapter(requireContext(), jobList, petList, userList, offerList, backerList)
        activeAdvertRecycleView.adapter = adapter

        FirebaseDatabase.getInstance().getReference("offers").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                offerList.clear()
                for (offerSnapshot in dataSnapshot.children) {
                    val offer = offerSnapshot.getValue(Offer::class.java) ?: continue
                    if (offer.offerUser == firebaseUser?.uid && !offer.offerStatus && offer.priceStatus) {
                        offerList.add(offer)
                        FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(jobSnapshot: DataSnapshot) {
                                jobSnapshot.getValue(Job::class.java)?.let { job ->
                                    jobList.add(job)
                                    FirebaseDatabase.getInstance().getReference("pets").child(job.petID).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(petSnapshot: DataSnapshot) {
                                            petSnapshot.getValue(Pet::class.java)?.let { pet ->
                                                petList.add(pet)
                                                FirebaseDatabase.getInstance().getReference("users").child(offer.offerBackerId).addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                                        userSnapshot.getValue(User::class.java)?.let { user ->
                                                            userList.add(user)
                                                            FirebaseDatabase.getInstance().getReference("identifies").child(offer.offerBackerId).addListenerForSingleValueEvent(object : ValueEventListener {
                                                                override fun onDataChange(backerSnapshot: DataSnapshot) {
                                                                    backerSnapshot.getValue(Backer::class.java)?.let { backer ->
                                                                        backerList.add(backer)
                                                                        adapter.notifyDataSetChanged()  // Tüm veriler eklendikten sonra adapter'ı güncelle
                                                                    }
                                                                }
                                                                override fun onCancelled(error: DatabaseError) {}
                                                            })
                                                        }
                                                    }
                                                    override fun onCancelled(error: DatabaseError) {}
                                                })
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {}
                                    })
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Log error or show error message
            }
        })
    }

}