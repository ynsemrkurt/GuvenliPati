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
    private val userList = ArrayList<User>()
    private val userMap = HashMap<String, User>()
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("chat")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chatRecycleView.layoutManager =
            LinearLayoutManager(fragmentContext, RecyclerView.VERTICAL, false)

        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        firebaseUser?.let { currentUser ->
            loadChat(currentUser)
        }
    }

    private fun loadChat(currentUser: FirebaseUser) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isAdded) {
                    userList.clear()
                    userMap.clear()

                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val senderId =
                            dataSnapshot.child("senderId").getValue(String::class.java)
                        val recipientId =
                            dataSnapshot.child("recipientId").getValue(String::class.java)

                        if (senderId == currentUser.uid || recipientId == currentUser.uid) {
                            val otherUserId =
                                if (senderId == currentUser.uid) recipientId else senderId
                            if (otherUserId != null) {
                                binding.animationView2.visibility = View.GONE
                                fetchUser(otherUserId)
                            }
                        }
                    }
                    binding.loadingCardView.visibility = View.GONE
                    binding.scrollView.foreground = null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(
                        fragmentContext,
                        "Error: " + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun fetchUser(userId: String) {
        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    if (isAdded) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let {
                            if (!userMap.containsKey(userId)) {
                                userList.add(it)
                                userMap[userId] = it
                                binding.chatRecycleView.adapter =
                                    UserAdapter(fragmentContext, userList)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        (activity as HomeActivity).showToast("Error: " + error.message)
                    }
                }
            })
    }
}