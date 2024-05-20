package com.example.guvenlipati

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.job.JobDetailsActivity
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JobsAdapter(
    private val context: Context,
    private val jobList: MutableList<Job>,
    private val petList: MutableList<Pet>
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobsAdapter.ViewHolder, position: Int) {
        holder.petCard.animation= AnimationUtils.loadAnimation(context,R.anim.recyclerview_anim)

        val job = jobList[position]

        val petId = job.petID

        val matchingPet = petList.find { it.petId == petId }

        matchingPet?.let {
            holder.bind(job, it)
        }
    }

    override fun getItemCount(): Int = jobList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petCard: LinearLayout = view.findViewById(R.id.petCard)
        private val petPhotoImageView = view.findViewById<ImageView>(R.id.petPhotoImageView)
        private val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        private val jobTypeTextView= view.findViewById<TextView>(R.id.jobTypeTextView)
        private val petTypeTextView = view.findViewById<TextView>(R.id.petTypeTextView)
        private val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
        private val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
        private val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        private val countOfferTextView = view.findViewById<TextView>(R.id.countOfferTextView)


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


            val offersReference = FirebaseDatabase.getInstance().getReference("offers").child(job.jobId)
            offersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val offerCount = snapshot.childrenCount
                    countOfferTextView.text = offerCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


            itemView.setOnClickListener {
                val intent = Intent(context, JobDetailsActivity::class.java)
                intent.putExtra("job", job.jobId)
                context.startActivity(intent)
            }
        }
    }
}