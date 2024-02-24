package com.example.guvenlipati

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.Reference

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


        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {

            firebaseUser = FirebaseAuth.getInstance().currentUser!!
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

            val userName=view.findViewById<EditText>(R.id.editTextUserName)
            val userSurname=view.findViewById<EditText>(R.id.editTextUserSurname)
            val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup2)
            val selectedRadioButtonId: Int = radioGroup.checkedRadioButtonId
            val userGender: Boolean? = when (selectedRadioButtonId) {
                R.id.radioButtonMale -> true
                R.id.radioButtonFemale -> false
                else -> null
            }
            val userProvince=view.findViewById<Spinner>(R.id.spinnerProvince).selectedItem.toString()
            val userTown=view.findViewById<Spinner>(R.id.spinnerTown).selectedItem.toString()


            if (userName.text.isEmpty()){
                showToast("İsminizi giriniz!")
                return@setOnClickListener
            }

            if (userSurname.text.isEmpty()){
                showToast("Soyadınızı giriniz!")
                return@setOnClickListener
            }

            if (userGender==null){
                showToast("Cinsiyetinizi seçiniz!")
                return@setOnClickListener
            }

            if (userProvince==null || userTown==null){
                showToast("Şehir ve İlçe seçiniz!")
                return@setOnClickListener
            }

            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["userId"]=firebaseUser.uid
            hashMap["userPhoto"]=""
            hashMap["userName"]=userName.text.toString()
            hashMap["userSurname"]=userSurname.text.toString()
            hashMap["userGender"]=userGender.toString()
            hashMap["userProvince"]=userProvince.toString()
            hashMap["userTown"]=userTown.toString()

            databaseReference.setValue(hashMap).addOnCompleteListener{task ->
                if (task.isSuccessful){
                    showToast("Profil kaydı başarılı :)")
                }
                else{
                    showToast("Hatalı işlem!")
                }
            }
        }
    }
    private fun showToast(message:String){
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
    }
}