package com.example.guvenlipati.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentAddPetBinding


class AddPetFragment : Fragment() {

    private lateinit var binding: FragmentAddPetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAddPetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectDog.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity("dog")
        }

        binding.selectCat.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity("cat")
        }

        binding.selectBird.setOnClickListener {
            (requireActivity() as HomeActivity).goRegisterPetActivity("bird")
        }

    }

}