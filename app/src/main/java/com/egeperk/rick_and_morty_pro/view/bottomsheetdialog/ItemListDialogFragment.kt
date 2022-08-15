package com.egeperk.rick_and_morty_pro.view.bottomsheetdialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemListDialogFragment : BottomSheetDialogFragment() {

    private val homeViewModel by viewModel<HomeViewModel>()
    private val detailViewModel by viewModel<DetailViewModel>()
    private val args by navArgs<ItemListDialogFragmentArgs>()
    private var binding: FragmentBottomSheetDialogBinding? = null
    private var episodeAdapter: GenericAdapter<EpisodeQuery.Result>? = null
    private var charAdapter: GenericAdapter<CharactersQuery.Result>? = null
    private var episodeIdAdapter: GenericAdapter<CharacterByIdQuery.Episode>? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = context?.let { BottomSheetDialog(it, theme) }
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { bottomSheet ->
                val behaviour = BottomSheetBehavior.from(bottomSheet)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBottomSheetDialogBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner

            homeViewModel.isDialogShown.postValue(true)
            detailViewModel.isDialogShown.postValue(true)

            when (args.type) {
                TYPE_CHAR -> {
                    seasonsCard.isVisible = false
                    filterBtn.isVisible = false
                    headerTitle.text = resources.getString(R.string.characters)

                    if (args.from == TYPE_CHAR_BY_ID) {
                        val itemAdapter =
                            GenericAdapter<EpisodeByIdQuery.Character>(R.layout.character_row_detail) {
                                findNavController().safeNavigate(
                                    ItemListDialogFragmentDirections.actionItemListDialogFragmentToDetailFragment(
                                        homeViewModel.charPosition.value?.get(it)
                                            .toString()
                                    )
                                )
                            }
                        genericRv.apply {
                            adapter = itemAdapter
                            layoutManager = GridLayoutManager(requireContext(), 2)
                        }
                        detailViewModel.isDialogShown.observe(viewLifecycleOwner) {
                            if (it) {
                                args.uuid?.let { id -> detailViewModel.getEpisodeCharacters(id) }
                            }
                        }
                        lifecycleScope.launch {
                            detailViewModel.characterResult.collectLatest {
                                itemAdapter.submitData(it)
                            }
                        }
                    } else {
                        homeViewModel.charactersCount.observe(viewLifecycleOwner) {
                            itemCount.text = it.toString()
                        }
                        charAdapter =
                            GenericAdapter<CharactersQuery.Result>(R.layout.character_row) {}
                        genericRv.apply {
                            adapter = charAdapter
                            layoutManager = GridLayoutManager(requireContext(), 2)
                        }
                        homeViewModel.isDialogShown.observe(viewLifecycleOwner) {
                            if (it) {
                                homeViewModel.getCharacterData(EMPTY_VALUE)
                            }
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
                            GenericAdapter<CharacterByIdQuery.Episode>(R.layout.episode_row_detail) {}
                        genericRv.apply {
                            adapter = episodeIdAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        detailViewModel.isDialogShown.observe(viewLifecycleOwner) {
                            if (it) {
                                args.uuid?.let { id -> detailViewModel.getCharacterEpisodes(id) }
                            }
                        }

                        lifecycleScope.launch {
                            detailViewModel.episodeResult.collectLatest {
                                episodeIdAdapter?.submitData(it)
                            }
                        }

                    } else {
                        homeViewModel.episodeCount.observe(viewLifecycleOwner) {
                            itemCount.text = it.toString()
                        }

                        episodeAdapter =
                            GenericAdapter<EpisodeQuery.Result>(R.layout.episode_row) {}
                        genericRv.apply {
                            adapter = episodeAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        homeViewModel.isDialogShown.observe(viewLifecycleOwner) {
                            if (it) {
                                homeViewModel.getEpisodeData()
                            }
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
            if (args.from == TYPE_EPISODE_BY_ID) {
                lifecycleScope.launch {
                    episodeIdAdapter?.submitData(PagingData.empty())
                    detailViewModel.episodeResult.collectLatest {
                        if (filter != null) {
                            episodeIdAdapter?.submitData(it.filter { it.episode?.contains(filter) == true })
                        } else {
                            episodeIdAdapter?.submitData(it)
                        }
                    }
                }
            } else {
                lifecycleScope.launch {
                    episodeAdapter?.submitData(PagingData.empty())
                    homeViewModel.episodeResult.collectLatest {
                        if (filter != null) {
                            episodeAdapter?.submitData(it.filter { it.episode?.contains(filter) == true })
                        } else {
                            episodeAdapter?.submitData(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.isDialogShown.postValue(false)
        detailViewModel.isDialogShown.postValue(false)

    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.isDialogShown.postValue(false)
        detailViewModel.isDialogShown.postValue(false)

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        homeViewModel.isDialogShown.postValue(false)
        detailViewModel.isDialogShown.postValue(false)

    }
}