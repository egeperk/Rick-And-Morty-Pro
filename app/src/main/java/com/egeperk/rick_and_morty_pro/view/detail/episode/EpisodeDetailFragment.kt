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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.map
import com.egeperk.rick_and_morty.CharacterByIdQuery
import com.egeperk.rick_and_morty.EpisodeByIdQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentEpisodeDetailBinding
import com.egeperk.rick_and_morty_pro.util.Constants
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import com.egeperk.rick_and_morty_pro.util.setStatusBarDark
import com.egeperk.rick_and_morty_pro.util.setStatusBarLight
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.detail.character.DetailFragmentDirections
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class EpisodeDetailFragment : Fragment() {

    private val args by navArgs<EpisodeDetailFragmentArgs>()
    private val detailViewModel: DetailViewModel by viewModel()
    private var binding: FragmentEpisodeDetailBinding? = null
    private var charAdapter: GenericAdapter<EpisodeByIdQuery.Character>? = null
    private var locationAdapter: GenericAdapter<EpisodeByIdQuery.Character>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEpisodeDetailBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = detailViewModel
            activity?.setStatusBarDark(this.root)

            backBtn.setOnClickListener { findNavController().popBackStack() }

            if (arguments != null) {
                detailViewModel.apply {
                    getEpisodeData(args.uuid)
                    getEpisodeCharacters(args.uuid)
                }
            }

            charAdapter =
                GenericAdapter<EpisodeByIdQuery.Character>(R.layout.character_row_detail) { position ->
                    findNavController().safeNavigate(EpisodeDetailFragmentDirections.actionEpisodeDetailFragmentToDetailFragment(
                        charAdapter?.snapshot()?.items?.map {
                            it.id.toString()
                        }?.get(position).toString()
                    ))
                }
            characterRv.adapter = charAdapter


            locationAdapter = GenericAdapter(R.layout.location_row){}
            locationRv.adapter = locationAdapter



            lifecycleScope.launch {
                charAdapter?.submitData(detailViewModel.characterResult.value)
                detailViewModel.characterResult.collectLatest {
                    locationAdapter?.submitData(it)
                }
            }

            characterBtnLy.setOnClickListener {
                findNavController().safeNavigate(
                    EpisodeDetailFragmentDirections.actionEpisodeDetailFragmentToItemListDialogFragment(
                        Constants.TYPE_CHAR, Constants.TYPE_CHAR_BY_ID, args.uuid
                    )
                )
            }

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