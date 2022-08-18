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
import com.egeperk.rick_and_morty.EpisodeByIdQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.databinding.FragmentEpisodeDetailBinding
import com.egeperk.rick_and_morty_pro.util.*
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.favorites.FavoritesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class EpisodeDetailFragment : Fragment() {

    private val args by navArgs<EpisodeDetailFragmentArgs>()
    private val detailViewModel: DetailViewModel by viewModel()
    private val favoritesVieModel: FavoritesViewModel by viewModel()
    private var binding: FragmentEpisodeDetailBinding? = null
    private var charAdapter: GenericAdapter<EpisodeByIdQuery.Character>? = null
    private var locationAdapter: GenericAdapter<EpisodeByIdQuery.Character>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            favBtn.setOnClickListener {
                addEpisodeToDb()
            }
        }

        return binding?.root
    }

    private fun addEpisodeToDb() {
        lifecycleScope.launch {

            favoritesVieModel.addEpisode(
                Episode(
                    id = detailViewModel.episode.value?.id,
                    name = detailViewModel.episode.value?.name,
                    episode = detailViewModel.episode.value?.episode,
                    air_date = detailViewModel.episode.value?.air_date,
                    pk = detailViewModel.episode.value?.id?.toInt()!!,
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding?.root?.let { activity?.setStatusBarDark(it) }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.root?.let { activity?.setStatusBarLight(it) }
    }
}