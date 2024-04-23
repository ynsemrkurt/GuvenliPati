package com.example.guvenlipati.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.R
import com.example.guvenlipati.RatingAdapter
import com.example.guvenlipati.databinding.ActivityRatingBinding
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

class RatingActivity : AppCompatActivity() {

    lateinit var binding: ActivityRatingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val ratingJobRecyclerView = binding.ratingJobRecyclerView
        ratingJobRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

            val jobList = ArrayList<Job>()
            val petList = ArrayList<Pet>()
            val userList = ArrayList<User>()
            val offerList = ArrayList<Offer>()
            val backerList = ArrayList<Backer>()

            val adapter = RatingAdapter(this, jobList, petList, userList, offerList, backerList)
        ratingJobRecyclerView.adapter = adapter

            FirebaseDatabase.getInstance().getReference("offers").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    offerList.clear()
                    jobList.clear()
                    petList.clear()
                    userList.clear()
                    backerList.clear()
                    for (offerSnapshot in dataSnapshot.children) {
                        val offer = offerSnapshot.getValue(Offer::class.java)
                        if (offer != null && offer.offerUser == firebaseUser?.uid && offer.offerStatus && !offer.ratingStatus) {
                            offerList.add(offer)
                            FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId).addValueEventListener(object :
                                ValueEventListener {
                                override fun onDataChange(jobSnapshot: DataSnapshot) {
                                    val job = jobSnapshot.getValue(Job::class.java)
                                    job?.let {
                                        jobList.add(it)
                                        FirebaseDatabase.getInstance().getReference("pets").child(job.petID).addValueEventListener(object :
                                            ValueEventListener {
                                            override fun onDataChange(petSnapshot: DataSnapshot) {
                                                val pet = petSnapshot.getValue(Pet::class.java)
                                                pet?.let {
                                                    petList.add(it)
                                                    FirebaseDatabase.getInstance().getReference("users").child(offer.offerBackerId).addValueEventListener(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(userSnapshot: DataSnapshot) {
                                                            val user = userSnapshot.getValue(User::class.java)
                                                            user?.let {
                                                                userList.add(it)
                                                                FirebaseDatabase.getInstance().getReference("identifies").child(offer.offerBackerId).addValueEventListener(object :
                                                                    ValueEventListener {
                                                                    override fun onDataChange(backerSnapshot: DataSnapshot) {
                                                                        val backer = backerSnapshot.getValue(
                                                                            Backer::class.java)
                                                                        backer?.let {
                                                                            backerList.add(it)
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
                                override fun onCancelled(error: DatabaseError) {}
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible database errors
                }
            })
        binding.backToSplash.setOnClickListener {
            onBackPressed()
            finish()
        }
    }


}