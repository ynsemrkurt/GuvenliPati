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
import com.example.guvenlipati.ActiveOfferAdapter
import com.example.guvenlipati.databinding.FragmentActiveAdvertBinding
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

class ActiveAdvertFragment : Fragment() {

    private lateinit var binding: FragmentActiveAdvertBinding
    private lateinit var adapter: ActiveOfferAdapter
    private val jobList = mutableListOf<Job>()
    private val petList = mutableListOf<Pet>()
    private val userList = mutableListOf<User>()
    private val offerList = mutableListOf<Offer>()
    private val backerList = mutableListOf<Backer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveAdvertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadOffers()
    }

    private fun setupRecyclerView() {
        adapter =
            ActiveOfferAdapter(requireContext(), jobList, petList, userList, offerList, backerList)
        binding.activeAdvertRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.activeAdvertRecycleView.adapter = adapter
    }

    private fun loadOffers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference("offers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    clearLists()
                    dataSnapshot.children.forEach { offerSnapshot ->
                        val offer = offerSnapshot.getValue(Offer::class.java)
                        if (offer != null && offer.offerUser == firebaseUser?.uid && !offer.offerStatus && offer.priceStatus) {
                            binding.animationView2.visibility = View.GONE
                            binding.loadingCardView.visibility = View.VISIBLE
                            binding.linearLayout.foreground =
                                ColorDrawable(Color.parseColor("#FFFFFFFF"))
                            fetchJobAndRelatedData(offer)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Teklifler alınırken hata oluştu lütfen daha sonra tekrar deneyin!")
                    requireActivity().finish()
                }
            })
    }

    private fun fetchJobAndRelatedData(offer: Offer) {
        fetchDatabaseData("jobs", offer.offerJobId, Job::class.java) { job ->
            fetchDatabaseData("pets", job.petID, Pet::class.java) { pet ->
                fetchDatabaseData("users", offer.offerBackerId, User::class.java) { user ->
                    fetchDatabaseData(
                        "identifies",
                        offer.offerBackerId,
                        Backer::class.java
                    ) { backer ->
                        addLists(offer, user, pet, backer, job)
                        updateUI()
                    }
                }
            }
        }
    }

    private fun <T> fetchDatabaseData(
        reference: String,
        childId: String,
        modelClass: Class<T>,
        onSuccess: (T) -> Unit
    ) {
        FirebaseDatabase.getInstance().getReference(reference).child(childId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(modelClass)?.let { onSuccess(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Teklif bilgileri alınırken hata oluştu lütfen daha sonra tekrar deneyin!")
                    requireActivity().finish()
                }
            })
    }

    private fun clearLists() {
        jobList.clear()
        petList.clear()
        userList.clear()
        offerList.clear()
        backerList.clear()
    }

    private fun addLists(offer: Offer, user: User, pet: Pet, backer: Backer, job: Job) {
        offerList.add(offer)
        userList.add(user)
        petList.add(pet)
        backerList.add(backer)
        jobList.add(job)
    }

    private fun updateUI() {
        adapter.notifyDataSetChanged()
        binding.linearLayout.foreground = null
        binding.loadingCardView.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}