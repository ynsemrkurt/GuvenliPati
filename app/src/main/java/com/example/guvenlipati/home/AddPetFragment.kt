package com.example.guvenlipati.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R


class AddPetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_pet, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectDog = view.findViewById<ImageView>(R.id.select_dog)
        val selectCat = view.findViewById<ImageView>(R.id.select_cat)
        val selectBird = view.findViewById<ImageView>(R.id.select_bird)

        selectDog.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity("dog")
        }

        selectCat.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity("cat")
        }

        selectBird.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity("bird")
        }

    }

}