package com.egeperk.rick_and_morty_pro.view.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.databinding.FragmentFavoritesBinding
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES_CHAR
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES_EPISODE
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesFragment : Fragment() {

    private val favoritesVieModel: FavoritesViewModel by viewModel()
    private var favCharsAdapter: GenericAdapter<Character>? = null
    private var favEpisodeAdapter: GenericAdapter<Episode>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFavoritesBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = favoritesVieModel

            characterBtnLy.setOnClickListener {
                findNavController().safeNavigate(FavoritesFragmentDirections.actionFavoritesFragmentToItemListDialogFragment(TYPE_FAVORITES,
                    TYPE_FAVORITES_CHAR,null))
            }

            favCharsAdapter = GenericAdapter<Character>(R.layout.character_row_favorites) { position ->
                findNavController().safeNavigate(FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(
                    favCharsAdapter?.snapshot()?.items?.map { it.id }
                        ?.get(position)
                        .toString(), TYPE_FAVORITES
                ))
            }.apply {
                characterRv.adapter = this
            }

            lifecycleScope.launch {
                favoritesVieModel.readLimitedCharactersData.collectLatest {
                    favCharsAdapter?.submitData(it)
                }
            }

            episodeBtnLy.setOnClickListener {
                findNavController().safeNavigate(FavoritesFragmentDirections.actionFavoritesFragmentToItemListDialogFragment(TYPE_FAVORITES,
                    TYPE_FAVORITES_EPISODE,null))
            }

            favEpisodeAdapter = GenericAdapter<Episode>(R.layout.episode_row_favorites) { position ->
                findNavController().safeNavigate(FavoritesFragmentDirections.actionFavoritesFragmentToEpisodeDetailFragment(
                    favEpisodeAdapter?.snapshot()?.items?.map { it.id }
                        ?.get(position)
                        .toString()
                ))
            }.apply {
                episodesRv.adapter = this
            }

            lifecycleScope.launch {
                favoritesVieModel.readLimitedEpisodesData.collectLatest {
                    favEpisodeAdapter?.submitData(it)
                }
            }
        }.root
    }

}