package com.example.guvenlipati.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.RatingAdapter
import com.example.guvenlipati.databinding.ActivityRatingBinding
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingBinding
    private lateinit var jobList: ArrayList<Job>
    private lateinit var petList: ArrayList<Pet>
    private lateinit var userList: ArrayList<User>
    private lateinit var offerList: ArrayList<Offer>
    private lateinit var backerList: ArrayList<Backer>
    private lateinit var adapter: RatingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        fetchOffers()
        setBackButtonListener()
    }

    private fun initializeViews() {
        binding.ratingJobRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        jobList = ArrayList()
        petList = ArrayList()
        userList = ArrayList()
        offerList = ArrayList()
        backerList = ArrayList()
        adapter = RatingAdapter(this, jobList, petList, userList, offerList, backerList)
        binding.ratingJobRecyclerView.adapter = adapter
    }

    private fun fetchOffers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val offerReference = FirebaseDatabase.getInstance().getReference("offers")
        offerReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                offerList.clear()
                jobList.clear()
                petList.clear()
                userList.clear()
                backerList.clear()
                for (offerSnapshot in dataSnapshot.children) {
                    val offer = offerSnapshot.getValue(Offer::class.java)
                    if (offer != null && offer.offerUser == firebaseUser?.uid && offer.offerStatus && !offer.ratingStatus) {
                        binding.animationView2.visibility = View.GONE
                        binding.loadingCardView.visibility = View.VISIBLE
                        binding.linearLayout.foreground = ColorDrawable(Color.parseColor("#FFFFFF"))
                        offerList.add(offer)
                        fetchJobData(offer)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) { }
        })
    }

    private fun fetchJobData(offer: Offer) {
        val jobReference = FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId)
        jobReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(jobSnapshot: DataSnapshot) {
                val job = jobSnapshot.getValue(Job::class.java)
                job?.let {
                    jobList.add(it)
                    fetchPetData(it, offer)
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun fetchPetData(job: Job, offer: Offer) {
        val petReference = FirebaseDatabase.getInstance().getReference("pets").child(job.petID)
        petReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(petSnapshot: DataSnapshot) {
                val pet = petSnapshot.getValue(Pet::class.java)
                pet?.let {
                    petList.add(it)
                    fetchUserData(offer)
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun fetchUserData(offer: Offer) {
        val userReference =
            FirebaseDatabase.getInstance().getReference("users").child(offer.offerBackerId)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val user = userSnapshot.getValue(User::class.java)
                user?.let {
                    userList.add(it)
                    fetchBackerData(offer)
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun fetchBackerData(offer: Offer) {
        val backerReference =
            FirebaseDatabase.getInstance().getReference("identifies").child(offer.offerBackerId)
        backerReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(backerSnapshot: DataSnapshot) {
                val backer = backerSnapshot.getValue(Backer::class.java)
                backer?.let {
                    backerList.add(it)
                    updateAdapterAndUI()
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun updateAdapterAndUI() {
        adapter.notifyDataSetChanged()
        binding.loadingCardView.visibility = View.GONE
        binding.linearLayout.foreground = null
    }

    private fun setBackButtonListener() {
        binding.backToSplash.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}
