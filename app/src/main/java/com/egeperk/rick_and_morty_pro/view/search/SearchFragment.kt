package com.egeperk.rick_and_morty_pro.view.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentSearchBinding.inflate(layoutInflater, container, false).apply {  }.root
    }
}