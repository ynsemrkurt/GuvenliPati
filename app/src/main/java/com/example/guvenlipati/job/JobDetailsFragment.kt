package com.example.guvenlipati.job

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

private var jobId: String? = null
private lateinit var firebaseUser: FirebaseUser
private lateinit var identifies: DatabaseReference
private var money: Int = 0
private var jobPriceTextView: TextView? = null
private lateinit var linearLayout: LinearLayout
private lateinit var loadingCardView: CardView
private var job: Job? = null

class JobDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val petPhotoImageView = view.findViewById<ImageView>(R.id.petPhotoImageView)
        val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        val petGenderTextView = view.findViewById<TextView>(R.id.petGenderTextView)
        val petVaccinateTextView = view.findViewById<TextView>(R.id.petVaccinateTextView)
        val petTypeTextView = view.findViewById<TextView>(R.id.petTypeTextView)
        val petWeightTextView = view.findViewById<TextView>(R.id.petWeightTextView)
        val circleImageProfilePhoto =
            view.findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
        val userNameTextView = view.findViewById<TextView>(R.id.userNameTextView)
        val jobTypeTextView = view.findViewById<TextView>(R.id.jobTypeTextView)
        val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
        val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
        val jobAboutTextView = view.findViewById<TextView>(R.id.jobAboutTextView)
        jobPriceTextView = view.findViewById(R.id.jobPriceTextView)
        val petAboutTextView = view.findViewById<TextView>(R.id.petAboutTextView)
        val textViewAge=view.findViewById<TextView>(R.id.textViewAge)
        linearLayout=view.findViewById<LinearLayout>(R.id.linearLayout)
        loadingCardView=view.findViewById<CardView>(R.id.loadingCardView)

        arguments?.let {
            jobId = it.getString("jobId")
        }

        val jobRef = FirebaseDatabase.getInstance().reference.child("jobs").child(jobId!!)
        identifies =
            FirebaseDatabase.getInstance().reference.child("identifies").child(firebaseUser.uid)

        jobRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                job = snapshot.getValue(Job::class.java)!!
                val userRef =
                    FirebaseDatabase.getInstance().reference.child("users").child(job!!.userID)

                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)!!
                        val petRef =
                            FirebaseDatabase.getInstance().reference.child("pets")
                                .child(job!!.petID)

                        petRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val pet = snapshot.getValue(Pet::class.java)!!

                                Glide.with(requireContext()).load(pet.petPhoto)
                                    .into(petPhotoImageView)
                                petNameTextView.text = pet.petName
                                when (pet.petGender) {
                                    true -> {
                                        petGenderTextView.text = "Erkek"
                                    }

                                    false -> {
                                        petGenderTextView.text = "Kadın"
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
                                Glide.with(requireContext()).load(user.userPhoto)
                                    .into(circleImageProfilePhoto)
                                userNameTextView.text = user.userName
                                when (job!!.jobType) {
                                    "homeJob" -> {
                                        jobTypeTextView.text = "Evde Bakım"
                                        readMoney("homeMoney")
                                    }

                                    "feedingJob" -> {
                                        jobTypeTextView.text = "Besleme"
                                        readMoney("feedingMoney")
                                    }

                                    "walkingJob" -> {
                                        jobTypeTextView.text = "Gezdirme"
                                        readMoney("walkingMoney")
                                    }
                                }
                                locationTextView.text = job!!.jobProvince + ", " + job!!.jobTown
                                startDateTextView.text = job!!.jobStartDate
                                endDateTextView.text = job!!.jobEndDate
                                jobAboutTextView.text = job!!.jobAbout
                                petAboutTextView.text = pet.petAbout
                                textViewAge.text=pet.petAge+" Yaş"
                            }

                            override fun onCancelled(error: DatabaseError) {
                                showToast("Error: ${error.message}")
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Error: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    private fun calculateAndUpdatePrice() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val startDate = dateFormat.parse(job!!.jobStartDate)
        val endDate = dateFormat.parse(job!!.jobEndDate)

        val differenceInMillis = endDate.time - startDate.time

        val daysDifference = TimeUnit.MILLISECONDS.toDays(differenceInMillis) + 1

        val totalMoney = daysDifference * money
        jobPriceTextView?.text = "$totalMoney₺"

        linearLayout.foreground=null
        loadingCardView.visibility=View.GONE
    }

    private fun readMoney(column: String) {
        identifies.child(column).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                money = snapshot.getValue(Int::class.java)!!
                calculateAndUpdatePrice()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
