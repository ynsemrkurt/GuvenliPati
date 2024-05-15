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

    private lateinit var binding: ActivityListRatingBinding
    private val userRatingPairs = ArrayList<UserRatingPair>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        val userId = intent.getStringExtra("userId") ?: ""
        val databaseReferenceRatings = FirebaseDatabase.getInstance().getReference("ratings")

        databaseReferenceRatings.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(ratingsSnapshot: DataSnapshot) {
                ratingsSnapshot.children.forEach { ratingSnapshot ->
                    val rating = ratingSnapshot.getValue(Rating::class.java)
                    if (rating?.backerId == userId) {
                        showLoadingState(true)
                        fetchUserRating(rating.userId) { user ->
                            userRatingPairs.add(UserRatingPair(user, rating))
                            binding.listRatingRecycleView.adapter?.notifyDataSetChanged()
                            showLoadingState(false)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })

        binding.backToSplash.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.listRatingRecycleView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ListRatingAdapter(this, userRatingPairs)
    }

    private fun fetchUserRating(userId: String, onUserFetched: (User?) -> Unit) {
        val databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(userId)
        databaseReferenceUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                val user = usersSnapshot.getValue(User::class.java)
                onUserFetched(user)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

    private fun showLoadingState(loading: Boolean) {
        if (loading) {
            binding.loadingCardView.visibility = View.VISIBLE
            binding.linearLayout.foreground = ColorDrawable(Color.parseColor("#FFFFFF"))
            binding.animationView2.visibility = View.GONE
        } else {
            binding.loadingCardView.visibility = View.GONE
            binding.linearLayout.foreground = null
        }
    }
}

data class UserRatingPair(
    val user: User?,
    val rating: Rating?
)
