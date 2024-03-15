package com.example.guvenlipati.chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.adapter.MessageAdapter
import com.example.guvenlipati.models.Chat
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChatingFragment : Fragment() {

    private var userId: String? = null
    private lateinit var recyclerViewMessages: RecyclerView
    private var chatList = ArrayList<Chat>()
    private var reference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userId = it.getString("userId")
        }

        val friendUserId = activity?.intent?.getStringExtra("userId").toString()
        reference = FirebaseDatabase.getInstance().getReference("users").child(friendUserId)
        recyclerViewMessages = view.findViewById(R.id.recycleViewMessages)

        recyclerViewMessages.layoutManager =
            LinearLayoutManager(fragmentContext, RecyclerView.VERTICAL, false)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                Glide.with(requireContext()).load(user?.userPhoto)
                    .into(view.findViewById(R.id.imagePhoto))
                view.findViewById<TextView>(R.id.textFriendName).text = user?.userName
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formattedDateTime = currentTime.format(formatter)

        view.findViewById<ImageButton>(R.id.buttonGoChat).setOnClickListener {
            if (view.findViewById<EditText>(R.id.editTextMessage).text.toString().isNotEmpty()) {
                reference = FirebaseDatabase.getInstance().getReference().child("chat")

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["senderId"] = firebaseUser?.uid.toString()
                hashMap["recipientId"] = friendUserId
                hashMap["messages"] = view.findViewById<EditText>(R.id.editTextMessage).text.toString()
                hashMap["currentTime"] = formattedDateTime.toString()
                reference!!.push().setValue(hashMap)
                view.findViewById<EditText>(R.id.editTextMessage).setText("")
            }
        }
        messageList(friendUserId)
    }

    private fun messageList(friendId: String) {
        chatList.clear()
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if ((chat?.senderId == firebaseUser?.uid && chat?.recipientId == friendId) ||
                        (chat?.senderId == friendId && chat.recipientId == firebaseUser?.uid)
                    ) {
                        chatList.add(chat)
                    }
                }
                val chatAdapter = MessageAdapter(fragmentContext, chatList)
                recyclerViewMessages.adapter = chatAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
