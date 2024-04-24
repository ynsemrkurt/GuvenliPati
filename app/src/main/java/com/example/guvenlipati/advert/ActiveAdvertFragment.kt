package com.example.guvenlipati.advert

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.ActiveOfferAdapter
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.graphics.Color

class ActiveAdvertFragment : Fragment() {

    lateinit var binding: com.example.guvenlipati.databinding.FragmentActiveAdvertBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            com.example.guvenlipati.databinding.FragmentActiveAdvertBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val activeAdvertRecycleView = binding.activeAdvertRecycleView
        activeAdvertRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val jobList = mutableListOf<Job>()
        val petList = mutableListOf<Pet>()
        val userList = mutableListOf<User>()
        val offerList = mutableListOf<Offer>()
        val backerList = mutableListOf<Backer>()

        val adapter =
            ActiveOfferAdapter(requireContext(), jobList, petList, userList, offerList, backerList)
        activeAdvertRecycleView.adapter = adapter

        FirebaseDatabase.getInstance().getReference("offers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    offerList.clear()
                    jobList.clear()
                    petList.clear()
                    userList.clear()
                    backerList.clear()
                    dataSnapshot.children.forEach { offerSnapshot ->
                        val offer = offerSnapshot.getValue(Offer::class.java) ?: return@forEach
                        if (offer.offerUser == firebaseUser?.uid && !offer.offerStatus && offer.priceStatus) {
                            offerList.add(offer)
                            binding.animationView2.visibility=View.GONE
                            binding.loadingCardView.visibility = View.VISIBLE
                            binding.linearLayout.foreground = ColorDrawable(Color.parseColor("#FFFFFFFF"))
                            databaseProcess("jobs", offer.offerJobId, Job::class.java) { job ->
                                jobList.add(job)
                                databaseProcess("pets", job.petID, Pet::class.java) { pet ->
                                    petList.add(pet)
                                    databaseProcess(
                                        "users",
                                        offer.offerBackerId,
                                        User::class.java
                                    ) { user ->
                                        userList.add(user)
                                        databaseProcess(
                                            "identifies",
                                            offer.offerBackerId,
                                            Backer::class.java
                                        ) { backer ->
                                            backerList.add(backer)
                                            adapter.notifyDataSetChanged()
                                            binding.loadingCardView.visibility = View.GONE
                                            binding.linearLayout.foreground = null
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun <T> databaseProcess(
        reference: String,
        child: String,
        claSs: Class<T>,
        onSuccess: (T) -> Unit
    ) {
        FirebaseDatabase.getInstance().getReference(reference).child(child)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(claSs)?.let { data ->
                        onSuccess(data)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("databaseProcess", "Database error: ${error.toException()}")
                }
            })
    }

}