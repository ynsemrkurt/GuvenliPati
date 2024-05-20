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
import com.example.guvenlipati.adapter.ActiveJobAdapter
import com.example.guvenlipati.databinding.FragmentActiveJobsBinding
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

    private lateinit var binding: FragmentActiveJobsBinding
    private lateinit var adapter: ActiveJobAdapter
    private val jobList = mutableListOf<Job>()
    private val petList = mutableListOf<Pet>()
    private val userList = mutableListOf<User>()
    private val offerList = mutableListOf<Offer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadActiveOffers()
    }

    private fun setupRecyclerView() {
        adapter = ActiveJobAdapter(requireContext(), jobList, petList, userList, offerList)
        binding.activeJobRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.activeJobRecycleView.adapter = adapter
    }

    private fun loadActiveOffers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference("offers")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                clearLists()

                dataSnapshot.children.forEach { snapshot ->
                    val offer = snapshot.getValue(Offer::class.java)
                    if (offer != null && offer.offerBackerId == firebaseUser?.uid && !offer.offerStatus && offer.priceStatus) {
                        binding.activeJobRecycleView.foreground =
                            ColorDrawable(Color.parseColor("#FFFFFF"))
                        binding.loadingCardView.visibility = View.VISIBLE
                        fetchJob(offer)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun clearLists() {
        jobList.clear()
        petList.clear()
        userList.clear()
        offerList.clear()
    }

    private fun fetchJob(offer: Offer) {
        FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val job = snapshot.getValue(Job::class.java)
                    if (job != null) {
                        fetchUser(offer, job)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchUser(offer: Offer, job: Job) {
        FirebaseDatabase.getInstance().getReference("users").child(offer.offerUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(User::class.java)?.let {
                        fetchPet(offer, job, it)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchPet(offer: Offer, job: Job, user: User) {
        FirebaseDatabase.getInstance().getReference("pets").child(job.petID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Pet::class.java)?.let {
                        petList.add(it)
                        jobList.add(job)
                        userList.add(user)
                        offerList.add(offer)
                        binding.activeJobRecycleView.foreground = null
                        binding.loadingCardView.visibility = View.GONE
                        binding.animationView2.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
