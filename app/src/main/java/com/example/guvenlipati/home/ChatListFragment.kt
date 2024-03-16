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
import com.example.guvenlipati.R
import com.example.guvenlipati.adapter.UserAdapter
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ChatListFragment : Fragment() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        chatRecyclerView = view.findViewById(R.id.chatRecycleView)
        chatRecyclerView.layoutManager = LinearLayoutManager(fragmentContext, RecyclerView.VERTICAL, false)

        val userList = ArrayList<User>()
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

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
                    chatRecyclerView.adapter = userAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(fragmentContext, "ERROR", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(fragmentContext, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
