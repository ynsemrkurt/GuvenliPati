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
import com.example.guvenlipati.adapter.PastJobAdapter
import com.example.guvenlipati.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PastJobFragment : Fragment() {

    private lateinit var binding: com.example.guvenlipati.databinding.FragmentPastJobBinding
    private lateinit var adapter: PastJobAdapter
    private val jobList = mutableListOf<Job>()
    private val petList = mutableListOf<Pet>()
    private val userList = mutableListOf<User>()
    private val offerList = mutableListOf<Offer>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = com.example.guvenlipati.databinding.FragmentPastJobBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadOffers()
    }

    private fun setupRecyclerView() {
        adapter = PastJobAdapter(requireContext(), jobList, petList, userList, offerList)
        binding.pastJobRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.pastJobRecycleView.adapter = adapter
    }

    private fun loadOffers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("offers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    clearLists()
                    dataSnapshot.children.forEach { offerSnapshot ->
                        val offer = offerSnapshot.getValue(Offer::class.java)
                        if (offer != null && offer.offerBackerId == firebaseUser?.uid && offer.offerStatus) {
                            binding.pastJobRecycleView.foreground =
                                ColorDrawable(Color.parseColor("#FFFFFF"))
                            binding.loadingCardView.visibility = View.VISIBLE
                            fetchJobAndRelatedData(offer)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Error loading data: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun fetchJobAndRelatedData(offer: Offer) {
        FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(jobSnapshot: DataSnapshot) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    if (job != null) {
                        fetchUser(offer, job)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchPet(offer: Offer, job: Job, user: User) {
        FirebaseDatabase.getInstance().getReference("pets").child(job.petID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(petSnapshot: DataSnapshot) {
                    val pet = petSnapshot.getValue(Pet::class.java)
                    if (pet != null) {
                        petList.add(pet)
                        userList.add(user)
                        offerList.add(offer)
                        jobList.add(job)
                        adapter.notifyDataSetChanged()
                        binding.animationView2.visibility = View.GONE
                        binding.pastJobRecycleView.foreground = null
                        binding.loadingCardView.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchUser(offer: Offer, job: Job) {
        FirebaseDatabase.getInstance().getReference("users").child(offer.offerUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        fetchPet(offer, job, user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun clearLists() {
        jobList.clear()
        petList.clear()
        userList.clear()
        offerList.clear()
    }
}
