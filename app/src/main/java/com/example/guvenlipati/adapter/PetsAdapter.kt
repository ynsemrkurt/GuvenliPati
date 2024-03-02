package com.example.guvenlipati.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.EditPetActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Pet

class PetsAdapter(private val context: Context, private val petList: ArrayList<Pet>) :
    RecyclerView.Adapter<PetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = petList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pet = petList[position]
        holder.bind(pet)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petPhoto: ImageView = view.findViewById(R.id.petImage)
        private val petName: TextView = view.findViewById(R.id.petName)
        private val petType: TextView = view.findViewById(R.id.petType)
        private val changePet: ImageButton = view.findViewById(R.id.buttonEditPet)

        fun bind(pet: Pet) {
            petName.text = pet.petName
            petType.text = pet.petBreed

            Glide.with(context)
                .load(Uri.parse(pet.petPhoto))
                .placeholder(R.drawable.index_cat)
                .into(petPhoto)

            changePet.setOnClickListener {
                val intent = Intent(context, EditPetActivity::class.java)
                intent.putExtra("petId", pet.petId)
                context.startActivity(intent)
            }
        }
    }
}