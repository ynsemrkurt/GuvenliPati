package com.example.guvenlipati.backer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentBackerSplashBinding

class BackerSplashFragment : Fragment() {

    private lateinit var binding: FragmentBackerSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBackerSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            (activity as PetBackerActivity).goRegisterBackerFragment()
        }
    }

}