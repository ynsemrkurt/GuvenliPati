package com.example.guvenlipati.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Pet

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
        val pet = selectPetList[position]
        holder.bind(pet, position)
    }

    override fun getItemCount(): Int = selectPetList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petPhoto: ImageView = view.findViewById(R.id.petImage)
        private val petName: TextView = view.findViewById(R.id.petName)
        private val petType: TextView = view.findViewById(R.id.petType)
        private val petAge: TextView = view.findViewById(R.id.petAge)

        fun bind(pet: Pet, position: Int) {
            petName.text = pet.petName
            petType.text = pet.petBreed
            petAge.text=pet.petAge+" Yaş"

            Glide.with(context)
                .load(Uri.parse(pet.petPhoto))
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhoto)
        }
    }
}