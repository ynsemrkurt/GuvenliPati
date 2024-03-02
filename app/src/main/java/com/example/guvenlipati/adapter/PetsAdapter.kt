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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.EditPetActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Pet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase

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
        private val deletePet: ImageButton = view.findViewById(R.id.buttonDeletePet)


        fun bind(pet: Pet) {
            petName.text = pet.petName
            petType.text = pet.petBreed

            Glide.with(context)
                .load(Uri.parse(pet.petPhoto))
                .placeholder(R.drawable.default_pet_image_2)
                .into(petPhoto)

            changePet.setOnClickListener {
                val intent = Intent(context, EditPetActivity::class.java)
                intent.putExtra("petId", pet.petId)
                context.startActivity(intent)
            }

            deletePet.setOnClickListener {
                showDeleteConfirmationDialog(pet)
            }
        }
    }
    private fun showDeleteConfirmationDialog(pet: Pet) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Emin Misiniz?")
            .setMessage("Dostunuzu silersen bu işlemi geri alamazsınız.")
            .setBackground(ContextCompat.getDrawable(context, R.drawable.background_dialog))
            .setPositiveButton("Sil") { _, _ ->
                deletePet(pet)
            }
            .setNegativeButton("İptal") { _, _ ->
                showToast("Silme işlemi iptal edildi.")
            }
            .show()
    }

    private fun deletePet(pet: Pet) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("pets").child(pet.petId)
        databaseReference.removeValue()
            .addOnSuccessListener {
                showToast("Dost başarıyla silindi.")
            }
            .addOnFailureListener {
                showToast("Dost silme işlemi başarısız.")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}