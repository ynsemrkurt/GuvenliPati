package com.example.guvenlipati.payment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.RetrofitInstance
import com.example.guvenlipati.databinding.FragmentPaymentBinding
import com.example.guvenlipati.home.HomeActivity
import com.example.guvenlipati.models.Notification
import com.example.guvenlipati.models.Offer
import com.example.guvenlipati.models.PushNotification
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private var topic = "/topics/myTopic"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextCardHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString()
                if (currentText.length > 20) {
                    binding.editTextCardHolderName.setText(currentText.substring(0, 20))
                    binding.editTextCardHolderName.setSelection(20)
                } else {
                    binding.cardName.text = currentText
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        binding.editTextCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Önceki metin değişmeden önce yapılacak işlemler (Opsiyonel)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Metin değiştiğinde yapılacak işlemler
                val currentText = s.toString()
                if (currentText.length <= 19) {
                    val formattedText = formatCreditCardNumber(currentText)
                    binding.editTextCardNumber.removeTextChangedListener(this)
                    binding.editTextCardNumber.setText(formattedText)
                    binding.editTextCardNumber.setSelection(formattedText.length)
                    binding.editTextCardNumber.addTextChangedListener(this)
                    binding.creditCardNumber.text = formattedText
                } else {
                    binding.editTextCardNumber.setText(currentText.substring(0, 19))
                    binding.editTextCardNumber.setSelection(19)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            private fun formatCreditCardNumber(text: String): String {
                val trimmed = text.replace("\\s+".toRegex(), "") // Boşlukları kaldır
                val chunked = trimmed.chunked(4) // 4 karakterlik parçalara böl
                return chunked.joinToString(" ") // Her 4 karakter arasına boşluk ekle
            }
        })

        binding.editTextExpDate.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var mm = ""
            private var yy = ""
            private var isDeleting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val clean = it.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")

                    var cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 4) {
                        sel++
                        i += 2
                    }
                    if (clean == cleanC) sel--

                    if (cl <= 2) {
                        mm = clean
                    } else {
                        mm = clean.substring(0, 2)
                        yy = clean.substring(2)
                    }

                    if (mm.length < 2) {
                        mm = mm
                    }
                    if (yy.length > 2) {
                        yy = yy.substring(0, 2)
                    } else if (yy.length < 2 && cl > 2) {
                        yy = yy
                    }

                    current = if (cl <= 2 || isDeleting) {
                        mm
                    } else {
                        "$mm/$yy"
                    }
                    if (it.toString() != current) {
                        binding.editTextExpDate.setText(current)
                        binding.editTextExpDate.setSelection(if (sel < current.length) sel else current.length)
                    }
                }
                binding.cardDate.text = "$mm/$yy"
                if (binding.editTextExpDate.text.isEmpty()) {
                    binding.cardDate.text = "07/30"
                }
            }
        })


        binding.editTextCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.length > 3) {
                        binding.editTextCVV.setText(s.subSequence(0, 3))
                        binding.editTextCVV.setSelection(3)
                    }
                }
            }
        })

        binding.backToSplash.setOnClickListener {
            requireActivity().finish()
        }

        binding.ConfirmPaymentButton.setOnClickListener {

            if (binding.editTextCardHolderName.text.trim()
                    .isEmpty() || binding.editTextCardNumber.text.trim()
                    .isEmpty() || binding.editTextExpDate.text.trim()
                    .isEmpty() || binding.editTextCVV.text.trim().isEmpty()
            ) {
                showToast("Tüm alanları doldurunuz!")
                return@setOnClickListener
            }

            if (binding.editTextCardNumber.text.length != 19) {
                showToast("Kart numarası 19 haneli olmalıdır!")
                return@setOnClickListener
            }

            if (binding.editTextExpDate.text.length != 5) {
                showToast("Son kullanma tarihi formatı hatalı!")
                return@setOnClickListener
            }
            val mm = binding.editTextExpDate.text.substring(0, 2).toIntOrNull() ?: 0
            val yy = binding.editTextExpDate.text.substring(3, 5).toIntOrNull() ?: 0
            val currentYearLastTwoDigits = Calendar.getInstance().get(Calendar.YEAR) % 100

            if (mm !in 1..12) {
                showToast("Geçersiz ay.")
                return@setOnClickListener
            }

            if (yy < currentYearLastTwoDigits) {
                showToast("Geçersiz yıl.")
                return@setOnClickListener
            }

            if (binding.editTextCVV.text.length != 3) {
                showToast("CVV 3 haneli olmalıdır!")
                return@setOnClickListener
            }

            if (!binding.checkBox.isChecked || !binding.checkBox2.isChecked || !binding.checkBox3.isChecked) {
                showToast("Lütfen tüm şartları kabul ediniz!")
                return@setOnClickListener
            }

            val offerId = activity?.intent?.getStringExtra("offerId")
            val jobId = activity?.intent?.getStringExtra("jobId")
            val backerId = activity?.intent?.getStringExtra("backerId")
            val petPhoto = activity?.intent?.getStringExtra("petPhoto")

            if (offerId != null && jobId != null && backerId != null && petPhoto != null) {
                val offerRef = FirebaseDatabase.getInstance().getReference("offers").child(offerId)
                val jobRef = FirebaseDatabase.getInstance().getReference("jobs").child(jobId)

                offerRef.updateChildren(mapOf("priceStatus" to true)).addOnSuccessListener {
                    jobRef.updateChildren(mapOf("jobStatus" to false)).addOnSuccessListener {
                        deleteOtherOffers(jobId)
                        notifyBacker(backerId, petPhoto)
                        showBottomSheet()
                    }.addOnFailureListener {
                        showToast("İş güncellenirken hata oluştu")
                    }
                }.addOnFailureListener {
                    showToast("Teklif güncellenirken hata oluştu")
                }
            } else {
                showToast("Gerekli veriler eksik")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_payment, null)
        view.findViewById<Button>(R.id.backToMain).setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }

    private fun deleteOtherOffers(jobId: String?) {
        jobId?.let { jId ->
            val offersRef = FirebaseDatabase.getInstance().getReference("offers")
            offersRef.orderByChild("offerJobId").equalTo(jId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { offerSnapshot ->
                            val offer = offerSnapshot.getValue(Offer::class.java)
                            if (offer?.priceStatus == false) {
                                offerSnapshot.ref.removeValue()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("PaymentFragment", "Error retrieving offers: ${error.message}")
                    }
                })
        }
    }


    private fun notifyBacker(backerId: String?, petPhoto: String?) {
        topic = "/topics/$backerId"
        val notification = PushNotification(
            Notification(
                "Teklifin Onaylandı \uD83E\uDD73",
                "Hemen gel ve incele...",
                FirebaseAuth.getInstance().currentUser?.uid.toString(),
                petPhoto.toString(),
                2
            ),
            topic
        )
        sendNotification(notification)
    }
}