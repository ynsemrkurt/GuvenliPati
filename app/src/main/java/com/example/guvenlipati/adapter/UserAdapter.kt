package com.example.guvenlipati.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.chat.ChatActivity
import com.example.guvenlipati.models.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textUserName: TextView = view.findViewById(R.id.textViewUsername)
        private val userImage: CircleImageView = view.findViewById(R.id.imageChatUser)
        private val textMessage: TextView = view.findViewById(R.id.textViewLastMessage)
        private val userLayout: LinearLayout = view.findViewById(R.id.userCard)

        fun bind(user: User) {
            textUserName.text = user.userName
            if (user.userPhoto.isNotEmpty()){
                Glide.with(context)
                    .load(Uri.parse(user.userPhoto))
                    .into(userImage)
            }


            userLayout.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("userId", user.userId)
                context.startActivity(intent)
            }
        }
    }
}
