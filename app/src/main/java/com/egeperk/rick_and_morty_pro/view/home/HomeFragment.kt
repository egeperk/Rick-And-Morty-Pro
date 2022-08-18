package com.egeperk.rick_and_morty_pro.view.home

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentHomeBinding
import com.egeperk.rick_and_morty_pro.util.Constants.EMPTY_VALUE
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_CHAR
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private var charAdapter: GenericAdapter<CharactersQuery.Result>? = null
    private var episodeAdapter: GenericAdapter<EpisodeQuery.Result>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentHomeBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel


            episodeBtnLy.setOnClickListener {
                homeViewModel.isDialogShown.value = true
                showSheet(TYPE_EPISODE)
            }

            characterBtnLy.setOnClickListener {
                homeViewModel.isDialogShown.value = true
                showSheet(TYPE_CHAR)
            }

            charAdapter = GenericAdapter(R.layout.character_row) {
                findNavController().safeNavigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                        homeViewModel.charPosition.value?.get(it)
                            .toString()
                    )
                )
            }
            homeCharacterRv.adapter = charAdapter

            lifecycleScope.launch {
                homeViewModel.charResult.collectLatest {
                    charAdapter?.submitData(it)
                }
            }

            episodeAdapter = GenericAdapter(R.layout.episode_row) {
                findNavController().safeNavigate(
                    HomeFragmentDirections.actionHomeFragmentToEpisodeDetailFragment(
                        homeViewModel.episodePosition.value?.get(it).toString()
                    )
                )
            }
            homeEpisodeRv.adapter = episodeAdapter

            homeViewModel.isDialogShown.observe(viewLifecycleOwner) {
                if (!it) {
                    homeViewModel.apply {
                        getEpisodeData()
                        getCharacterData(EMPTY_VALUE)
                    }
                }
            }
            lifecycleScope.launch {
                homeViewModel.episodeResult.collectLatest {
                    episodeAdapter?.submitData(it)
                }
            }
        }.root
    }

    private fun showSheet(type: String) {
        findNavController().safeNavigate(
            HomeFragmentDirections.actionHomeFragmentToItemListDialogFragment(
                type, null, null
            )
        )
    }
}