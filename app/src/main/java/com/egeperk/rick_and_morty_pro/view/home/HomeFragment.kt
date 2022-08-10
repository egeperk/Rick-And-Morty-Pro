package com.egeperk.rick_and_morty_pro.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return FragmentHomeBinding.inflate(layoutInflater, container, false).apply {

        }.root
    }

}