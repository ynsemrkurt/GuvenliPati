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
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.chat.ChatActivity
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.home.ListRatingActivity
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.example.guvenlipati.payment.PaymentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase

class OfferAdapter(
    private val context: Context,
    private val jobList: List<Job>,
    private val petList: List<Pet>,
    private val userList: List<User>,
    private val offerList: MutableList<Offer>,
    private val backerList: List<Backer>,
    internal var ratingList: List<Double>
) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_offer, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferAdapter.ViewHolder, position: Int) {
        if (position < jobList.size && position < petList.size && position < userList.size && position < offerList.size && position < backerList.size && position < ratingList.size) {
            holder.bind(
                jobList[position],
                petList[position],
                userList[position],
                offerList[position],
                backerList[position],
                ratingList[position]
            )
        }

        holder.itemView.findViewById<Button>(R.id.payButton).setOnClickListener {
            val offer = offerList[position]
            val job = jobList[position]
            val pet = petList[position]
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra("offerId", offer.offerId)
            intent.putExtra("jobId", job.jobId)
            intent.putExtra("paymentAmount", offer.offerPrice)
            intent.putExtra("backerId", offer.offerBackerId)
            intent.putExtra("petPhoto", pet.petPhoto)
            context.startActivity(intent)
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
        private val buttonDeleteOffer = view.findViewById<ImageButton>(R.id.buttonDeleteOffer)
        private val ratingBar=view.findViewById<RatingBar>(R.id.ratingBar)

        fun bind(job: Job, pet: Pet, user: User, offer: Offer, backer: Backer, rating: Double) {
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
            ratingBar.rating=rating.toFloat()

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

            buttonDeleteOffer.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Emin Misiniz?")
                    .setMessage("Teklifi silerseniz tekrar geri alamazsınız.")
                    .setBackground(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.background_dialog
                        )
                    )
                    .setPositiveButton("Sil") { _, _ ->
                        deleteOffer(position)
                    }
                    .setNegativeButton("İptal") { _, _ ->
                        showToast("Silme işlemi iptal edildi.")
                    }
                    .show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun deleteOffer(position: Int) {
        val offer = offerList[position]
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("offers").child(offer.offerId)
        databaseReference.removeValue()
            .addOnSuccessListener {
                showToast("Teklif silme işlemi başarılı.")
            }
            .addOnFailureListener { exception ->
                showToast("Teklif silme işlemi başarısız: ${exception.message}")
            }
    }
}