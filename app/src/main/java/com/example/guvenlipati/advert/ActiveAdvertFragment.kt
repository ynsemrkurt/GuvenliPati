package com.example.guvenlipati.advert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.OfferAdapter
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ActiveAdvertFragment : Fragment() {

    lateinit var binding: com.example.guvenlipati.databinding.FragmentActiveAdvertBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=com.example.guvenlipati.databinding.FragmentActiveAdvertBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val activeAdvertRecycleView = binding.activeAdvertRecycleView
        activeAdvertRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val databaseReferenceOffers = FirebaseDatabase.getInstance().getReference("offers")
        val databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("jobs")
        val databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")
        val databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users")
        val databaseReferenceBackers = FirebaseDatabase.getInstance().getReference("identifies")

        val jobList = ArrayList<Job>()
        val petList = ArrayList<Pet>()
        val userList = ArrayList<User>()
        val offerList = ArrayList<Offer>()
        val backerList = ArrayList<Backer>()

        databaseReferenceOffers.get().addOnSuccessListener { offersSnapshot ->
            for (offerSnapshot in offersSnapshot.children) {
                val offer = offerSnapshot.getValue(Offer::class.java)
                offer?.let {
                    if (offer.offerUser == firebaseUser?.uid && !offer.offerStatus && offer.priceStatus) {
                        offerList.add(it)
                        databaseReferenceJobs.child(offer.offerJobId).get()
                            .addOnSuccessListener { jobSnapshot ->
                                val job = jobSnapshot.getValue(Job::class.java)
                                job?.let {
                                    jobList.add(it)
                                    databaseReferencePets.child(job.petID).get()
                                        .addOnSuccessListener { petSnapshot ->
                                            val pet = petSnapshot.getValue(Pet::class.java)
                                            pet?.let {
                                                petList.add(it)
                                                databaseReferenceUsers.child(offer.offerBackerId)
                                                    .get()
                                                    .addOnSuccessListener { userSnapshot ->
                                                        val user =
                                                            userSnapshot.getValue(User::class.java)
                                                        user?.let {
                                                            userList.add(it)
                                                            databaseReferenceBackers.child(offer.offerBackerId)
                                                                .get()
                                                                .addOnSuccessListener { backerSnapshot ->
                                                                    val backer =
                                                                        backerSnapshot.getValue(
                                                                            Backer::class.java
                                                                        )
                                                                    backer?.let { backerList.add(it) }
                                                                    val adapter = OfferAdapter(
                                                                        requireContext(),
                                                                        jobList,
                                                                        petList,
                                                                        userList,
                                                                        offerList,
                                                                        backerList
                                                                    )
                                                                    activeAdvertRecycleView.adapter =
                                                                        adapter
                                                                }
                                                        }
                                                    }
                                            }
                                        }
                                }
                            }
                    }
                }
            }
        }
    }
}