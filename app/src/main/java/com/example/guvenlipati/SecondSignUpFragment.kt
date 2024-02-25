package com.example.guvenlipati

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SecondSignUpFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var userGender: Boolean? = null
        val buttonFemale = view.findViewById<Button>(R.id.buttonFemale)
        val buttonMale = view.findViewById<Button>(R.id.buttonMale)

        val spinnerProvince: Spinner = view.findViewById(R.id.spinnerProvince)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.city_array,
            android.R.layout.simple_spinner_item
        )
        spinnerProvince.adapter = adapter

        val spinnerTown: Spinner = view.findViewById(R.id.spinnerTown)
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.town_array,
            android.R.layout.simple_spinner_item
        )
        spinnerTown.adapter = adapter2

        buttonFemale.setOnClickListener {
            userGender = true
            buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonFemale.setTextColor(Color.WHITE)
            buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonMale.setTextColor(Color.BLACK)
        }

        buttonMale.setOnClickListener {
            userGender = false
            buttonMale.setBackgroundResource(R.drawable.sign2_edittext_bg2)
            buttonMale.setTextColor(Color.WHITE)
            buttonFemale.setBackgroundResource(R.drawable.sign2_edittext_bg)
            buttonFemale.setTextColor(Color.BLACK)
        }


        view.findViewById<Button>(R.id.saveProfileButton).setOnClickListener {

            firebaseUser = FirebaseAuth.getInstance().currentUser!!
            databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

            val userName = view.findViewById<EditText>(R.id.editTextUserName)
            val userSurname = view.findViewById<EditText>(R.id.editTextUserSurname)
            val userProvince = spinnerProvince.selectedItem.toString()
            val userTown = spinnerTown.selectedItem.toString()


            if (userName.text.isEmpty()) {
                showToast("İsminizi giriniz!")
                return@setOnClickListener
            }

            if (userSurname.text.isEmpty()) {
                showToast("Soyadınızı giriniz!")
                return@setOnClickListener
            }

            if (userGender == null) {
                showToast("Cinsiyetinizi seçiniz!")
                return@setOnClickListener
            }

            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["userId"] = firebaseUser.uid
            hashMap["userPhoto"] = ""
            hashMap["userName"] = userName.text.toString()
            hashMap["userSurname"] = userSurname.text.toString()
            hashMap["userGender"] = userGender.toString()
            hashMap["userProvince"] = userProvince
            hashMap["userTown"] = userTown

            databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Profil kaydı başarılı :)")
                } else {
                    showToast("Hatalı işlem!")
                }
            }
        }

        view.findViewById<ImageButton>(R.id.backToSplash).setOnClickListener{
            showAlertDialog()
        }
    }
    private fun deleteUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Kullanıcı silindi.")
                (activity as MainActivity).goSplashFragment()
                showToast("Kayıt Silindi")
            } else {
                showToast("Silme işlemi başarısız!")
                task.exception
            }
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())

        alertDialogBuilder.setTitle("Geri dönmek istediğinize emin misiniz?")
        alertDialogBuilder.setMessage("Eğer geri dönerseniz kaydınız silinecektir.")

        alertDialogBuilder.setPositiveButton("Sil") { _, _ ->
            deleteUserData()
        }

        alertDialogBuilder.setNegativeButton("İptal") { _, _ ->
            showToast("İptal Edildi")
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}