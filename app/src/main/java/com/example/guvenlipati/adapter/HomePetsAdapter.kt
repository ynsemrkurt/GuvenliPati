package com.example.guvenlipati.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.chat.ProfileActivity
import com.example.guvenlipati.models.Pet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomePetsAdapter(
    private val context: Context,
    private val selectPetList: ArrayList<Pet>
) :
    RecyclerView.Adapter<HomePetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_pet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePetsAdapter.ViewHolder, position: Int) {
        holder.petCard.animation= AnimationUtils.loadAnimation(context,R.anim.recyclerview_anim)
        val pet = selectPetList[position]
        holder.bind(pet, position)
    }

    override fun getItemCount(): Int = selectPetList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petCard: LinearLayout =view.findViewById(R.id.petCard)
        private val petPhoto: ImageView = view.findViewById(R.id.petImage)
        private val petName: TextView = view.findViewById(R.id.petName)
        private val petType: TextView = view.findViewById(R.id.petType)

        @SuppressLint("SetTextI18n")
        fun bind(pet: Pet, position: Int) {
            petName.text = pet.petName
            petType.text = pet.petBreed

            Glide.with(context)
                .load(Uri.parse(pet.petPhoto))
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhoto)

            itemView.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.TransparentDialog)

                val inflater = LayoutInflater.from(context)
                val view2 = inflater.inflate(R.layout.item_pet_preview, null)
                builder.setView(view2)

                val petPhotoImageView=view2.findViewById<ImageView>(R.id.petPhotoImageView)
                val petNameTextView=view2.findViewById<TextView>(R.id.petNameTextView)
                val textViewAge=view2.findViewById<TextView>(R.id.textViewAge)
                val petGenderTextView=view2.findViewById<TextView>(R.id.petGenderTextView)
                val petVaccinateTextView=view2.findViewById<TextView>(R.id.petVaccinateTextView)
                val petTypeTextView=view2.findViewById<TextView>(R.id.petTypeTextView)
                val petWeightTextView=view2.findViewById<TextView>(R.id.petWeightTextView)
                val petAboutTextView=view2.findViewById<TextView>(R.id.petAboutTextView)
                val infoButton=view2.findViewById<ImageButton>(R.id.infoButton)

                if (pet.petPhoto.isNotEmpty()){
                    Glide.with(context)
                        .load(Uri.parse(pet.petPhoto))
                        .into(petPhotoImageView)
                }
                else{
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
                petTypeTextView.text=pet.petBreed
                petWeightTextView.text=pet.petWeight + " Kg"
                petAboutTextView.text=pet.petAbout

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
}