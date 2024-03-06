package com.example.guvenlipati

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterBackerFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_backer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val editTextFullName = view.findViewById<EditText>(R.id.editTextFullName)
        val editTextID = view.findViewById<EditText>(R.id.editTextID)
        val editTextAge = view.findViewById<EditText>(R.id.editTextAge)
        val editTextAdress = view.findViewById<EditText>(R.id.editTextAdress)
        val editTextExperience = view.findViewById<EditText>(R.id.editTextExperience)
        val editTextPetNumber = view.findViewById<EditText>(R.id.editTextPetNumber)
        val editTextBackerAbout = view.findViewById<EditText>(R.id.editTextBackerAbout)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val checkBox2 = view.findViewById<CheckBox>(R.id.checkBox2)
        val checkBox3 = view.findViewById<CheckBox>(R.id.checkBox3)
        val confirmBackerButton = view.findViewById<Button>(R.id.ConfirmBackerButton)
        val progressCard = view.findViewById<View>(R.id.progressCard)
        val buttonPaws = view.findViewById<ImageView>(R.id.buttonPaw2)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("identifies")
                .child(firebaseUser.uid)
        databaseReference2 =
            FirebaseDatabase.getInstance().getReference("users")
                .child(firebaseUser.uid)


        confirmBackerButton.setOnClickListener {
            val backerAge = editTextAge.text.toString().toIntOrNull()

            if (auth.currentUser != null) {


                if (editTextFullName.text.isEmpty() || editTextAdress.text.isEmpty() || editTextExperience.text.isEmpty() || editTextBackerAbout.text.isEmpty() || editTextPetNumber.text.isEmpty()) {
                    showToast("Lütfen boş alan bırakmayınız!")
                    return@setOnClickListener
                }
                if (!isTCKNCorrect(editTextID.text.toString())) {
                    showToast("TC kimlik numaranızı doğru giriniz!")
                    return@setOnClickListener
                }
                if (backerAge != null) {
                    if (backerAge < 18 || backerAge > 80 || editTextAge.text.toString().isEmpty()) {
                        showToast("Yaşınız 18'in altında veya 80'in üstünde olamaz!")
                        return@setOnClickListener
                    }
                }
                if (!checkBox.isChecked || !checkBox2.isChecked || !checkBox3.isChecked) {
                    showToast("Sözleşmeleri kabul etmeniz gerekmektedir!")
                    return@setOnClickListener
                }


                progressCard.visibility = View.VISIBLE
                buttonPaws.visibility = View.INVISIBLE
                confirmBackerButton.visibility = View.INVISIBLE

                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["userID"] = auth.currentUser!!.uid
                hashMap["fullName"] = editTextFullName.text.toString()
                hashMap["TC"] = editTextID.text.toString()
                hashMap["age"] = editTextAge.text.toString()
                hashMap["adress"] = editTextAdress.text.toString()
                hashMap["experience"] = editTextExperience.text.toString()
                hashMap["petNumber"] = editTextPetNumber.text.toString()
                hashMap["about"] = editTextBackerAbout.text.toString()
                hashMap["dogBacker"] = false
                hashMap["catBacker"] = false
                hashMap["birdBacker"] = false
                hashMap["userAvailability"]=0
                hashMap["homeJob"]=false
                hashMap["feedingJob"]=false
                hashMap["walkingJob"]=false
                hashMap["homeMoney"]=0
                hashMap["feedingMoney"]=0
                hashMap["walkingMoney"]=0
                //Müsaitlik durumu 1->Hafta İçi 2->Hafta Sonu 3->Tüm Günler

                databaseReference2.child("userBacker").setValue(true)

                databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        (activity as PetBackerActivity).goBackerPreferenceFragment()
                        showToast("Bakıcı Kaydı Başarılı!")
                    } else {
                        showToast("Hatalı işlem!")
                    }
                    progressCard.visibility = View.INVISIBLE
                    buttonPaws.visibility = View.VISIBLE
                    confirmBackerButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isTCKNCorrect(id: String?): Boolean {
        if (id == null) return false

        if (id.length != 11) return false

        val chars = id.toCharArray()
        val a = IntArray(11)

        for (i in 0 until 11) {
            a[i] = chars[i] - '0'
        }

        if (a[0] == 0) return false
        if (a[10] % 2 == 1) return false

        if (((a[0] + a[2] + a[4] + a[6] + a[8]) * 7 - (a[1] + a[3] + a[5] + a[7])) % 10 != a[9]) return false

        if ((a[0] + a[1] + a[2] + a[3] + a[4] + a[5] + a[6] + a[7] + a[8] + a[9]) % 10 != a[10]) return false

        return true
    }
}