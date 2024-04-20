package com.example.guvenlipati.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.guvenlipati.databinding.FragmentPaymentBinding
import com.example.guvenlipati.home.HomeActivity
import com.example.guvenlipati.models.Offer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding

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

        binding.ConfirmPaymentButton.setOnClickListener {
            val offerId = activity?.intent?.getStringExtra("offerId")

            if (offerId != null) {
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("offers").child(offerId)

                databaseReference.updateChildren(
                    mapOf(
                        "priceStatus" to true
                    )
                ).addOnSuccessListener {
                    showToast("ÖDEME BAŞARILI")
                }
                    .addOnFailureListener {
                        showToast("ÖDEME BAŞARISIZ")
                    }

            }


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
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}