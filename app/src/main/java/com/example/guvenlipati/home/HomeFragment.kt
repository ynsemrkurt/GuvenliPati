package com.example.guvenlipati.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import com.example.guvenlipati.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardView= view.findViewById<CardView>(R.id.loadingCardView)
        val scrollView=view.findViewById<ScrollView>(R.id.scrollView)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
                .child("userBacker")
        val goBackerButton = view.findViewById<Button>(R.id.goBackerButton)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(Boolean::class.java) == true){
                    goBackerButton.visibility = View.GONE
                }
                scrollView.foreground=null
                cardView.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


            goBackerButton.setOnClickListener {
                (activity as HomeActivity).goPetBackerActivity()
            }
    }
}