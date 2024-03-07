package com.example.guvenlipati

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class JobsSplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jobs_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createAdvertsButton = view.findViewById<Button>(R.id.createAdvertsButton)
        val loginButton = view.findViewById<Button>(R.id.loginButton)

        createAdvertsButton.setOnClickListener {
            val intent = Intent(requireContext(), FindBackerActivity::class.java)
            startActivity(intent)
        }
    }
}