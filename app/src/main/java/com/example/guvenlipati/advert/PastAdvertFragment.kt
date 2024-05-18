package com.example.guvenlipati.advert

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guvenlipati.PastAdvertsAdapter
import com.example.guvenlipati.databinding.FragmentPastAdvertBinding
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PastAdvertFragment : Fragment() {

    private lateinit var binding: FragmentPastAdvertBinding
    private lateinit var adapter: PastAdvertsAdapter
    private val jobList = ArrayList<Job>()
    private val petList = ArrayList<Pet>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPastAdvertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        setupRecyclerView()
        fetchJobs(currentUser!!)
    }

    private fun setupRecyclerView() {
        adapter = PastAdvertsAdapter(requireContext(), jobList, petList)
        binding.pastAdvertRecycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PastAdvertFragment.adapter
        }
    }

    private fun fetchPets(job: Job) {
        FirebaseDatabase.getInstance().getReference("pets")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(petsSnapshot: DataSnapshot) {
                    for (petSnapshot in petsSnapshot.children) {
                        petSnapshot.getValue(Pet::class.java)?.let { pet ->
                            if (pet.petId == job.petID) {
                                addListAndUpdateUI(job, pet)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun fetchJobs(userId: String) {
        val currentDate = Date()
        FirebaseDatabase.getInstance().getReference("jobs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(jobsSnapshot: DataSnapshot) {
                    jobsSnapshot.children.forEach { jobSnapshot ->
                        jobSnapshot.getValue(Job::class.java)?.let { job ->
                            if ((dateFormat.parse(job.jobStartDate)!!.before(currentDate) || !job.jobStatus) && job.userID == userId) {
                                clearListAndUpdateUI()
                                fetchPets(job)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun dbError() {
        Toast.makeText(
            requireContext(),
            "Veri Tabanı hatası lütfen daha sonra deneyiz!",
            Toast.LENGTH_SHORT
        ).show()
        requireActivity().finish()
    }

    private fun clearListAndUpdateUI() {
        binding.loadingCardView.visibility = View.VISIBLE
        binding.scrollView.foreground =
            ColorDrawable(Color.parseColor("#FFFFFF"))
        binding.animationView2.visibility = View.GONE
        jobList.clear()
        petList.clear()
    }

    private fun addListAndUpdateUI(job: Job, pet: Pet) {
        jobList.add(job)
        petList.add(pet)
        adapter.notifyDataSetChanged()
        binding.loadingCardView.visibility = View.GONE
        binding.scrollView.foreground = null
        binding.animationView2.visibility = View.GONE
    }
}