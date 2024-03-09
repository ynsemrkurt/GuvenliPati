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

class JobsAdapter(
    private val context: Context,
    private val jobList: ArrayList<Job>,
    private val petList: ArrayList<Pet>
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobsAdapter.ViewHolder, position: Int) {
        val job = jobList[position]

        val petId = job.petID

        val matchingPet = petList.find { it.petId == petId }

        matchingPet?.let {
            holder.bind(job, it)
        }
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
}