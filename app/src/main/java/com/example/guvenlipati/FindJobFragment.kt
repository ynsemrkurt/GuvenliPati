package com.example.guvenlipati

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FindJobFragment : Fragment() {

    private lateinit var databaseReferenceJobs: DatabaseReference
    private lateinit var jobRecyclerView: RecyclerView
    private val jobList = ArrayList<Job>()
    private val petList = ArrayList<Pet>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_job, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobRecyclerView = view.findViewById(R.id.jobRecycleView)
        jobRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("jobs")

        val databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")

        databaseReferencePets.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                petList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val pet = dataSnapShot.getValue(Pet::class.java)
                    pet?.let {
                        petList.add(it)
                    }
                }

                Log.d("JobsAdapter", "Pet list size: ${petList.size}")

                databaseReferenceJobs.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        jobList.clear()
                        for (dataSnapShot: DataSnapshot in snapshot.children) {
                            val job = dataSnapShot.getValue(Job::class.java)
                            job?.let {
                                jobList.add(it)
                            }
                        }

                        Log.d("JobsAdapter", "Job list size: ${jobList.size}")

                        val jobAdapter = JobsAdapter(
                            requireContext(),
                            jobList,
                            petList
                        )
                        jobRecyclerView.adapter = jobAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("JobsAdapter", "Error reading jobs from database: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JobsAdapter", "Error reading pets from database: ${error.message}")
            }
        })
    }
}
