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
import com.example.guvenlipati.OfferAdapter
import com.example.guvenlipati.databinding.FragmentPaymentAdvertBinding
import com.example.guvenlipati.models.Backer
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.Rating
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PaymentAdvertFragment : Fragment() {

    private lateinit var binding: FragmentPaymentAdvertBinding
    private lateinit var adapter: OfferAdapter
    private val offerList = mutableListOf<Offer>()
    private val jobList = mutableListOf<Job>()
    private val petList = mutableListOf<Pet>()
    private val userList = mutableListOf<User>()
    private val backerList = mutableListOf<Backer>()
    private val ratingList = mutableListOf<Double>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentAdvertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadOffers()
    }

    private fun setupRecyclerView() {
        adapter = OfferAdapter(
            requireContext(),
            jobList,
            petList,
            userList,
            offerList,
            backerList,
            ratingList
        )
        binding.paymentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PaymentAdvertFragment.adapter
        }
    }

    private fun loadOffers() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("offers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    clearLists()
                    dataSnapshot.children.forEach { offerSnapshot ->
                        val offer = offerSnapshot.getValue(Offer::class.java)
                        if (offer != null && offer.offerUser == currentUser?.uid && isOfferWithinLast7Days(
                                offer.offerDate
                            ) && !offer.offerStatus && !offer.priceStatus
                        ) {
                            loadingUI()
                            fetchJobAndRelatedData(offer)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun fetchJobAndRelatedData(offer: Offer) {
        FirebaseDatabase.getInstance().getReference("jobs").child(offer.offerJobId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(jobSnapshot: DataSnapshot) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    job?.let {
                        fetchPet(job, offer)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun fetchPet(job: Job, offer: Offer) {
        FirebaseDatabase.getInstance().getReference("pets").child(job.petID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(petSnapshot: DataSnapshot) {
                    val pet = petSnapshot.getValue(Pet::class.java)
                    pet?.let {
                        fetchUser(offer, job, pet)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun fetchUser(offer: Offer, job: Job, pet: Pet) {
        FirebaseDatabase.getInstance().getReference("users").child(offer.offerBackerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        fetchBacker(offer, job, pet, user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun fetchBacker(offer: Offer, job: Job, pet: Pet, user: User) {
        FirebaseDatabase.getInstance().getReference("identifies").child(offer.offerBackerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(backerSnapshot: DataSnapshot) {
                    val backer = backerSnapshot.getValue(Backer::class.java)
                    backer?.let {
                        databaseProcessRating(offer, job, pet, user, backer)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun databaseProcessRating(
        offer: Offer,
        job: Job,
        pet: Pet,
        user: User,
        backer: Backer
    ) {
        FirebaseDatabase.getInstance().getReference("ratings")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ratings = snapshot.children.mapNotNull { it.getValue(Rating::class.java) }
                        .filter { it.backerId == offer.offerBackerId }
                    addLists(ratings, offer, job, pet, user, backer)
                    updateUI()
                }

                override fun onCancelled(error: DatabaseError) {
                    dbError()
                }
            })
    }

    private fun isOfferWithinLast7Days(offerDate: String): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        return dateFormat.parse(offerDate)?.after(calendar.time) ?: false
    }

    private fun clearLists() {
        offerList.clear()
        jobList.clear()
        petList.clear()
        userList.clear()
        backerList.clear()
        ratingList.clear()
    }

    private fun addLists(
        ratings: List<Rating>,
        offer: Offer,
        job: Job,
        pet: Pet,
        user: User,
        backer: Backer
    ) {
        val averageRating = ratings.map { it.rating }.average()
        ratingList.add(if (ratings.isNotEmpty()) averageRating else 0.0)
        offerList.add(offer)
        jobList.add(job)
        petList.add(pet)
        userList.add(user)
        backerList.add(backer)
    }

    private fun updateUI() {
        adapter.notifyDataSetChanged()
        binding.animationView2.visibility = View.GONE
        binding.loadingCardView.visibility = View.GONE
        binding.paymentRecyclerView.foreground = null
    }

    private fun dbError() {
        Toast.makeText(
            requireContext(),
            "Veri Tabanı hatası lütfen daha sonra deneyiz!",
            Toast.LENGTH_SHORT
        ).show()
        requireActivity().finish()
    }

    private fun loadingUI() {
        binding.paymentRecyclerView.foreground = ColorDrawable(Color.parseColor("#FFFFFF"))
        binding.loadingCardView.visibility = View.VISIBLE
    }
}
