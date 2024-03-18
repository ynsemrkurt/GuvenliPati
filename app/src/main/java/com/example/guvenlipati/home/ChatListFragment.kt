package com.example.guvenlipati.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.FirebaseService
import com.example.guvenlipati.R
import com.example.guvenlipati.adapter.UserAdapter
import com.example.guvenlipati.databinding.FragmentChatListBinding
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class ChatListFragment : Fragment() {
    private lateinit var binding: FragmentChatListBinding
    private lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        val view = binding.root

        FirebaseService.sharedPref =
            fragmentContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseService.sharedPref =
            fragmentContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseService.sharedPref =
            fragmentContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        binding.chatRecycleView.layoutManager =
            LinearLayoutManager(fragmentContext, RecyclerView.VERTICAL, false)

        val userList = ArrayList<User>()
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")

        val xId = firebaseUser?.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$xId")

        firebaseUser?.let { currentUser ->
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val user: User? = dataSnapshot.getValue(User::class.java)
                        user?.let {
                            if (it.userId != currentUser.uid) {
                                userList.add(it)
                            }
                        }
                    }
                    val userAdapter = UserAdapter(fragmentContext, userList)
                    binding.chatRecycleView.adapter = userAdapter
                    binding.loadingCardView.visibility = View.GONE
                    binding.scrollView.foreground = null
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(fragmentContext, "Error: " + error.message, Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(fragmentContext, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
