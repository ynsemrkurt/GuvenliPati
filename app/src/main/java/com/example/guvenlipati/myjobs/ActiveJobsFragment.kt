package com.example.guvenlipati.myjobs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.ActiveJobAdapter
import com.example.guvenlipati.ActiveOfferAdapter
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentActiveJobsBinding
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

class ActiveJobsFragment : Fragment() {

    lateinit var binding: FragmentActiveJobsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveJobsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val activeAdvertRecycleView = binding.activeJobRecycleView
        activeAdvertRecycleView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val jobList = ArrayList<Job>()
        val petList = ArrayList<Pet>()
        val userList = ArrayList<User>()
        val offerList = ArrayList<Offer>()

        val adapter = ActiveJobAdapter(requireContext(), jobList, petList, userList, offerList)
        activeAdvertRecycleView.adapter = adapter

        FirebaseDatabase.getInstance().getReference("offers").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                offerList.clear()
                jobList.clear()
                petList.clear()
                userList.clear()
                for (offerSnapshot in dataSnapshot.children) {
                    val offer = offerSnapshot.getValue(Offer::class.java) ?: continue
                    if (offer.offerBackerId == firebaseUser?.uid && !offer.offerStatus && offer.priceStatus) {
                        offerList.add(offer)
                        FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(jobSnapshot: DataSnapshot) {
                                jobSnapshot.getValue(Job::class.java)?.let { job ->
                                    jobList.add(job)
                                    FirebaseDatabase.getInstance().getReference("pets").child(job.petID).addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(petSnapshot: DataSnapshot) {
                                            petSnapshot.getValue(Pet::class.java)?.let { pet ->
                                                petList.add(pet)
                                                FirebaseDatabase.getInstance().getReference("users").child(offer.offerUser).addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                                        userSnapshot.getValue(User::class.java)?.let { user ->
                                                            userList.add(user)
                                                            adapter.notifyDataSetChanged()
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
            }
        })
    }
}