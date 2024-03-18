package com.example.guvenlipati.chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.RetrofitInstance
import com.example.guvenlipati.adapter.MessageAdapter
import com.example.guvenlipati.databinding.FragmentChatingBinding
import com.example.guvenlipati.models.Chat
import com.example.guvenlipati.models.Notification
import com.example.guvenlipati.models.PushNotification
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

import com.google.gson.Gson
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChatingFragment : Fragment() {

    private var userId: String? = null
    private lateinit var binding: FragmentChatingBinding
    private var chatList = ArrayList<Chat>()
    private var reference: DatabaseReference? = null
    private var reference2: DatabaseReference? = null
    private var user: User? = null
    private var userData: User? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var fragmentContext: Context
    private var topic = "/topics/myTopic"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun scrollToBottom() {
        binding.recycleViewMessages.post {
            binding.recycleViewMessages.scrollToPosition(chatList.size - 1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userId = it.getString("userId")
        }

        val friendUserId = activity?.intent?.getStringExtra("userId").toString()
        reference = FirebaseDatabase.getInstance().getReference("users").child(friendUserId)
        reference2 = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser!!.uid)

        binding.recycleViewMessages.layoutManager = LinearLayoutManager(fragmentContext)

        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                if (user!!.userPhoto.isEmpty()) {
                    Glide.with(requireContext()).load(R.drawable.men_image).into(binding.imagePhoto)
                }else {
                    Glide.with(requireContext()).load(user?.userPhoto).into(binding.imagePhoto)
                }
                binding.textFriendName.text = user?.userName
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        reference2!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userData = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formattedDateTime = currentTime.format(formatter)

        binding.buttonGoChat.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isNotEmpty()) {
                reference = FirebaseDatabase.getInstance().getReference("chat")

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["senderId"] = firebaseUser?.uid.toString()
                hashMap["recipientId"] = friendUserId
                hashMap["messages"] = message
                hashMap["currentTime"] = formattedDateTime.toString()
                reference!!.push().setValue(hashMap)
                binding.editTextMessage.setText("")
                scrollToBottom()

                topic = "/topics/$friendUserId"
                PushNotification(
                    Notification(
                        userData!!.userName,
                        message,
                        firebaseUser!!.uid,
                        userData!!.userPhoto
                    ),
                    topic
                ).also {
                    sendNotification(it)
                }
            }
        }
        messageList(friendUserId)

        view.findViewById<ImageView>(R.id.backButton).setOnClickListener{
            activity?.finish()
        }
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
                binding.recycleViewMessages.adapter = chatAdapter
                scrollToBottom()
                binding.loadingCardView.visibility = View.GONE
                binding.constraint.foreground = null
            }


            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
