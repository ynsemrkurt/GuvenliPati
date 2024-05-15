package com.example.guvenlipati.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.backer.PetBackerActivity
import com.example.guvenlipati.databinding.FragmentJobsSplashBinding
import com.example.guvenlipati.job.GetJobActivity
import com.example.guvenlipati.job.JobsActivity
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
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var binding: FragmentJobsSplashBinding
    private var petBool = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        fetchUserData()

        binding.createAdvertsButton.setOnClickListener {
            checkUserPetStatus()
        }

        binding.findJobButton.setOnClickListener {
            checkUserBackerStatus()
        }
    }

    private fun fetchUserData() {
        val databaseReferenceUsers =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        databaseReferenceUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User::class.java) ?: User()
                }
                hideLoadingView()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Veritabanı hatası: ${error.message}")
            }
        })
    }

    private fun hideLoadingView() {
        binding.loadingCardView.visibility = View.GONE
        binding.linearLayout.foreground = null
    }

    private fun checkUserPetStatus() {
        val petQuery =
            FirebaseDatabase.getInstance().getReference("pets").orderByChild("userId")
                .equalTo(user.userId)
        petQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                petBool = dataSnapshot.exists()
                if (petBool) {
                    goToJobsActivity()
                } else {
                    showAddPetDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled", error.toException())
            }
        })
    }

    private fun goToJobsActivity() {
        (activity as HomeActivity).goActivity(JobsActivity())
    }

    private fun showAddPetDialog() {
        showMaterialDialog(
            "Dost Ekle!",
            "Henüz dostunuz bulunmamaktadır. İlan oluşturmak için dost profili oluşturmak zorundasınız!",
            "Dost Ekle!",
            {
                (activity as HomeActivity).goSelectAddPetFragment()
            },
            {
                showToast("İptal Edildi")
            }
        )
    }

    private fun checkUserBackerStatus() {
        if (!user.userBacker) {
            showMaterialDialog(
                "Bakıcı Ol!",
                "Bakıcı profiliniz bulunmamaktadır. İş alabilmek için bakıcı profili oluşturmak zorundasınız!",
                "Bakıcı Ol!",
                {
                    (activity as HomeActivity).goActivity(PetBackerActivity())
                },
                {
                    showToast("İptal Edildi")
                }
            )
        } else {
            (activity as HomeActivity).goActivity(GetJobActivity())
        }
    }

    private fun showMaterialDialog(
        title: String,
        message: String,
        positiveButton: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.background_dialog
                )
            )
            .setPositiveButton(positiveButton) { _, _ ->
                positiveAction()
            }
            .setNegativeButton("İptal") { _, _ ->
                negativeAction()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
