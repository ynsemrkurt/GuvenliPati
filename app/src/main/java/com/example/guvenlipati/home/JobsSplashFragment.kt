package com.example.guvenlipati.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.guvenlipati.job.JobsActivity
import com.example.guvenlipati.job.GetJobActivity
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentJobsSplashBinding
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
            val intent = Intent(requireContext(), JobsActivity::class.java)
            startActivity(intent)
        }

        binding.findJobButton.setOnClickListener {
            if (::user.isInitialized) {
                val userBackerBool = user.userBacker
                if (!userBackerBool) {
                    showMaterialDialog()
                } else {
                    val intent = Intent(requireContext(), GetJobActivity::class.java)
                    startActivity(intent)
                }
            } else {
                showToast("Kullanıcı verileri yüklenemedi.")
            }
        }
    }

    private fun showMaterialDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Bakıcı Ol!")
            .setMessage("Bakıcı profiliniz bulunmamaktadır. İş alabilmek için bakıcı profili oluşturmak zorundasınız!")
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_dialog
                )
            )
            .setPositiveButton("Bakıcı Ol!") { _, _ ->
                (activity as HomeActivity).goPetBackerActivity()
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
