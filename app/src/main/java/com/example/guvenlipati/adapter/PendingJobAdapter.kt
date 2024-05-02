package com.example.guvenlipati.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PendingJobAdapter(
    private val context: Context,
    private val jobList: List<Job>,
    private val petList: List<Pet>,
    private val offerList: MutableList<Offer>
) : RecyclerView.Adapter<PendingJobAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pending_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingJobAdapter.ViewHolder, position: Int) {
        if (position < jobList.size && position < petList.size  && position < offerList.size) {
            holder.bind(
                jobList[position],
                petList[position],
                offerList[position]
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
        private val priceTextView = view.findViewById<TextView>(R.id.priceTotalTextView)
        private val buttonDeleteOffer = view.findViewById<ImageView>(R.id.buttonDeleteOffer)

        @SuppressLint("SetTextI18n")
        fun bind(job: Job, pet: Pet, offer: Offer) {
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
            priceTextView.text = offer.offerPrice.toString() + " TL"


            Glide.with(context)
                .load(pet.petPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhotoImageView)

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
                        deleteOffer(offer.offerId)
                    }
                    .setNegativeButton("İptal") { _, _ ->
                        showToast("Silme işlemi iptal edildi.")
                    }
                    .show()
            }
            petPhotoImageView.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

                val inflater = LayoutInflater.from(context)
                val view2 = inflater.inflate(R.layout.item_pet_preview, null)
                builder.setView(view2)

                val petPhotoImageView = view2.findViewById<ImageView>(R.id.petPhotoImageView)
                val petNameTextView = view2.findViewById<TextView>(R.id.petNameTextView)
                val textViewAge = view2.findViewById<TextView>(R.id.textViewAge)
                val petGenderTextView = view2.findViewById<TextView>(R.id.petGenderTextView)
                val petVaccinateTextView = view2.findViewById<TextView>(R.id.petVaccinateTextView)
                val petTypeTextView = view2.findViewById<TextView>(R.id.petTypeTextView)
                val petWeightTextView = view2.findViewById<TextView>(R.id.petWeightTextView)
                val petAboutTextView = view2.findViewById<TextView>(R.id.petAboutTextView)
                val infoButton = view2.findViewById<ImageButton>(R.id.infoButton)

                if (pet.petPhoto.isNotEmpty()) {
                    Glide.with(context)
                        .load(Uri.parse(pet.petPhoto))
                        .into(petPhotoImageView)
                } else {
                    petPhotoImageView.setImageResource(R.drawable.default_pet_image_2)
                }

                val petYearInt = pet.petBirthYear.toInt()
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy")
                val currentYearInt = currentDateTime.format(formatter).toInt()
                val petAge = currentYearInt - petYearInt

                petNameTextView.text=pet.petName
                textViewAge.text= "$petAge Yaş"
                when (pet.petGender) {
                    true -> {
                        petGenderTextView.text = "Dişi"
                    }

                    false -> {
                        petGenderTextView.text = "Erkek"
                    }
                }
                when (pet.petVaccinate) {
                    true -> {
                        petVaccinateTextView.text = "Yapılıyor"
                    }

                    false -> {
                        petVaccinateTextView.text = "Aşısız"
                    }
                }
                petTypeTextView.text = pet.petBreed
                petWeightTextView.text = pet.petWeight + " Kg"
                petAboutTextView.text = pet.petAbout

                infoButton.setOnClickListener {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("userId", pet.userId)
                    context.startActivity(intent)
                }
                val dialog = builder.create()
                dialog.show()
            }

        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        private fun deleteOffer(offerId:String) {
            val databaseReference =
                FirebaseDatabase.getInstance().getReference("offers").child(offerId)
            databaseReference.removeValue()
                .addOnSuccessListener {
                    showToast("Teklif silme işlemi başarılı.")
                }
                .addOnFailureListener { exception ->
                    showToast("Teklif silme işlemi başarısız: ${exception.message}")
                }
        }
    }
}
