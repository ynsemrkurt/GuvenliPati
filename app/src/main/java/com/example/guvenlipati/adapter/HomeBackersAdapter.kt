package com.example.guvenlipati.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User

class HomeBackersAdapter(
    private val context: Context,
    private val selectBackerList: ArrayList<Backer>,
    private val selectUserList: ArrayList<User>
) :
    RecyclerView.Adapter<HomeBackersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_backer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeBackersAdapter.ViewHolder, position: Int) {
        val backer = selectBackerList[position]
        val user = selectUserList.find { it.userId == backer.userID }
        if (user != null) {
            holder.bind(backer, user, position)
        } else {
            Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = selectBackerList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val backerPhoto: ImageView = view.findViewById(R.id.backerImage)
        private val backerName: TextView = view.findViewById(R.id.backerName)
        private val backerLocation: TextView = view.findViewById(R.id.backerLocation)

        fun bind(backer: Backer, user: User, position: Int) {

            Glide.with(context)
                .load(Uri.parse(user.userPhoto))
                .into(backerPhoto)

            backerName.text = user.userName
            backerLocation.text = user.userProvince+"/"+user.userTown


            itemView.setOnClickListener {
            }
        }
    }
}