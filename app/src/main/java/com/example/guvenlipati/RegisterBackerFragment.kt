package com.example.guvenlipati

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterBackerFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_backer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextFullName= view.findViewById<EditText>(R.id.editTextFullName)
        val editTextID= view.findViewById<EditText>(R.id.editTextID)
        val editTextAge= view.findViewById<EditText>(R.id.editTextAge)
        val editTextAdress= view.findViewById<EditText>(R.id.editTextAdress)
        val editTextExperience= view.findViewById<EditText>(R.id.editTextExperience)
        val editTextPetNumber=view.findViewById<EditText>(R.id.editTextPetNumber)
        val editTextBackerAbout=view.findViewById<EditText>(R.id.editTextBackerAbout)
        val checkBox=view.findViewById<CheckBox>(R.id.checkBox)
        val checkBox2=view.findViewById<CheckBox>(R.id.checkBox2)
        val checkBox3=view.findViewById<CheckBox>(R.id.checkBox3)
        val ConfirmBackerButton=view.findViewById<Button>(R.id.ConfirmBackerButton)

        auth= FirebaseAuth.getInstance()
        databaseReference= FirebaseDatabase.getInstance().getReference("identifies")

        ConfirmBackerButton.setOnClickListener{
            if(auth.currentUser!=null){

                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["userID"]=auth.currentUser!!.uid
                hashMap["fullName"]=editTextFullName.text.toString()
                hashMap["TC"]=editTextID.text.toString()
                hashMap["age"]=editTextAge.text.toString()
                hashMap["adress"]=editTextAdress.text.toString()
                hashMap["experience"]=editTextExperience.text.toString()
                hashMap["petNumber"]=editTextPetNumber.text.toString()
                hashMap["about"]=editTextBackerAbout.text.toString()

                databaseReference.setValue(hashMap)
            }
        }


    }
}