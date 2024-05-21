package com.example.guvenlipati.job

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.RetrofitInstance
import com.example.guvenlipati.chat.ChatActivity
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.databinding.FragmentJobDetailsBinding
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Notification
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.PushNotification
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class JobDetailsFragment : Fragment() {

    private var jobId: String? = null
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var identifies: DatabaseReference
    private lateinit var linearLayout: LinearLayout
    private lateinit var loadingCardView: CardView
    private var job: Job? = null
    private lateinit var binding: FragmentJobDetailsBinding
    private var topic = "/topics/myTopic"
    private var pet: Pet = Pet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val petPhotoImageView = binding.petPhotoImageView
        val petNameTextView = binding.petNameTextView
        val petGenderTextView = binding.petGenderTextView
        val petVaccinateTextView = binding.petVaccinateTextView
        val petTypeTextView = binding.petTypeTextView
        val petWeightTextView = binding.petWeightTextView
        val circleImageProfilePhoto = binding.circleImageProfilePhoto
        val userNameTextView = binding.userNameTextView
        val locationTextView = binding.locationTextView
        val startDateTextView = binding.startDateTextView
        val endDateTextView = binding.endDateTextView
        val jobAboutTextView = binding.jobAboutTextView
        val petAboutTextView = binding.petAboutTextView
        val textViewAge = binding.textViewAge
        linearLayout = binding.linearLayout
        loadingCardView = binding.loadingCardView

        arguments?.let {
            jobId = it.getString("jobId")
        }

        fetchOffersAndUpdateStatistics()

        val jobRef = FirebaseDatabase.getInstance().reference.child("jobs").child(jobId!!)
        identifies =
            FirebaseDatabase.getInstance().reference.child("identifies").child(firebaseUser.uid)

        jobRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                job = snapshot.getValue(Job::class.java)!!
                val userRef =
                    FirebaseDatabase.getInstance().reference.child("users").child(job!!.userID)

                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)!!
                        val petRef =
                            FirebaseDatabase.getInstance().reference.child("pets")
                                .child(job!!.petID)

                        petRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                pet = snapshot.getValue(Pet::class.java)!!

                                if (pet.petPhoto.isNotEmpty()) {
                                    Glide.with(requireContext()).load(pet.petPhoto)
                                        .into(petPhotoImageView)
                                }
                                petNameTextView.text = pet.petName
                                when (pet.petGender) {
                                    true -> {
                                        petGenderTextView.text = "Dişi"
                                    }

                                    false -> {
                                        petGenderTextView.text = "Erkek"
                                    }
                                }
                                when (pet.petVaccinate) {
                                    true -> {
                                        petVaccinateTextView.text = "Yapılıyor"
                                    }

                                    false -> {
                                        petVaccinateTextView.text = "Aşısız"
                                    }
                                }
                                petTypeTextView.text = pet.petBreed
                                petWeightTextView.text = pet.petWeight + " Kg"
                                if (user.userPhoto.isNotEmpty()) {
                                    Glide.with(requireContext()).load(user.userPhoto)
                                        .into(circleImageProfilePhoto)
                                }
                                val petYearInt = pet.petBirthYear.toInt()
                                val currentDateTime = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ofPattern("yyyy")
                                val currentYearInt = currentDateTime.format(formatter).toInt()
                                val petAge = currentYearInt - petYearInt

                                userNameTextView.text = user.userName
                                locationTextView.text = job!!.jobProvince + ", " + job!!.jobTown
                                startDateTextView.text = job!!.jobStartDate
                                endDateTextView.text = job!!.jobEndDate
                                jobAboutTextView.text = job!!.jobAbout
                                petAboutTextView.text = pet.petAbout
                                textViewAge.text = "$petAge Yaş"
                                linearLayout.foreground = null
                                loadingCardView.visibility = View.GONE
                            }

                            override fun onCancelled(error: DatabaseError) {
                                showToast("Error: ${error.message}")
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Error: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })

        binding.buttonGoChat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("userId", job?.userID)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.circleImageProfilePhoto.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("userId", job?.userID)
            startActivity(intent)
        }

        binding.buttonOffer.setOnClickListener {
            val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

            val inflater = LayoutInflater.from(context)
            val view2 = inflater.inflate(R.layout.item_job_offer, null)
            builder.setView(view2)

            val offerPrice = view2.findViewById<EditText>(R.id.offerPrice)
            val buttonSend = view2.findViewById<Button>(R.id.buttonSend)

            val dialog = builder.create()
            dialog.show()

            buttonSend.setOnClickListener {
                val price = offerPrice.text.toString().trim()
                if (price.isEmpty() || price.toInt() <= 0) {
                    showToast("Lütfen geçerli bir tutar giriniz!")
                    return@setOnClickListener
                }

                val offersRef = FirebaseDatabase.getInstance().reference.child("offers")
                offersRef.orderByChild("offerJobId").equalTo(jobId!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var offerExists = false
                            var offerId = ""
                            for (offerSnapshot in snapshot.children) {
                                val offerBackerId = offerSnapshot.child("offerBackerId")
                                    .getValue(String::class.java)
                                if (offerBackerId == firebaseUser.uid) {
                                    offerExists = true
                                    offerId = offerSnapshot.key ?: UUID.randomUUID().toString()
                                    break
                                }
                            }

                            val offerData = mapOf(
                                "offerPrice" to price.toInt()
                            )

                            if (offerExists) {
                                offersRef.child(offerId).updateChildren(offerData)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            showToast("Teklif güncellendi!")
                                            dialog.dismiss()
                                        } else {
                                            showToast("Teklif güncellenemedi. Tekrar deneyiniz!")
                                        }
                                    }
                            } else {
                                offerId = UUID.randomUUID().toString()
                                val newOfferData = mapOf(
                                    "offerId" to offerId,
                                    "offerJobId" to jobId!!,
                                    "offerUser" to job!!.userID,
                                    "offerPrice" to price.toInt(),
                                    "offerBackerId" to firebaseUser.uid,
                                    "offerDate" to SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis()),
                                    "offerStatus" to false,
                                    "priceStatus" to false,
                                    "confirmUser" to false,
                                    "confirmBacker" to false,
                                    "ratingStatus" to false
                                )
                                offersRef.child(offerId).setValue(newOfferData)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            showToast("Teklif gönderildi!")
                                            topic = "/topics/${job!!.userID}"
                                            PushNotification(
                                                Notification(
                                                    "Yeni Teklifin Var \uD83E\uDD73",
                                                    "Hemen gel ve incele...",
                                                    firebaseUser.uid,
                                                    pet.petPhoto,
                                                    1
                                                ),
                                                topic
                                            ).also {
                                                sendNotification(it)
                                            }
                                            dialog.dismiss()
                                        } else {
                                            showToast("Teklif gönderilemedi. Tekrar deneyiniz!")
                                        }
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            showToast("Error: ${error.message}")
                        }
                    })

                dialog.setOnCancelListener {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun fetchOffersAndUpdateStatistics() {
        val offersRef = FirebaseDatabase.getInstance().reference.child("offers")
        offersRef.orderByChild("offerJobId").equalTo(jobId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val offers = mutableListOf<Int>()
                    for (offerSnapshot in snapshot.children) {
                        val offerPrice = offerSnapshot.child("offerPrice").getValue(Int::class.java)
                        offerPrice?.let { offers.add(it) }
                    }

                    if (offers.isNotEmpty()) {
                        val minOffer = offers.minOrNull() ?: 0
                        val maxOffer = offers.maxOrNull() ?: 0
                        val avgOffer = if (offers.isNotEmpty()) offers.average().toInt() else 0

                        val statisticsText =
                            "Min Teklif: $minOffer  Ort Teklif: $avgOffer  Max Teklif: $maxOffer"
                        binding.istotistikKahvesi.text = statisticsText
                        binding.istotistikKahvesi.visibility = View.VISIBLE
                    } else {
                        binding.istotistikKahvesi.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Error fetching offers: ${error.message}")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }
}
