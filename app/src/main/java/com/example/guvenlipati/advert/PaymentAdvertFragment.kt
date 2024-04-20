package com.example.guvenlipati.advert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.OfferAdapter
import com.example.guvenlipati.databinding.FragmentPaymentAdvertBinding
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PaymentAdvertFragment : Fragment() {

    lateinit var binding: FragmentPaymentAdvertBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentAdvertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val paymentAdvertRecyclerView = binding.paymentRecyclerView
        paymentAdvertRecyclerView.layoutManager =
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
                offer?.let { offerList.add(it) }
                val userId = offer?.offerUser ?: ""
                val offerDate = offer?.offerDate ?: ""
                val offerStatus = offer?.offerStatus ?: true

                if (userId == firebaseUser?.uid && isOfferWithinLast7Days(offerDate) && !offerStatus) {
                    val jobIds = offer?.offerJobId?.split(",") ?: emptyList()
                    jobIds.forEach { jobId ->
                        databaseReferenceJobs.child(jobId).get()
                            .addOnSuccessListener { jobSnapshot ->
                                val job = jobSnapshot.getValue(Job::class.java)
                                job?.let { jobList.add(it) }
                                val petId = job?.petID ?: ""
                                databaseReferencePets.child(petId).get()
                                    .addOnSuccessListener { petSnapshot ->
                                        val pet = petSnapshot.getValue(Pet::class.java)
                                        pet?.let { petList.add(it) }
                                        val userBackerId = offer?.offerBackerId
                                        databaseReferenceUsers.child(userBackerId!!).get()
                                            .addOnSuccessListener { userSnapshot ->
                                                val user = userSnapshot.getValue(User::class.java)
                                                user?.let { userList.add(it) }
                                                val backerId = offer.offerBackerId
                                                databaseReferenceBackers.child(backerId).get()
                                                    .addOnSuccessListener { backerSnapshot ->
                                                        val backer =
                                                            backerSnapshot.getValue(Backer::class.java)
                                                        backer?.let { backerList.add(it) }
                                                        val adapter = OfferAdapter(
                                                            requireContext(),
                                                            jobList,
                                                            petList,
                                                            userList,
                                                            offerList,
                                                            backerList
                                                        )
                                                        paymentAdvertRecyclerView.adapter = adapter
                                                    }
                                            }
                                    }
                            }
                    }
                }
            }
        }
    }

    private fun isOfferWithinLast7Days(offerDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val offerDateTime = dateFormat.parse(offerDate)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val last7Days = calendar.time

        return offerDateTime.after(last7Days)
    }
}