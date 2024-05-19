package com.example.guvenlipati.job

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.JobsAdapter
import com.example.guvenlipati.databinding.FragmentFindJobBinding
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date

class FindJobFragment : Fragment() {

    private lateinit var binding: FragmentFindJobBinding
    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var jobsAdapter: JobsAdapter
    private val jobList = mutableListOf<Job>()
    private val petList = mutableListOf<Pet>()

    private val databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("jobs")
    private val databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")
    private val databaseReferenceIdentifies = FirebaseDatabase.getInstance()
        .getReference("identifies")
        .child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchUserPreferences()
    }

    private fun setupRecyclerView() {
        jobRecyclerView = binding.jobRecycleView
        jobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobsAdapter = JobsAdapter(requireContext(), jobList, petList)
        jobRecyclerView.adapter = jobsAdapter
    }

    private fun fetchUserPreferences() {
        databaseReferenceIdentifies.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                fetchJobs(userSnapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast()
            }
        })
    }

    private fun fetchJobs(userSnapshot: DataSnapshot) {
        databaseReferenceJobs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(jobsSnapshot: DataSnapshot) {
                clearLists()
                for (dataSnapshot in jobsSnapshot.children) {
                    val job = dataSnapshot.getValue(Job::class.java)
                    job?.let { if (isJobValid(it, userSnapshot)) jobList.add(it) }
                }
                fetchPets()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast()
            }
        })
    }

    private fun fetchPets() {
        databaseReferencePets.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(petsSnapshot: DataSnapshot) {
                for (dataSnapshot in petsSnapshot.children) {
                    val pet = dataSnapshot.getValue(Pet::class.java)
                    pet?.let { if (!petList.contains(it)) petList.add(it) }
                }
                updateUI()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast()
            }
        })
    }

    private fun clearLists() {
        jobList.clear()
        petList.clear()
    }

    private fun isJobValid(job: Job, userSnapshot: DataSnapshot): Boolean {
        val currentDate = Date()
        val jobStartDate = SimpleDateFormat("dd/MM/yyyy").parse(job.jobStartDate)
        return userSnapshot.child(job.petSpecies + "Backer").getValue(Boolean::class.java) == true &&
                job.userID != FirebaseAuth.getInstance().currentUser?.uid.toString() &&
                jobStartDate != null && !jobStartDate.before(currentDate) &&
                job.jobStatus
    }

    private fun updateUI() {
        jobsAdapter.notifyDataSetChanged()
        binding.animationView2.visibility = if (jobList.isNotEmpty()) View.GONE else View.VISIBLE
        binding.scrollView.foreground = null
        binding.loadingCardView.visibility = View.GONE
    }

    private fun showToast() {
        Toast.makeText(requireContext(), "Veri Tabanı Hatası!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }
}
