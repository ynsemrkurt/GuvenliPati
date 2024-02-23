package com.example.guvenlipati

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner

class SecondSignUpFragment : Fragment() {

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
    }

}