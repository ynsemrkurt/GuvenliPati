package com.example.guvenlipati.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.home.ListRatingActivity
import com.example.guvenlipati.home.RatingActivity
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import java.time.LocalDateTime

class HomeBackersAdapter(
    private val context: Context,
    private val selectBackerList: ArrayList<Backer>,
    private val selectUserList: ArrayList<User>,
    private var ratingList: List<Double>
) :
    RecyclerView.Adapter<HomeBackersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_backer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeBackersAdapter.ViewHolder, position: Int) {
        holder.backerCard.animation= AnimationUtils.loadAnimation(context,R.anim.recyclerview_anim)
        val backer = selectBackerList[position]
        val user = selectUserList.find { it.userId == backer.userID }
        val rating=ratingList[position]
        if (user != null) {
            holder.bind(backer, user, position, rating)
        } else {
            Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = selectBackerList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val backerCard: LinearLayout = view.findViewById(R.id.backerCard)
        private val backerPhoto: ImageView = view.findViewById(R.id.backerImage)
        private val backerName: TextView = view.findViewById(R.id.backerName)
        private val backerLocation: TextView = view.findViewById(R.id.backerLocation)


        fun bind(backer: Backer, user: User, position: Int, rating: Double) {

            if (user.userPhoto.isNotEmpty()) {
                Glide.with(context)
                    .load(Uri.parse(user.userPhoto))
                    .into(backerPhoto)
            }

            backerName.text = user.userName
            backerLocation.text = user.userProvince + "/" + user.userTown


            itemView.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

                val inflater = LayoutInflater.from(context)
                val view2 = inflater.inflate(R.layout.item_backer_preview, null)
                builder.setView(view2)

                val petPhotoImageView = view2.findViewById<ImageView>(R.id.petPhotoImageView)
                val backerNameTextView = view2.findViewById<TextView>(R.id.backerNameTextView)
                val petGenderTextView = view2.findViewById<TextView>(R.id.petGenderTextView)
                val backerLocationTextView =
                    view2.findViewById<TextView>(R.id.backerLocationTextView)
                val petNumberTextView = view2.findViewById<TextView>(R.id.petNumberTextView)
                val backerExperienceTextView =
                    view2.findViewById<TextView>(R.id.backerExperienceTextView)
                val backerAboutTextView = view2.findViewById<TextView>(R.id.backerAboutTextView)
                val infoButton = view2.findViewById<ImageButton>(R.id.infoButton)
                val ratingButton = view2.findViewById<ImageButton>(R.id.ratingButton)
                val totalRateTextView= view2.findViewById<TextView>(R.id.totalRateTextView)


                if (user.userPhoto.isNotEmpty()) {
                    Glide.with(context)
                        .load(Uri.parse(user.userPhoto))
                        .into(petPhotoImageView)
                }

                backerNameTextView.text = user.userName
                when (user.userGender) {
                    true -> {
                        petGenderTextView.text = "Kadın"
                    }

                    false -> {
                        petGenderTextView.text = "Erkek"
                    }
                }
                backerLocationTextView.text = user.userProvince + "/" + user.userTown
                petNumberTextView.text = backer.petNumber
                backerExperienceTextView.text = backer.experience
                backerAboutTextView.text = backer.about
                totalRateTextView.text=rating.toString()


                infoButton.setOnClickListener {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("userId", backer.userID)
                    context.startActivity(intent)
                }

                ratingButton.setOnClickListener{
                    val intent = Intent(context, ListRatingActivity::class.java)
                    intent.putExtra("userId", user.userId)
                    context.startActivity(intent)
                }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}