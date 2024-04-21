package com.example.guvenlipati

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase

class AdvertsAdapter(
    private val context: Context,
    private val jobList: ArrayList<Job>,
    private val petList: ArrayList<Pet>
) : RecyclerView.Adapter<AdvertsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_advert, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdvertsAdapter.ViewHolder, position: Int) {
        holder.advertCard.animation = AnimationUtils.loadAnimation(context, R.anim.recyclerview_anim)

        val job = jobList[position]

        val petId = job.petID

        val matchingPet = petList.find { it.petId == petId }

        matchingPet?.let {
            holder.bind(job, it)
        }

        holder.buttonDeleteAdvert.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Emin Misiniz?")
                .setMessage("İlanı silerseniz tekrar geri alamazsınız.")
                .setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.background_dialog
                    )
                )
                .setPositiveButton("Sil") { _, _ ->
                    deleteAdvert(position)
                }
                .setNegativeButton("İptal") { _, _ ->
                    showToast("Silme işlemi iptal edildi.")
                }
                .show()
        }
    }

    override fun getItemCount(): Int = jobList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val advertCard: LinearLayout = view.findViewById(R.id.advertCard)
        private val petPhotoImageView = view.findViewById<ImageView>(R.id.petPhotoImageView)
        private val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        private val jobTypeTextView= view.findViewById<TextView>(R.id.jobTypeTextView)
        private val petTypeTextView = view.findViewById<TextView>(R.id.petTypeTextView)
        private val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
        private val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
        private val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        val buttonDeleteAdvert = view.findViewById<ImageButton>(R.id.buttonDeleteAdvert)

        fun bind(job: Job, pet: Pet) {
            when(job.jobType){
                "feedingJob" -> jobTypeTextView.text = "Besleme"
                "walkingJob" -> jobTypeTextView.text = "Gezdirme"
                "homeJob" -> jobTypeTextView.text = "Evde Bakım"
            }
            petNameTextView.text = pet.petName
            petTypeTextView.text = pet.petBreed
            startDateTextView.text = job.jobStartDate
            endDateTextView.text = job.jobEndDate
            locationTextView.text = job.jobProvince + ", " + job.jobTown

            Glide.with(context)
                .load(pet.petPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhotoImageView)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun deleteAdvert(position: Int) {
        val job = jobList[position]
        val jobReference = FirebaseDatabase.getInstance().getReference("jobs").child(job.jobId)
        jobReference.child("jobStatus").setValue(false)
            .addOnSuccessListener {
                showToast("İlan başarıyla silindi.")
            }
            .addOnFailureListener { exception ->
                showToast("İlanın durumu güncellenirken bir hata oluştu: ${exception.message}")
            }
    }
}
