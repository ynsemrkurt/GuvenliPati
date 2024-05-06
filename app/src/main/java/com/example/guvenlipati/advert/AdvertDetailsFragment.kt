package com.example.guvenlipati.advert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.databinding.FragmentAdvertDetailsBinding
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AdvertDetailsFragment : Fragment() {

    private var job: Job? = null
    private lateinit var binding: FragmentAdvertDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdvertDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadJob()

        binding.circleImageProfilePhoto.setOnClickListener {
            goToProfile()
        }
    }

    private fun goToProfile() {
        val intent = Intent(requireContext(), ProfileActivity::class.java)
        intent.putExtra("userId", job?.userID)
        startActivity(intent)
    }

    private fun loadJob() {
        var jobId: String? = null
        arguments?.let {
            jobId = it.getString("jobId")
        }
        val jobRef = FirebaseDatabase.getInstance().reference.child("jobs").child(jobId!!)
        jobRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                job = snapshot.getValue(Job::class.java)!!
                fetchUser(job!!)
            }

            override fun onCancelled(error: DatabaseError) {
                dbError()
            }
        })
    }

    private fun fetchUser(job: Job) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(job.userID)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                fetchPet(job, user)
            }

            override fun onCancelled(error: DatabaseError) {
                dbError()
            }
        })
    }

    private fun fetchPet(job: Job, user: User) {
        val petRef = FirebaseDatabase.getInstance().reference.child("pets").child(job.petID)
        petRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pet = snapshot.getValue(Pet::class.java)!!
                uploadInfo(pet, job, user)
            }

            override fun onCancelled(error: DatabaseError) {
                dbError()
            }
        })
    }

    private fun uploadInfo(pet: Pet, job: Job, user: User) {
        loadPhoto(pet.petPhoto, binding.petPhotoImageView)
        loadPhoto(user.userPhoto, binding.circleImageProfilePhoto)
        binding.petNameTextView.text = pet.petName
        binding.petGenderTextView.text = gender(pet.petGender)
        binding.petVaccinateTextView.text = isVaccinate(pet.petVaccinate)
        binding.petTypeTextView.text = pet.petBreed
        binding.petWeightTextView.text = pet.petWeight + " Kg"
        binding.userNameTextView.text = user.userName
        binding.locationTextView.text = job.jobProvince + ", " + job.jobTown
        binding.startDateTextView.text = job.jobStartDate
        binding.endDateTextView.text = job.jobEndDate
        binding.jobAboutTextView.text = job.jobAbout
        binding.petAboutTextView.text = pet.petAbout
        binding.textViewAge.text = "${calculateAge(pet.petBirthYear)} Yaş"
        updateUI()
    }

    private fun gender(gender: Boolean): String {
        return if (gender) {
            "Dişi"
        } else {
            "Erkek"
        }
    }

    private fun isVaccinate(vaccinate: Boolean): String {
        return if (vaccinate) {
            "Yapılıyor"
        } else {
            "Aşısız"
        }
    }

    private fun calculateAge(birthDate: String): Int {
        val petYearInt = birthDate.toInt()
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val currentYearInt = currentDateTime.format(formatter).toInt()
        return currentYearInt - petYearInt
    }

    private fun loadPhoto(uri: String, imageView: ImageView) {
        Glide.with(requireContext()).load(uri)
            .into(imageView)
    }

    private fun updateUI() {
        binding.linearLayout.foreground = null
        binding.loadingCardView.visibility = View.GONE
    }

    private fun dbError() {
        Toast.makeText(requireContext(), "Veri tabanı hatası daha sonra deneyiniz!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }
}
