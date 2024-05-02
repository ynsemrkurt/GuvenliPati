package com.example.guvenlipati.backer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentBackerPreferenceBinding
import com.example.guvenlipati.home.HomeActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BackerPreferenceFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentBackerPreferenceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBackerPreferenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dogJob = false
        var catJob = false
        var birdJob = false
        var userAvailability: Int? = null
        var homeJob = false
        var feedingJob = false
        var walkingJob = false
        val dogs = binding.dogs
        val cats = binding.cats
        val birds = binding.birds
        val midWeek = binding.midWeek
        val weekEnd = binding.weekEnd
        val allDays = binding.allDays
        val home = binding.job1
        val feeding = binding.job2
        val walking = binding.job3
        val homeMoney = binding.editTextBoarding
        val feedingMoney = binding.editTextBoarding2
        val walkingMoney = binding.editTextBoarding3
        val saveButton = binding.JobOptionButton

        dogs.setOnCheckedChangeListener { _, isChecked ->
            dogJob = isChecked
        }

        cats.setOnCheckedChangeListener { _, isChecked ->
            catJob = isChecked
        }

        birds.setOnCheckedChangeListener { _, isChecked ->
            birdJob = isChecked
        }

        midWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userAvailability = 1
            }
        }

        weekEnd.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userAvailability = 2
            }
        }

        allDays.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userAvailability = 3
            }
        }

        home.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                homeJob = true
                homeMoney.isEnabled = true
                homeMoney.text.clear()
            } else {
                homeJob = false
                homeMoney.isEnabled = false
                homeMoney.setText("0")
            }
        }

        feeding.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                feedingJob = true
                feedingMoney.isEnabled = true
                feedingMoney.text.clear()
            } else {
                feedingJob = false
                feedingMoney.isEnabled = false
                feedingMoney.setText("0")
            }
        }

        walking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                walkingJob = true
                walkingMoney.isEnabled = true
                walkingMoney.text.clear()
            } else {
                walkingJob = false
                walkingMoney.isEnabled = false
                walkingMoney.setText("0")
            }
        }

        saveButton.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            databaseReference = FirebaseDatabase.getInstance().getReference("identifies")

            if (!dogs.isChecked && !cats.isChecked && !birds.isChecked) {
                showToast("Lütfen En Az Bir Hayvan Seçiniz!")
                return@setOnClickListener
            }
            if (!midWeek.isChecked && !weekEnd.isChecked && !allDays.isChecked) {
                showToast("Lütfen Bir Müsaitlik Durumu Seçiniz!")
                return@setOnClickListener
            }

            if (!home.isChecked && !feeding.isChecked && !walking.isChecked) {
                showToast("Lütfen Bir İş Seçiniz!")
                return@setOnClickListener
            }

            if (home.isChecked) {
                if (homeMoney.text.toString().trim().isEmpty() || homeMoney.text.toString().toInt() <= 0) {
                    showToast("Lütfen Bir Tutar Giriniz!")
                    return@setOnClickListener
                }
            }

            if (feeding.isChecked) {
                if (feedingMoney.text.toString().trim().isEmpty() || feedingMoney.text.toString()
                        .toInt() <= 0
                ) {
                    showToast("Lütfen Bir Tutar Giriniz!")
                    return@setOnClickListener
                }
            }

            if (walking.isChecked) {
                if (walkingMoney.text.toString().trim().isEmpty() || walkingMoney.text.toString()
                        .toInt() <= 0
                ) {
                    showToast("Lütfen Bir Tutar Giriniz!")
                    return@setOnClickListener

                }
            }

            databaseReference = FirebaseDatabase.getInstance().getReference("identifies")
                .child(auth.currentUser!!.uid)

            databaseReference.updateChildren(
                mapOf(
                    "dogBacker" to dogJob,
                    "catBacker" to catJob,
                    "birdBacker" to birdJob,
                    "userAvailability" to userAvailability,
                    "homeJob" to homeJob,
                    "feedingJob" to feedingJob,
                    "walkingJob" to walkingJob,
                    "homeMoney" to homeMoney.text.toString().toInt(),
                    "feedingMoney" to feedingMoney.text.toString().toInt(),
                    "walkingMoney" to walkingMoney.text.toString().toInt()
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Başarılı işlemleri
                } else {
                    showToast("Kayıt hatası: ${task.exception}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
