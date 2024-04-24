package com.example.guvenlipati.advert

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.AdvertsAdapter
import com.example.guvenlipati.JobsAdapter
import com.example.guvenlipati.PastAdvertsAdapter
import com.example.guvenlipati.R
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

class PastAdvertFragment : Fragment() {

    private lateinit var binding: FragmentPastAdvertBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPastAdvertBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = Date()

        val pastAdvertRecycleView=binding.pastAdvertRecycleView
        pastAdvertRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("jobs")
        val databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")

        val jobList = ArrayList<Job>()
        val petList = ArrayList<Pet>()

        databaseReferencePets.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(petsSnapshot: DataSnapshot) {
                petList.clear()
                binding.loadingCardView.visibility = View.VISIBLE
                binding.scrollView.foreground = ColorDrawable(Color.parseColor("#FFFFFFFF"))
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
                                if (job?.userID == FirebaseAuth.getInstance().currentUser?.uid && (startDate.before(currentDate) || job?.jobStatus==false)) {
                                    binding.animationView2.visibility=View.GONE
                                    job?.let {
                                        jobList.add(it)
                                    }
                                }
                            }
                        }
                        val adapter = PastAdvertsAdapter(requireContext(),jobList, petList)
                        pastAdvertRecycleView.adapter = adapter
                        binding.loadingCardView.visibility = View.GONE
                        binding.scrollView.foreground=null
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