package com.egeperk.rick_and_morty_pro.view.bottomsheetdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.databinding.FragmentBottomSheetDialogBinding
import com.egeperk.rick_and_morty_pro.util.Constants.EMPTY_VALUE
import com.egeperk.rick_and_morty_pro.util.Constants.ROTATE_DOWN
import com.egeperk.rick_and_morty_pro.util.Constants.ROTATE_UP
import com.egeperk.rick_and_morty_pro.util.Constants.SEASON_FIVE
import com.egeperk.rick_and_morty_pro.util.Constants.SEASON_FOUR
import com.egeperk.rick_and_morty_pro.util.Constants.SEASON_ONE
import com.egeperk.rick_and_morty_pro.util.Constants.SEASON_THREE
import com.egeperk.rick_and_morty_pro.util.Constants.SEASON_TWO
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_CHAR
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_CHAR_BY_ID
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE_BY_ID
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES_CHAR
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES_EPISODE
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_HOME
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_SEARCH
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_SEARCH_CHAR
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.favorites.FavoritesViewModel
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemListDialogFragment : BottomSheetDialogFragment() {

    private val homeViewModel by viewModel<HomeViewModel>()
    private val detailViewModel by viewModel<DetailViewModel>()
    private val favoritesViewModel by viewModel<FavoritesViewModel>()
    private val args by navArgs<ItemListDialogFragmentArgs>()
    private var binding: FragmentBottomSheetDialogBinding? = null
    private var episodeAdapter: GenericAdapter<Episode>? = null
    private var characterAdapter: GenericAdapter<Character>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBottomSheetDialogBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            binding = this

            when (args.type) {
                TYPE_SEARCH -> {

                    setCharacterScreen()

                    if (args.from == TYPE_SEARCH_CHAR) {
                        setCharacterAdapter(args.uuid.toString(), from = null)
                    } else {
                        setEpisodeScreen()
                        setEpisodeAdapter(args.uuid.toString(), from = null)
                    }
                }
                TYPE_FAVORITES -> {

                    if (args.from == TYPE_FAVORITES_CHAR) {
                        setCharacterScreen()

                        setCharacterAdapter(arg = null, args.from)
                    } else {
                        setEpisodeScreen()

                        setEpisodeAdapter(arg = null, args.from)
                    }
                }
                TYPE_CHAR -> {

                    setCharacterScreen()

                    if (args.from == TYPE_CHAR_BY_ID) {
                        setCharacterAdapter(args.uuid, args.from)
                    } else {
                        setCharacterAdapter(EMPTY_VALUE, from = null)
                    }
                }
                TYPE_EPISODE -> {
                    setEpisodeScreen()
                    if (args.from == TYPE_EPISODE_BY_ID) {
                        setEpisodeAdapter(args.uuid, args.from)
                    } else {
                        setEpisodeAdapter(EMPTY_VALUE, from = null)
                    }
                }
                else -> Unit
            }
            filterBtn.setOnClickListener {
                setButtons()
            }
        }
        return binding?.root
    }

    private fun setEpisodeAdapter(arg: String?, from: String?) {

        episodeAdapter =
            GenericAdapter(R.layout.episode_row) { position ->
                findNavController().safeNavigate(
                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToEpisodeDetailFragment(
                        episodeAdapter?.snapshot()?.items?.map { it.id }
                            ?.get(position)
                            .toString(), TYPE_SEARCH
                    )
                )
            }
        binding?.genericRv?.apply {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        when (from) {
            TYPE_FAVORITES_EPISODE -> {
                lifecycleScope.launch {
                    favoritesViewModel.apply {
                        readEpisodesData()
                        episodeResult.collectLatest {
                            episodeAdapter?.submitData(it)
                        }
                    }
                }
                favoritesViewModel.episodeCount.observe(viewLifecycleOwner) {
                    binding?.itemCount?.text = it.toString()
                }
            }
            TYPE_EPISODE_BY_ID -> {

                detailViewModel.apply {
                    getCharacterEpisodes(
                        arg ?: EMPTY_VALUE,
                        showThree = false
                    )
                    getCharacterData(arg ?: EMPTY_VALUE)
                }
                lifecycleScope.launch {
                    detailViewModel.episodeResult.collectLatest {
                        episodeAdapter?.submitData(it)
                    }
                }
                detailViewModel.character.observe(viewLifecycleOwner) {
                    binding?.itemCount?.text = it?.episode?.size.toString()
                }
            }
            else -> {
                homeViewModel.apply {
                    getEpisodeData(showFour = false, arg)
                    getEpisodeCount(arg ?: EMPTY_VALUE)
                }

                lifecycleScope.launch {
                    homeViewModel.episodeResult.collectLatest {
                        episodeAdapter?.submitData(it)
                    }
                }
                homeViewModel.episodeCount.observe(viewLifecycleOwner) {
                    binding?.itemCount?.text = it.toString()
                }
            }
        }
    }

    private fun setCharacterAdapter(arg: String?, from: String?) {
        characterAdapter =
            GenericAdapter(R.layout.character_row) { position ->
                findNavController().safeNavigate(
                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToDetailFragment(
                        characterAdapter?.snapshot()?.items?.map { it.id }
                            ?.get(position)
                            .toString(), TYPE_HOME
                    )
                )
            }
        binding?.genericRv?.apply {
            adapter = characterAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        when (from) {
            TYPE_FAVORITES_CHAR -> {

                favoritesViewModel.characters.observe(viewLifecycleOwner) {
                    lifecycleScope.launch {
                        characterAdapter?.submitData(PagingData.from(it))
                    }
                }
                favoritesViewModel.characterCount.observe(viewLifecycleOwner) {
                    binding?.itemCount?.text = it.toString()
                }

            }
            TYPE_CHAR_BY_ID -> {

                detailViewModel.apply {
                    getEpisodeCharacters(
                        arg ?: EMPTY_VALUE,
                        showThree = false
                    )
                    getEpisodeData(arg ?: EMPTY_VALUE)
                }

                lifecycleScope.launch {
                    detailViewModel.characterResult.collectLatest {
                        characterAdapter?.submitData(it)
                    }
                }
                detailViewModel.episode.observe(viewLifecycleOwner) {
                    binding?.itemCount?.text = it?.characters?.size.toString()
                }
            }
            else -> {
                homeViewModel.apply {
                    getCharacterData(arg ?: EMPTY_VALUE, showFour = false)
                    getCharacterCount(arg ?: EMPTY_VALUE)
                }

                lifecycleScope.launch {
                    homeViewModel.charResult.collectLatest {
                        characterAdapter?.submitData(it)
                    }
                }
                homeViewModel.charactersCount.observe(viewLifecycleOwner) {
                    binding?.itemCount?.text = it.toString()
                }
            }
        }
    }

    private fun setCharacterScreen() {
        binding?.apply {
            seasonsCard.isVisible = false
            filterBtn.isVisible = false
            headerTitle.text = resources.getString(R.string.characters)
        }
    }

    private fun setEpisodeScreen() {
        binding?.apply {

            headerTitle.text = resources.getString(R.string.episodes)
            filterBtn.isVisible = true
        }
    }

    private fun setButtons() {
        if (binding?.seasonsCard?.isVisible == false) {
            binding?.apply {
                seasonsCard.isVisible = true
                seasonsCard.bringToFront()
                downArrow.rotation = ROTATE_UP
            }
        } else {
            binding?.apply {
                seasonsCard.isVisible = false
                downArrow.rotation = ROTATE_DOWN
            }
        }
        val data: PagingData<Episode> = when (args.from) {
            TYPE_EPISODE_BY_ID -> {
                detailViewModel.episodeResult.value
            }
            TYPE_FAVORITES_EPISODE -> {
                favoritesViewModel.episodeResult.value
            }
            else -> {
                homeViewModel.episodeResult.value
            }
        }

        selectSeason(binding?.season1Tv, SEASON_ONE, data)
        selectSeason(binding?.season2Tv, SEASON_TWO, data)
        selectSeason(binding?.season3Tv, SEASON_THREE, data)
        selectSeason(binding?.season4Tv, SEASON_FOUR, data)
        selectSeason(binding?.season5Tv, SEASON_FIVE, data)
        selectSeason(binding?.allSeasonTv, filter = null, data)

    }

    private fun selectSeason(v: TextView?, filter: String?, data: PagingData<Episode>) {
        v?.setOnClickListener {

            lifecycleScope.launch {
                episodeAdapter?.submitData(PagingData.empty())
                if (filter != null) {
                    episodeAdapter?.submitData(data.filter { ep ->
                        ep.episode?.contains(filter) == true
                    })
                } else {
                    episodeAdapter?.submitData(data)
                }
            }

            binding?.apply {
                allSeasonBtn.text = v.text
                seasonsCard.isVisible = false
                downArrow.rotation = ROTATE_DOWN
            }
        }
    }
}