package com.example.guvenlipati.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet

class JobsAdapter(
    private val context: Context,
    private val jobList: ArrayList<Job>,
    private val petList: ArrayList<Pet>
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_advert, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobList[position]

        val petId = job.petId

        val matchingPet = petList.find { it.petId == petId }

        matchingPet?.let {
            holder.bind(job, it)
        }
    }

    override fun getItemCount(): Int = jobList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petPhotoImageView = view.findViewById<ImageView>(R.id.petPhotoImageView)
        private val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        private val petTypeTextView = view.findViewById<TextView>(R.id.petTypeTextView)
        private val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
        private val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
        private val locationTextView = view.findViewById<TextView>(R.id.locationTextView)

        fun bind(job: Job, pet: Pet) {
            petNameTextView.text = pet.petName
            petTypeTextView.text = pet.petBreed
            startDateTextView.text = job.jobStartDate
            endDateTextView.text = job.jobFinishDate
            locationTextView.text = job.jobProvince + ", " + job.jobTown

            Glide.with(context)
                .load(Uri.parse(pet.petPhoto))
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhotoImageView)
        }
    }
}
