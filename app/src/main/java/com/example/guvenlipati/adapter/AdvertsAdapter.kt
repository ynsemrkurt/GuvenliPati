package com.example.guvenlipati

import android.content.Context
import android.util.Log
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
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdvertsAdapter(
    private val context: Context,
    private val jobList: MutableList<Job>,
    private val petList: MutableList<Pet>
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
        val jobId = job.jobId
        val jobReference = FirebaseDatabase.getInstance().getReference("jobs").child(jobId)

        // Set the job status to false, marking it as deleted.
        jobReference.child("jobStatus").setValue(false)
            .addOnSuccessListener {
                showToast("İlan başarıyla silindi.")
                deleteRelatedOffers(jobId) // Call to delete related offers
            }
            .addOnFailureListener { exception ->
                showToast("İlanın durumu güncellenirken bir hata oluştu: ${exception.message}")
            }
    }

    private fun deleteRelatedOffers(jobId: String) {
        val offersRef = FirebaseDatabase.getInstance().getReference("offers")
        offersRef.orderByChild("offerJobId").equalTo(jobId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { offerSnapshot ->
                        val offer = offerSnapshot.getValue(Offer::class.java)
                        if (offer != null && !offer.priceStatus) { // Check if priceStatus is false
                            offerSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    Log.d("AdvertsAdapter", "Related offer deleted successfully.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("AdvertsAdapter", "Failed to delete related offer: ${e.message}")
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdvertsAdapter", "Error fetching related offers: ${error.message}")
                }
            })
    }

}
