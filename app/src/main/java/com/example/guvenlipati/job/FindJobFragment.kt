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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class FindJobFragment : Fragment() {

    private lateinit var databaseReferenceJobs: DatabaseReference
    private lateinit var jobRecyclerView: RecyclerView
    private val jobList = ArrayList<Job>()
    private val petList = ArrayList<Pet>()
    private lateinit var databaseReferenceIdentifies: DatabaseReference
    private lateinit var binding: FragmentFindJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = Date()

        jobRecyclerView = binding.jobRecycleView
        jobRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        databaseReferenceIdentifies =
            FirebaseDatabase.getInstance().getReference("identifies").child(
                FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
        databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("jobs")
        val databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")

        databaseReferenceIdentifies.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {

                databaseReferencePets.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(petsSnapshot: DataSnapshot) {
                        petList.clear()
                        for (dataSnapshot: DataSnapshot in petsSnapshot.children) {
                            val pet = dataSnapshot.getValue(Pet::class.java)
                            pet?.let {
                                petList.add(it)
                            }
                        }

                        databaseReferenceJobs.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(jobsSnapshot: DataSnapshot) {
                                jobList.clear()
                                for (dataSnapshot: DataSnapshot in jobsSnapshot.children) {
                                    val job = dataSnapshot.getValue(Job::class.java)
                                    val startDate =
                                        SimpleDateFormat("dd/MM/yyyy").parse(job?.jobStartDate)
                                    job?.let {
                                        if (userSnapshot.child(job.jobType)
                                                .getValue(Boolean::class.java) == true && userSnapshot.child(
                                                job.petSpecies + "Backer"
                                            )
                                                .getValue(Boolean::class.java) == true && job.userID != FirebaseAuth.getInstance().currentUser?.uid.toString() && !startDate.before(
                                                currentDate
                                            ) && job.jobStatus
                                        ) {
                                            jobList.add(it)
                                        }
                                    }
                                }


                                val jobAdapter = JobsAdapter(
                                    requireContext(),
                                    jobList,
                                    petList
                                )
                                jobRecyclerView.adapter = jobAdapter
                                if (jobList.isNotEmpty()) {
                                    binding.animationView2.visibility = View.GONE
                                }
                                binding.scrollView.foreground = null
                                binding.loadingCardView.visibility = View.GONE
                            }

                            override fun onCancelled(error: DatabaseError) {
                                showToast("Hata!")
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Hata!")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Hata!")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
