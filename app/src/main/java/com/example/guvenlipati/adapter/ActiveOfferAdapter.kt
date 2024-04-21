package com.example.guvenlipati

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.chat.ChatActivity
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.example.guvenlipati.payment.PaymentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime

class ActiveOfferAdapter(
    private val context: Context,
    private val jobList: List<Job>,
    private val petList: List<Pet>,
    private val userList: List<User>,
    private val offerList: ArrayList<Offer>,
    private val backerList: List<Backer>
) : RecyclerView.Adapter<ActiveOfferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_active_offer, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveOfferAdapter.ViewHolder, position: Int) {
        if (position < jobList.size && position < petList.size && position < userList.size && position < offerList.size && position < backerList.size) {
            holder.bind(
                jobList[position],
                petList[position],
                userList[position],
                offerList[position],
                backerList[position]
            )
        }
    }

    override fun getItemCount(): Int = offerList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petPhotoImageView = view.findViewById<ImageView>(R.id.petPhotoImageView)
        private val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        private val jobTypeTextView = view.findViewById<TextView>(R.id.jobTypeTextView)
        private val petTypeTextView = view.findViewById<TextView>(R.id.petTypeTextView)
        private val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
        private val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
        private val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        private val backerPhotoImageView =
            view.findViewById<ImageView>(R.id.backerPhotoImageView)
        private val backerNameTextView = view.findViewById<TextView>(R.id.backerNameTextView)
        private val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
        private val buttonGoChat = view.findViewById<ImageButton>(R.id.buttonGoChat)

        fun bind(job: Job, pet: Pet, user: User, offer: Offer, backer: Backer) {
            when (job.jobType) {
                "feedingJob" -> jobTypeTextView.text = "Besleme"
                "walkingJob" -> jobTypeTextView.text = "Gezdirme"
                "homeJob" -> jobTypeTextView.text = "Evde Bakım"
            }
            petNameTextView.text = pet.petName
            petTypeTextView.text = pet.petBreed
            startDateTextView.text = job.jobStartDate
            endDateTextView.text = job.jobEndDate
            locationTextView.text = job.jobProvince + ", " + job.jobTown
            backerNameTextView.text = user.userName + " " + user.userSurname
            priceTextView.text = offer.offerPrice.toString() + " TL"

            Glide.with(context)
                .load(pet.petPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhotoImageView)

            Glide.with(context)
                .load(user.userPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(backerPhotoImageView)

            buttonGoChat.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("userId", user.userId)
                context.startActivity(intent)
            }

            backerPhotoImageView.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

                val inflater = LayoutInflater.from(context)
                val view2 = inflater.inflate(R.layout.item_backer_preview, null)
                builder.setView(view2)

                val petPhotoImageView = view2.findViewById<ImageView>(R.id.petPhotoImageView)
                val backerNameTextView = view2.findViewById<TextView>(R.id.backerNameTextView)
                val textViewAge = view2.findViewById<TextView>(R.id.textViewAge)
                val petGenderTextView = view2.findViewById<TextView>(R.id.petGenderTextView)
                val backerLocationTextView =
                    view2.findViewById<TextView>(R.id.backerLocationTextView)
                val petNumberTextView = view2.findViewById<TextView>(R.id.petNumberTextView)
                val backerExperienceTextView =
                    view2.findViewById<TextView>(R.id.backerExperienceTextView)
                val backerAboutTextView = view2.findViewById<TextView>(R.id.backerAboutTextView)
                val infoButton = view2.findViewById<ImageButton>(R.id.infoButton)

                if (user.userPhoto.isNotEmpty()) {
                    Glide.with(context)
                        .load(Uri.parse(user.userPhoto))
                        .into(petPhotoImageView)
                }

                backerNameTextView.text = user.userName
                val currentYear = LocalDateTime.now().year
                textViewAge.text =
                    (currentYear - backer.backerBirthYear.toInt()).toString() + " Yaşında"
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

                infoButton.setOnClickListener {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("userId", backer.userID)
                    context.startActivity(intent)
                }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}