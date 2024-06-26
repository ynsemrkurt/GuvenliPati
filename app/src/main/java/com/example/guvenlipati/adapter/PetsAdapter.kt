package com.example.guvenlipati.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.editPet.EditPetActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.models.Pet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

private lateinit var firebaseUser: FirebaseUser

class PetsAdapter(private val context: Context, private val petList: ArrayList<Pet>) :
    RecyclerView.Adapter<PetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = petList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.petCard.animation= AnimationUtils.loadAnimation(context,R.anim.recyclerview_anim)
        val pet = petList[position]
        holder.bind(pet)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petCard: LinearLayout = view.findViewById(R.id.petCard)
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
            .setMessage("Dostunuzu silerseniz bu işlemi geri alamazsınız.")
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
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("jobs")
        val query = databaseReferenceJobs.orderByChild("petID").equalTo(pet.petId)

        val databaseReference = FirebaseDatabase.getInstance().getReference("pets").child(pet.petId)
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pet.petPhoto)
        databaseReference.removeValue()
            .addOnSuccessListener {
                storageReference.delete()
                    .addOnSuccessListener {
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (jobSnapshot in dataSnapshot.children) {
                                        jobSnapshot.ref.removeValue()
                                            .addOnSuccessListener {
                                            }
                                            .addOnFailureListener { exception ->
                                                showToast("İş silme işlemi başarısız: ${exception.message}")
                                            }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                showToast("İşler bulunurken bir hata oluştu: ${databaseError.message}")
                            }
                        })
                    }
                    .addOnFailureListener {
                        showToast("Dost fotoğrafı silme işlemi başarısız.")
                    }
            }
            .addOnFailureListener {
                showToast("Dost silme işlemi başarısız.")
            }

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}