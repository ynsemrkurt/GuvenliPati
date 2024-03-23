package com.example.guvenlipati.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentProfilePreviewBinding
import com.example.guvenlipati.splash.LoginFragment

class ProfilePreviewFragment : Fragment() {
    lateinit var binding: FragmentProfilePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

}