package com.example.guvenlipati

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView


class AddPetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_pet, container, false)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectDog = view.findViewById<ImageView>(R.id.select_dog)
        val selectCat = view.findViewById<ImageView>(R.id.select_cat)
        val selectBird = view.findViewById<ImageView>(R.id.select_bird)

        selectDog.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity()
        }

        selectCat.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity()
        }

        selectBird.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity()
        }
    }
}