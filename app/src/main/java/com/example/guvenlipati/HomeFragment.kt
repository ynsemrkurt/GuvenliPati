package com.example.guvenlipati

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goBackerButton = view.findViewById<Button>(R.id.goBackerButton)

        goBackerButton.setOnClickListener {
            val intent = Intent(requireContext(), PetBackerActivity::class.java)
            startActivity(intent)
        }
    }
}