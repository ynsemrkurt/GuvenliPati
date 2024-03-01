package com.example.guvenlipati

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

        val profilePhoto = view.findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
        val userNameEdit = view.findViewById<EditText>(R.id.editTextUserName)
        val userSurname = view.findViewById<EditText>(R.id.editTextUserSurname)
        val provinceCombo = view.findViewById<AutoCompleteTextView>(R.id.provinceCombo)
        val townCombo = view.findViewById<AutoCompleteTextView>(R.id.townCombo)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user?.userId == firebaseUser.uid) {
                    if (user.userPhoto.isEmpty()) {
                        profilePhoto.setImageResource(R.drawable.men_image)
                    }else{
                        val imageUri = Uri.parse(user.userPhoto)
                        Glide.with(this@ProfileFragment).load(imageUri)
                            .placeholder(R.drawable.men_image)
                            .into(profilePhoto)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}