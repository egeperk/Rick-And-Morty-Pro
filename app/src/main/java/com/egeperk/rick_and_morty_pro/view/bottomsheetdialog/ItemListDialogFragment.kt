package com.egeperk.rick_and_morty_pro.view.bottomsheetdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.egeperk.rick_and_morty.CharacterByIdQuery
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeByIdQuery
import com.egeperk.rick_and_morty.EpisodeQuery
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
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_DIALOG
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
    private var episodeAdapter: GenericAdapter<EpisodeQuery.Result>? = null
    private var charAdapter: GenericAdapter<CharactersQuery.Result>? = null
    private var charIdAdapter: GenericAdapter<EpisodeByIdQuery.Character>? = null
    private var episodeIdAdapter: GenericAdapter<CharacterByIdQuery.Episode>? = null
    private var favoriteCharAdapter: GenericAdapter<Character>? = null
    private var favoriteEpAdapter: GenericAdapter<Episode>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBottomSheetDialogBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner

            when (args.type) {
                TYPE_SEARCH -> {

                    seasonsCard.isVisible = false
                    filterBtn.isVisible = false
                    headerTitle.text = resources.getString(R.string.characters)

                    if (args.from == TYPE_SEARCH_CHAR) {

                        charAdapter =
                            GenericAdapter(R.layout.character_row) { position ->
                                findNavController().safeNavigate(
                                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToDetailFragment(
                                        charAdapter?.snapshot()?.items?.map { it.id }
                                            ?.get(position)
                                            .toString(), TYPE_HOME
                                    )
                                )
                            }
                        genericRv.apply {
                            adapter = charAdapter
                            layoutManager = GridLayoutManager(requireContext(), 2)
                        }

                        homeViewModel.charactersCount.observe(viewLifecycleOwner) {
                                itemCount.text = it.toString()
                        }

                        homeViewModel.apply {
                            getCharacterData(args.uuid.toString(), showFour = false)
                            getCharacterCount(args.uuid.toString())
                        }

                        lifecycleScope.launch {
                            homeViewModel.charResult.collectLatest {
                                charAdapter?.submitData(it)
                            }
                        }

                    } else {

                        headerTitle.text = resources.getString(R.string.episodes)
                        filterBtn.isVisible = true

                        episodeAdapter =
                            GenericAdapter(R.layout.episode_row) { position ->
                                findNavController().safeNavigate(
                                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToDetailFragment(
                                        episodeAdapter?.snapshot()?.items?.map { it.id }?.get(position)
                                            .toString(), TYPE_SEARCH
                                    )
                                )
                            }
                        genericRv.apply {
                            adapter = episodeAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }

                        episodeAdapter?.addLoadStateListener {
                            if (it.source.append is LoadState.NotLoading && it.append.endOfPaginationReached) {

                                itemCount.apply {
                                    text = episodeAdapter?.itemCount.toString()
                                    isVisible = true
                                }
                            }
                        }

                        homeViewModel.getEpisodeData(showFour = false, args.uuid.toString())


                        lifecycleScope.launch {
                            homeViewModel.episodeResult.collectLatest {
                                episodeAdapter?.submitData(it)
                            }
                        }
                    }
                }
                TYPE_FAVORITES -> {
                    if (args.from == TYPE_FAVORITES_CHAR) {

                        seasonsCard.isVisible = false
                        filterBtn.isVisible = false
                        headerTitle.text = resources.getString(R.string.characters)

                        favoriteCharAdapter =
                            GenericAdapter<Character>(R.layout.character_row_favorites) { position ->
                                findNavController().safeNavigate(ItemListDialogFragmentDirections.actionItemListDialogFragmentToDetailFragment(
                                    favoriteCharAdapter?.snapshot()?.items?.map { it.id }
                                        ?.get(position)
                                        .toString(), TYPE_FAVORITES
                                ))

                            }.apply {
                                genericRv.adapter = this
                                genericRv.layoutManager = GridLayoutManager(requireContext(), 2)
                            }

                        lifecycleScope.launch {
                            favoritesViewModel.readCharactersData()
                            favoritesViewModel.charResult.collectLatest {
                                favoriteCharAdapter?.submitData(it)
                            }
                        }
                        favoritesViewModel.characterCount.observe(viewLifecycleOwner) {
                            itemCount.text = it.toString()
                        }
                    } else {

                        headerTitle.text = resources.getString(R.string.episodes)
                        filterBtn.isVisible = true

                        favoriteEpAdapter =
                            GenericAdapter<Episode>(R.layout.episode_row_favorites) { position ->
                                findNavController().safeNavigate(ItemListDialogFragmentDirections.actionItemListDialogFragmentToEpisodeDetailFragment(
                                    favoriteEpAdapter?.snapshot()?.items?.map { it.id }
                                        ?.get(position)
                                        .toString(), TYPE_FAVORITES
                                ))

                            }.apply {
                                genericRv.adapter = this
                                genericRv.layoutManager = LinearLayoutManager(requireContext())
                            }

                        lifecycleScope.launch {
                            favoritesViewModel.readEpisodesData()
                            favoritesViewModel.episodeResult.collectLatest {
                                favoriteEpAdapter?.submitData(it)
                            }
                        }
                        favoritesViewModel.episodeCount.observe(viewLifecycleOwner) {
                            itemCount.text = it.toString()
                        }
                    }
                }
                TYPE_CHAR -> {

                    seasonsCard.isVisible = false
                    filterBtn.isVisible = false
                    headerTitle.text = resources.getString(R.string.characters)

                    if (args.from == TYPE_CHAR_BY_ID) {
                        charIdAdapter =
                            GenericAdapter(R.layout.character_row_detail) { position ->
                                findNavController().safeNavigate(
                                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToDetailFragment(
                                        charIdAdapter?.snapshot()?.items?.map { it.id }
                                            ?.get(position)
                                            .toString(), TYPE_HOME
                                    )
                                )
                            }
                        genericRv.apply {
                            adapter = charIdAdapter
                            layoutManager = GridLayoutManager(requireContext(), 2)
                        }
                        charIdAdapter?.addLoadStateListener {
                            if (it.source.append is LoadState.NotLoading && it.append.endOfPaginationReached) {

                                itemCount.apply {
                                    text = charIdAdapter?.itemCount.toString()
                                    isVisible = true
                                }
                            }
                        }

                        args.uuid?.let { id ->
                            detailViewModel.getEpisodeCharacters(
                                id,
                                showThree = false
                            )
                        }

                        lifecycleScope.launch {
                            detailViewModel.characterResult.collectLatest {
                                charIdAdapter?.submitData(it)
                            }
                        }

                    } else {

                        charAdapter =
                            GenericAdapter(R.layout.character_row) { position ->
                                findNavController().safeNavigate(
                                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToDetailFragment(
                                        charAdapter?.snapshot()?.items?.map { it.id }?.get(position)
                                            .toString(), TYPE_HOME
                                    )
                                )
                            }
                        genericRv.apply {
                            adapter = charAdapter
                            layoutManager = GridLayoutManager(requireContext(), 2)
                        }

                        homeViewModel.apply {
                            getCharacterData(EMPTY_VALUE, showFour = false)
                            getCharacterCount()
                        }

                        homeViewModel.charactersCount.observe(viewLifecycleOwner) {
                            itemCount.text = it.toString()
                        }

                        lifecycleScope.launch {
                            homeViewModel.charResult.collectLatest {
                                charAdapter?.submitData(it)
                            }
                        }
                    }
                }
                TYPE_EPISODE -> {

                    headerTitle.text = resources.getString(R.string.episodes)
                    filterBtn.isVisible = true

                    if (args.from == TYPE_EPISODE_BY_ID) {
                        episodeIdAdapter =
                            GenericAdapter(R.layout.episode_row_detail) { position ->
                                findNavController().safeNavigate(
                                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToEpisodeDetailFragment(
                                        episodeIdAdapter?.snapshot()?.items?.map {
                                            it.id
                                        }?.get(position)
                                            .toString(), TYPE_DIALOG
                                    )
                                )
                            }
                        genericRv.apply {
                            adapter = episodeIdAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }

                        episodeIdAdapter?.addLoadStateListener {
                            if (it.source.append is LoadState.NotLoading && it.append.endOfPaginationReached) {

                                itemCount.apply {
                                    text = episodeIdAdapter?.itemCount.toString()
                                    isVisible = true
                                }
                            }
                        }

                        args.uuid?.let { id ->
                            detailViewModel.getCharacterEpisodes(
                                id,
                                showThree = false
                            )
                        }

                        lifecycleScope.launch {
                            detailViewModel.episodeResult.collectLatest {
                                episodeIdAdapter?.submitData(it)
                            }
                        }

                    } else {

                        episodeAdapter =
                            GenericAdapter(R.layout.episode_row) { position ->

                                findNavController().safeNavigate(
                                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToEpisodeDetailFragment(
                                        episodeAdapter?.snapshot()?.items?.map { it.id }
                                            ?.get(position)
                                            .toString(), TYPE_DIALOG
                                    )
                                )
                            }
                        genericRv.apply {
                            adapter = episodeAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }

                        homeViewModel.apply {
                            getEpisodeData(showFour = false,null)
                            getEpisodeCount()
                        }

                        homeViewModel.episodeCount.observe(viewLifecycleOwner) {
                            itemCount.text = it.toString()
                        }

                        lifecycleScope.launch {
                            homeViewModel.episodeResult.collectLatest {
                                episodeAdapter?.submitData(it)
                            }
                        }
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

        selectSeason(binding?.season1Tv, SEASON_ONE)
        selectSeason(binding?.season2Tv, SEASON_TWO)
        selectSeason(binding?.season3Tv, SEASON_THREE)
        selectSeason(binding?.season4Tv, SEASON_FOUR)
        selectSeason(binding?.season5Tv, SEASON_FIVE)
        selectSeason(binding?.allSeasonTv, null)

    }

    private fun selectSeason(v: TextView?, filter: String?) {
        v?.setOnClickListener {
            when (args.from) {
                TYPE_EPISODE_BY_ID -> {
                    lifecycleScope.launch {
                        episodeIdAdapter?.submitData(PagingData.empty())
                        detailViewModel.episodeResult.collectLatest {
                            if (filter != null) {
                                episodeIdAdapter?.submitData(it.filter { ep ->
                                    ep.episode?.contains(filter) == true
                                })
                            } else {
                                episodeIdAdapter?.submitData(it)
                            }
                        }
                    }
                }
                TYPE_FAVORITES_EPISODE -> {
                    lifecycleScope.launch {
                        favoriteEpAdapter?.submitData(PagingData.empty())
                        favoritesViewModel.episodeResult.collectLatest {
                            if (filter != null) {
                                favoriteEpAdapter?.submitData(it.filter { ep ->
                                    ep.episode?.contains(filter) == true
                                })
                            } else {
                                favoriteEpAdapter?.submitData(it)
                            }
                        }
                    }
                }
                else -> {
                    lifecycleScope.launch {
                        episodeAdapter?.submitData(PagingData.empty())
                        homeViewModel.episodeResult.collectLatest {
                            if (filter != null) {
                                episodeAdapter?.submitData(it.filter { ep ->
                                    ep.episode?.contains(filter) == true
                                })
                            } else {
                                episodeAdapter?.submitData(it)
                            }
                        }
                    }
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