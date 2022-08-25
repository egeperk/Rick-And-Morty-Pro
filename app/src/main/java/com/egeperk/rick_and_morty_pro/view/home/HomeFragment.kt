package com.egeperk.rick_and_morty_pro.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.databinding.FragmentHomeBinding
import com.egeperk.rick_and_morty_pro.util.Constants.EMPTY_VALUE
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_CHAR
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_DIALOG
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE
import com.egeperk.rick_and_morty_pro.util.hasInternetConnection
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private var charAdapter: GenericAdapter<Character>? = null
    private var episodeAdapter: GenericAdapter<Episode>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentHomeBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel

            if (activity?.hasInternetConnection() == true) {

                homeViewModel.apply {
                    getEpisodeCount()
                    getCharacterCount()
                }

                episodeBtnLy.setOnClickListener {
                    showSheet(TYPE_EPISODE)
                }

                characterBtnLy.setOnClickListener {
                    showSheet(TYPE_CHAR)
                }

                charAdapter = GenericAdapter(R.layout.character_row) { position ->
                    findNavController().safeNavigate(
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                            charAdapter?.snapshot()?.items?.map { it.id }
                                ?.get(position)
                                .toString(), TYPE_DIALOG
                        )
                    )
                }
                homeCharacterRv.adapter = charAdapter

                lifecycleScope.launch {
                    homeViewModel.charResult.collectLatest {
                        charAdapter?.submitData(it)
                    }
                }

                episodeAdapter = GenericAdapter(R.layout.episode_row) { position ->
                    findNavController().safeNavigate(
                        HomeFragmentDirections.actionHomeFragmentToEpisodeDetailFragment(
                            episodeAdapter?.snapshot()?.items?.map { it.id }
                                ?.get(position)
                                .toString(), TYPE_DIALOG
                        )
                    )
                }
                homeEpisodeRv.adapter = episodeAdapter

                homeViewModel.apply {
                    getEpisodeData(showFour = true, name = null)
                    getCharacterData(EMPTY_VALUE, showFour = true)
                }


                lifecycleScope.launch {
                    homeViewModel.episodeResult.collectLatest {
                        episodeAdapter?.submitData(it)
                    }
                }
            }
        }.root
    }

    private fun showSheet(type: String) {
        findNavController().safeNavigate(
            HomeFragmentDirections.actionHomeFragmentToItemListDialogFragment(
                type, from = null, uuid = null
            )
        )
    }
}