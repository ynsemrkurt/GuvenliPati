package com.example.guvenlipati.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(private val context: android.content.Context, private val messageList: ArrayList<Chat>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val friendMessageType = 0
    private val userMessageType = 1
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (viewType == userMessageType) R.layout.item_container_sent_message else R.layout.item_container_received_message
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = messageList[position]
        holder.textUserMessage.text = chat.messages
        holder.textDateTime.text = chat.currentTime
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUserMessage: TextView = itemView.findViewById(R.id.textMessage)
        val textDateTime:TextView = itemView.findViewById(R.id.textDateTime)
    }
    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (messageList[position].senderId == firebaseUser?.uid) userMessageType else friendMessageType
    }


}
