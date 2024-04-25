package com.example.guvenlipati.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.ListRatingAdapter
import com.example.guvenlipati.databinding.ActivityListRatingBinding
import com.example.guvenlipati.models.Rating
import com.example.guvenlipati.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListRatingActivity : AppCompatActivity() {

    lateinit var binding: ActivityListRatingBinding
    private val userList = ArrayList<User>()
    private val ratingList = ArrayList<Rating>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView: RecyclerView = binding.listRatingRecycleView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = ListRatingAdapter(this, userList, ratingList)
        recyclerView.adapter = adapter

        val userId = intent.getStringExtra("userId")
        val databaseReferenceRatings = FirebaseDatabase.getInstance().getReference("ratings")


        databaseReferenceRatings.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(ratingsSnapshot: DataSnapshot) {
                for (ratingSnapshot in ratingsSnapshot.children) {
                    val rating = ratingSnapshot.getValue(Rating::class.java)
                    if (rating!!.backerId == userId) {
                        rating.let {
                            ratingList.add(it)
                            val databaseReferenceUsers =
                                FirebaseDatabase.getInstance().getReference("users").child(rating.userId)
                            databaseReferenceUsers.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(usersSnapshot: DataSnapshot) {
                                    for (userSnapshot in usersSnapshot.children) {
                                        val user = userSnapshot.getValue(User::class.java)
                                        user?.let {
                                            userList.add(it)
                                        }
                                    }
                                    adapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
