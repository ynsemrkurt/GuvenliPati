package com.example.guvenlipati.advert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.JobsAdapter
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class PastAdvertFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_advert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = Date()

        val pastAdvertRecycleView=view.findViewById<RecyclerView>(R.id.pastAdvertRecycleView)
        pastAdvertRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("jobs")
        val databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")

        val jobList = ArrayList<Job>()
        val petList = ArrayList<Pet>()

        databaseReferencePets.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(petsSnapshot: DataSnapshot) {
                petList.clear()
                for (dataSnapshot: DataSnapshot in petsSnapshot.children) {
                    val pet = dataSnapshot.getValue(Pet::class.java)
                    if (pet?.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                        pet?.let {
                            petList.add(it)
                        }
                    }
                }

                databaseReferenceJobs.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(jobsSnapshot: DataSnapshot) {
                        jobList.clear()
                        for (dataSnapshot: DataSnapshot in jobsSnapshot.children) {
                            val job = dataSnapshot.getValue(Job::class.java)
                            val startDate = SimpleDateFormat("dd/MM/yyyy").parse(job?.jobStartDate)
                            if (startDate != null) {
                                if (job?.userID == FirebaseAuth.getInstance().currentUser?.uid && startDate.before(currentDate)) {
                                    job?.let {
                                        jobList.add(it)
                                    }
                                }
                            }
                        }
                        val adapter = JobsAdapter(requireContext(),jobList, petList)
                        pastAdvertRecycleView.adapter = adapter
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