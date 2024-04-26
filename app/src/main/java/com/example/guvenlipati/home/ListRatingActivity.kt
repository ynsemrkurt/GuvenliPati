package com.example.guvenlipati.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
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
    private val userRatingPairs = ArrayList<UserRatingPair>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView: RecyclerView = binding.listRatingRecycleView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ListRatingAdapter(this, userRatingPairs)
        recyclerView.adapter = adapter

        val userId = intent.getStringExtra("userId") ?: ""
        val databaseReferenceRatings = FirebaseDatabase.getInstance().getReference("ratings")

        databaseReferenceRatings.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(ratingsSnapshot: DataSnapshot) {
                ratingsSnapshot.children.forEach { ratingSnapshot ->
                    binding.loadingCardView.visibility = View.VISIBLE
                    binding.linearLayout.foreground=ColorDrawable(Color.parseColor("#FFFFFF"))
                    val rating = ratingSnapshot.getValue(Rating::class.java)
                    if (rating?.backerId == userId) {
                        fetchUser(rating.userId) { user ->
                            userRatingPairs.add(UserRatingPair(user, rating))
                            adapter.notifyDataSetChanged()
                            binding.animationView2.visibility=View.GONE
                            binding.loadingCardView.visibility = View.GONE
                            binding.linearLayout.foreground=null
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.backToSplash.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun fetchUser(userId: String, onUserFetched: (User?) -> Unit) {
        val databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(userId)
        databaseReferenceUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                val user = usersSnapshot.getValue(User::class.java)
                onUserFetched(user)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}


data class UserRatingPair(
    val user: User?,
    val rating : Rating?
)