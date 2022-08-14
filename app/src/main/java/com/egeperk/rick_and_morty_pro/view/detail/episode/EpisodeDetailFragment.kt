package com.egeperk.rick_and_morty_pro.view.detail.episode

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.egeperk.rick_and_morty_pro.databinding.FragmentEpisodeDetailBinding
import com.egeperk.rick_and_morty_pro.util.setStatusBarDark
import com.egeperk.rick_and_morty_pro.util.setStatusBarLight


class EpisodeDetailFragment : Fragment() {

    private var binding: FragmentEpisodeDetailBinding? = null
    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEpisodeDetailBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            activity?.setStatusBarDark(this.root)

            backBtn.setOnClickListener { findNavController().popBackStack() }

            val textShader: Shader = LinearGradient(
                0f,
                0f,
                0f,
                300f,
                intArrayOf(Color.WHITE, Color.TRANSPARENT),
                floatArrayOf(0f, 1f),
                TileMode.CLAMP
            )
            episodeDescription.paint.shader = textShader
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.root?.let { activity?.setStatusBarLight(it) }
    }
}