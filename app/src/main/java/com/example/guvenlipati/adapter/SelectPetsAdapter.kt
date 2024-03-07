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

class SelectPetsAdapter(
    private val context: Context,
    private val selectPetList: ArrayList<Pet>,
    private val onItemClick: (String) -> Unit
) :
    RecyclerView.Adapter<SelectPetsAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_pet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectPetsAdapter.ViewHolder, position: Int) {
        val pet = selectPetList[position]
        holder.bind(pet, position)
    }

    override fun getItemCount(): Int = selectPetList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petPhoto: ImageView = view.findViewById(R.id.petImage)
        private val petName: TextView = view.findViewById(R.id.petName)
        private val petType: TextView = view.findViewById(R.id.petType)
        private val correctImage = view.findViewById<ImageView>(R.id.correctImage)

        fun bind(pet: Pet, position: Int) {
            petName.text = pet.petName
            petType.text = pet.petBreed

            Glide.with(context)
                .load(Uri.parse(pet.petPhoto))
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhoto)

            itemView.setOnClickListener {
                onItemClick.invoke(pet.petId)
                val previousSelectedPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
            }

            // Görsel durumu güncelle
            if (position == selectedPosition) {
                correctImage.visibility = View.VISIBLE
            } else {
                correctImage.visibility = View.INVISIBLE
            }
        }
    }
}