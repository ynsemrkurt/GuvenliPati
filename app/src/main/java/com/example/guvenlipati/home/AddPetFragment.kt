package com.example.guvenlipati.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.addPet.RegisterPetActivity
import com.example.guvenlipati.databinding.FragmentAddPetBinding


class AddPetFragment : Fragment() {

    private lateinit var binding: FragmentAddPetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectDog.setOnClickListener {
            goRegisterPetActivity("dog")
        }

        binding.selectCat.setOnClickListener {
            goRegisterPetActivity("cat")
        }

        binding.selectBird.setOnClickListener {
            goRegisterPetActivity("bird")
        }

    }

    private fun goRegisterPetActivity(petType: String) {
        val intent = Intent(requireActivity(), RegisterPetActivity::class.java)
        intent.putExtra("petType", petType)
        startActivity(intent)
    }

}