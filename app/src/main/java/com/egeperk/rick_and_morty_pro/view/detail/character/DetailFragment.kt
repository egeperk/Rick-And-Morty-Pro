package com.egeperk.rick_and_morty_pro.view.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egeperk.rick_and_morty.CharacterByIdQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.databinding.FragmentDetailBinding
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE_BY_ID
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import com.egeperk.rick_and_morty_pro.util.setStatusBarDark
import com.egeperk.rick_and_morty_pro.util.setStatusBarLight
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.favorites.FavoritesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by viewModel()
    private val favoritesVieModel: FavoritesViewModel by viewModel()
    private val args by navArgs<DetailFragmentArgs>()
    private var binding: FragmentDetailBinding? = null
    private var episodeAdapter: GenericAdapter<CharacterByIdQuery.Episode>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false).apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
            activity?.setStatusBarDark(this.root)

            backBtn.setOnClickListener { findNavController().popBackStack() }

            if (arguments != null) {
                detailViewModel.apply {
                    getCharacterData(args.uuid)
                    getCharacterEpisodes(args.uuid)
                }
            }


            episodeAdapter =
                GenericAdapter(R.layout.episode_row_detail) { position ->
                    findNavController().safeNavigate(
                        DetailFragmentDirections.actionDetailFragmentToEpisodeDetailFragment(
                            episodeAdapter?.snapshot()?.items?.map { it.id }?.get(position)
                                .toString()
                        )
                    )
                }
            episodeRv.adapter = episodeAdapter

            lifecycleScope.launch {
                episodeAdapter?.submitData(detailViewModel.episodeResult.value)
            }

            episodeBtnLy.setOnClickListener {
                showEpisodeSheet()
            }

            favBtn.setOnClickListener {
                lifecycleScope.launch {


                    favoritesVieModel.addCharacter(
                        Character(
                            id = detailViewModel.character.value?.id,
                            name = detailViewModel.character.value?.name,
                            image = detailViewModel.character.value?.image,
                            status = detailViewModel.character.value?.status,
                            species = detailViewModel.character.value?.species,
                            type = detailViewModel.character.value?.type,
                            origin = detailViewModel.character.value?.origin?.name,
                            location = detailViewModel.character.value?.location?.name
                        )
                    )
                }
            }
        }
        return binding?.root
    }

    private fun showEpisodeSheet() {
        findNavController().safeNavigate(
            DetailFragmentDirections.actionDetailFragmentToItemListDialogFragment(
                TYPE_EPISODE, TYPE_EPISODE_BY_ID, args.uuid
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.root?.let { activity?.setStatusBarLight(it) }
    }
}