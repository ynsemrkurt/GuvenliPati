package com.example.guvenlipati.myjobs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guvenlipati.adapter.PendingJobAdapter
import com.example.guvenlipati.databinding.FragmentPendingJobBinding
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingJobFragment : Fragment() {

    lateinit var binding: FragmentPendingJobBinding
    private lateinit var adapter: PendingJobAdapter
    private val jobList = mutableListOf<Job>()
    private val petList = mutableListOf<Pet>()
    private val offerList = mutableListOf<Offer>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPendingJobBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadOffers()
    }

    private fun setupRecyclerView() {
        adapter = PendingJobAdapter(requireContext(), jobList, petList, offerList)
        binding.pendingJobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pendingJobRecyclerView.adapter = adapter
    }

    private fun loadOffers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("offers").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                clearLists()
                dataSnapshot.children.forEach { offerSnapshot ->
                    val offer = offerSnapshot.getValue(Offer::class.java)
                    if (offer != null && offer.offerBackerId == firebaseUser?.uid && !offer.priceStatus) {
                        fetchJobAndRelatedData(offer)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchJobAndRelatedData(offer: Offer) {
        FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(jobSnapshot: DataSnapshot) {
                val job = jobSnapshot.getValue(Job::class.java)
                if (job != null) {
                    fetchPet(job.petID, offer,job) // Teklif verisini fetchPete aktarın
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchPet(petId: String, offer: Offer, job: Job) {
        FirebaseDatabase.getInstance().getReference("pets").child(petId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(petSnapshot: DataSnapshot) {
                val pet = petSnapshot.getValue(Pet::class.java)
                if (pet != null) {
                    petList.add(pet)
                    offerList.add(offer)
                    jobList.add(job)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun clearLists() {
        jobList.clear()
        petList.clear()
        offerList.clear()
    }
}

