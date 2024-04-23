package com.example.guvenlipati

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime

class RatingAdapter(
    private val context: Context,
    private val jobList: List<Job>,
    private val petList: List<Pet>,
    private val userList: List<User>,
    private val offerList: ArrayList<Offer>,
    private val backerList: List<Backer>
) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottomsheet_rating, null)
        val backToMainButton = view.findViewById<Button>(R.id.backToMain)
        backToMainButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rating_job, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingAdapter.ViewHolder, position: Int) {
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
        private val buttonRate = view.findViewById<Button>(R.id.rateButton)

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


            buttonRate.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

                val inflater = LayoutInflater.from(context)
                val view2 = inflater.inflate(R.layout.item_rating, null)
                builder.setView(view2)

                val ratingBar = view2.findViewById<RatingBar>(R.id.ratingBar)
                val commentEditText = view2.findViewById<EditText>(R.id.editTextComment)
                val sendButton = view2.findViewById<Button>(R.id.buttonSend)

                val dialog = builder.create()
                dialog.show()

                sendButton.setOnClickListener {
                    if (ratingBar.rating == 0f) {
                        Toast.makeText(context, "Lütfen bir puan verin", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (commentEditText.text.toString().trim().isEmpty()) {
                        Toast.makeText(context, "Lütfen bir yorum girin", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val databaseReference = FirebaseDatabase.getInstance().getReference("ratings")
                    val databaseReferenceOffer =
                        FirebaseDatabase.getInstance().getReference("offers").child(offer.offerId)
                    val hashMap = HashMap<String, Any>()
                    hashMap["rating"] = ratingBar.rating
                    hashMap["comment"] = commentEditText.text.toString()
                    hashMap["userID"] = offer.offerBackerId
                    hashMap["date"] = LocalDateTime.now().toString()
                    databaseReference.child(offer.offerId).setValue(hashMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            databaseReferenceOffer.updateChildren(
                                mapOf(
                                    "ratingStatus" to true
                                )
                            )
                            dialog.dismiss()
                            showBottomSheet()
                        } else {
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
}