package com.example.guvenlipati

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.chat.ChatActivity
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.models.Job
import com.example.guvenlipati.models.Notification
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.PushNotification
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ActiveJobAdapter(
    private val context: Context,
    private val jobList: List<Job>,
    private val petList: List<Pet>,
    private val userList: List<User>,
    private val offerList: MutableList<Offer>
) : RecyclerView.Adapter<ActiveJobAdapter.ViewHolder>() {

    private var topic = "/topics/myTopic"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_active_job, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveJobAdapter.ViewHolder, position: Int) {
        if (position < jobList.size && position < petList.size && position < userList.size && position < offerList.size) {
            holder.bind(
                jobList[position],
                petList[position],
                userList[position],
                offerList[position]
            )
        }
    }

    override fun getItemCount(): Int = offerList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petPhotoImageView = view.findViewById<ImageView>(R.id.petPhotoImageView)
        private val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        private val jobTypeTextView = view.findViewById<TextView>(R.id.jobTypeTextView)
        private val petTypeTextView = view.findViewById<TextView>(R.id.petTypeTextView)
        private val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
        private val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
        private val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        private val backerPhotoImageView =
            view.findViewById<ImageView>(R.id.backerPhotoImageView)
        private val backerNameTextView = view.findViewById<TextView>(R.id.backerNameTextView)
        private val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
        private val buttonGoChat = view.findViewById<ImageButton>(R.id.buttonGoChat)
        private val confirmButton = view.findViewById<Button>(R.id.confirmButton)
        private val confirmStatusTextView = view.findViewById<TextView>(R.id.confirmStatusTextView)
        private val buttonCopyId = view.findViewById<ImageButton>(R.id.buttonCopyId)
        private val supportButton = view.findViewById<Button>(R.id.supportButton)

        @SuppressLint("SetTextI18n")
        fun bind(job: Job, pet: Pet, user: User, offer: Offer) {
            when (job.jobType) {
                "feedingJob" -> jobTypeTextView.text = "Besleme"
                "walkingJob" -> jobTypeTextView.text = "Gezdirme"
                "homeJob" -> jobTypeTextView.text = "Evde Bakım"
            }
            petNameTextView.text = pet.petName
            petTypeTextView.text = pet.petBreed
            startDateTextView.text = job.jobStartDate
            endDateTextView.text = job.jobEndDate
            locationTextView.text = job.jobProvince + ", " + job.jobTown
            backerNameTextView.text = user.userName + " " + user.userSurname
            priceTextView.text = offer.offerPrice.toString() + " TL"

            if (offer.confirmBacker == false && offer.confirmUser == true) {
                confirmStatusTextView.text = "İş Sahibi\nOnayladı..."
            } else if (offer.confirmBacker == true && offer.confirmUser == false) {
                confirmStatusTextView.text = "Sen\nOnayladın..."
            } else {
                confirmStatusTextView.text = "Henüz\nOnaylanmadı..."
            }

            Glide.with(context)
                .load(pet.petPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhotoImageView)

            Glide.with(context)
                .load(user.userPhoto)
                .placeholder(R.drawable.default_pet_image_2)
                .into(backerPhotoImageView)

            buttonGoChat.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("userId", offer.offerUser)
                context.startActivity(intent)
            }

            confirmButton.setOnClickListener {
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("offers").child(offer.offerId)
                if (offer.confirmUser == true) {
                    databaseReference.updateChildren(
                        mapOf(
                            "confirmBacker" to true,
                            "offerStatus" to true
                        )
                    )
                } else {
                    databaseReference.updateChildren(
                        mapOf(
                            "confirmBacker" to true
                        )
                    )
                    topic = "/topics/${user.userId}"
                    PushNotification(
                        Notification(
                            "Bakıcı İşi Onaylandı \uD83E\uDD73",
                            "Hemen gel ve sende onayla...",
                            FirebaseAuth.getInstance().currentUser?.uid.toString(),
                            pet.petPhoto,
                            4
                        ),
                        topic
                    ).also {
                        sendNotification(it)
                    }
                }
            }

            supportButton.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", "yunusemre-kurt@outlook.com", null)
                )
                context.startActivity(Intent.createChooser(intent, "Send email..."))
            }

            buttonCopyId.setOnClickListener {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("simple text", offer.offerId)
                clipboard.setPrimaryClip(clip)
                showToast("Teklif ID kopyalandı...")
            }

            backerPhotoImageView.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("userId", pet.userId)
                context.startActivity(intent)
            }

            petPhotoImageView.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

                val inflater = LayoutInflater.from(context)
                val view2 = inflater.inflate(R.layout.item_pet_preview, null)
                builder.setView(view2)

                val petPhotoImageView = view2.findViewById<ImageView>(R.id.petPhotoImageView)
                val petNameTextView = view2.findViewById<TextView>(R.id.petNameTextView)
                val textViewAge = view2.findViewById<TextView>(R.id.textViewAge)
                val petGenderTextView = view2.findViewById<TextView>(R.id.petGenderTextView)
                val petVaccinateTextView = view2.findViewById<TextView>(R.id.petVaccinateTextView)
                val petTypeTextView = view2.findViewById<TextView>(R.id.petTypeTextView)
                val petWeightTextView = view2.findViewById<TextView>(R.id.petWeightTextView)
                val petAboutTextView = view2.findViewById<TextView>(R.id.petAboutTextView)
                val infoButton = view2.findViewById<ImageButton>(R.id.infoButton)

                if (pet.petPhoto.isNotEmpty()) {
                    Glide.with(context)
                        .load(Uri.parse(pet.petPhoto))
                        .into(petPhotoImageView)
                } else {
                    petPhotoImageView.setImageResource(R.drawable.default_pet_image_2)
                }


                val petYearInt = pet.petBirthYear.toInt()
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy")
                val currentYearInt = currentDateTime.format(formatter).toInt()
                val petAge = currentYearInt - petYearInt

                petNameTextView.text=pet.petName
                textViewAge.text= "$petAge Yaş"

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
                petAboutTextView.text = pet.petAbout

                infoButton.setOnClickListener {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("userId", pet.userId)
                    context.startActivity(intent)
                }

                val dialog = builder.create()
                dialog.show()
            }

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }
}