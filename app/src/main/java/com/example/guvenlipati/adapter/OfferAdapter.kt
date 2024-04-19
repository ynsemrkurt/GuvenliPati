package com.example.guvenlipati

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User

class OfferAdapter(
    private val context: Context,
    private val jobList: ArrayList<Job>,
    private val petList: ArrayList<Pet>,
    private val userList: ArrayList<User>
) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferAdapter.ViewHolder, position: Int) {
        val job = jobList[position]
        holder.bind(job, petList[position], userList[position])
    }

    override fun getItemCount(): Int = jobList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petPhotoImageView = view.findViewById<ImageView>(R.id.petPhotoImageView)
        private val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        private val jobTypeTextView= view.findViewById<TextView>(R.id.jobTypeTextView)
        private val petTypeTextView = view.findViewById<TextView>(R.id.petTypeTextView)
        private val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
        private val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
        private val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        private val backerPhotoImageView = view.findViewById<ImageView>(R.id.backerPhotoImageView)
        private val backerNameTextView = view.findViewById<TextView>(R.id.backerNameTextView)

        fun bind(job: Job, pet: Pet, user: User) {
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
            backerNameTextView.text = user.userName


            Glide.with(context)
                .load(pet.petPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhotoImageView)

            Glide.with(context)
                .load(user.userPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(backerPhotoImageView)
        }
    }
}