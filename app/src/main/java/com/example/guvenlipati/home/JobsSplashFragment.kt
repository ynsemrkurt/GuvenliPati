package com.example.guvenlipati.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.DialogTitle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.guvenlipati.job.JobsActivity
import com.example.guvenlipati.job.GetJobActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentJobsSplashBinding
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JobsSplashFragment : Fragment() {

    private lateinit var user: User
    private lateinit var pet: Pet
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var binding: FragmentJobsSplashBinding
    private var petBool = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentJobsSplashBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        databaseReferenceUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(User::class.java)
                    user = userData ?: User()
                }
                binding.loadingCardView.visibility = View.GONE
                binding.linearLayout.foreground = null
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Veritabanı hatası: ${error.message}")
            }
        })

        binding.createAdvertsButton.setOnClickListener {
            val petQuery =
                FirebaseDatabase.getInstance().getReference("pets").orderByChild("userId")
                    .equalTo(user.userId)
            petQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val pet = dataSnapshot.children.firstOrNull()?.getValue(Pet::class.java)
                        petBool = pet?.userId == user.userId
                        if (petBool) {
                            (activity as HomeActivity).goJobCreateActivity()
                        } else {
                            showMaterialDialog("Dost Ekle!", "Henüz dostunuz bulunmamaktadır. İlan  oluşturmak için dost profili oluşturmak zorundasınız!", "Dost Ekle!", { (activity as HomeActivity).goSelectAddPetFragment() })
                        }
                    } else {
                        showMaterialDialog("Dost Ekle!", "Henüz dostunuz bulunmamaktadır. İlan  oluşturmak için dost profili oluşturmak zorundasınız!", "Dost Ekle!", { (activity as HomeActivity).goSelectAddPetFragment() })

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled", error.toException())
                }
            })
        }

        binding.findJobButton.setOnClickListener {
            if (::user.isInitialized) {
                val userBackerBool = user.userBacker
                if (!userBackerBool) {
                    showMaterialDialog("Bakıcı Ol!", "Bakıcı profiliniz bulunmamaktadır. İş alabilmek için bakıcı profili oluşturmak zorundasınız!", "Bakıcı Ol!", { (activity as HomeActivity).goPetBackerActivity()})
                } else {
                    val intent = Intent(requireContext(), GetJobActivity::class.java)
                    startActivity(intent)
                }
            } else {
                showToast("Kullanıcı verileri yüklenemedi.")
            }
        }
    }

    private fun showMaterialDialog(title: String, message: String, positiveButtonText: String, goFunction: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_dialog
                )
            )
            .setPositiveButton(positiveButtonText) { _, _ ->
                goFunction()
            }
            .setNegativeButton("İptal") { _, _ ->
                showToast("İptal Edildi")
            }
            .show()

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
