package com.egeperk.rick_and_morty_pro.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {

    private var binding: FragmentDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false).apply {

            activity?.window?.statusBarColor = resources.getColor(R.color.app_black)
            WindowInsetsControllerCompat(activity?.window!!, this.root).isAppearanceLightStatusBars = false


        }
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.statusBarColor = resources.getColor(R.color.white)
        WindowInsetsControllerCompat(activity?.window!!, binding?.root!!).isAppearanceLightStatusBars = true

    }
}