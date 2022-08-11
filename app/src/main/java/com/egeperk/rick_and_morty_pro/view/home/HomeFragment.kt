package com.egeperk.rick_and_morty_pro.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentHomeBinding
import com.egeperk.rick_and_morty_pro.view.bottomsheetdialog.ItemListDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var charAdapter: GenericAdapter<CharactersQuery.Result>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return FragmentHomeBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel

            characterBtnLy.setOnClickListener {
                ItemListDialogFragment().show(childFragmentManager,"x")
            }

            charAdapter = GenericAdapter(R.layout.character_row) {}
            homeCharacterRv.adapter = charAdapter

            lifecycleScope.launch {
                homeViewModel.charResult.collectLatest {
                    charAdapter?.submitData(it)
                }
            }

            val xAdapter = GenericAdapter<EpisodeQuery.Result>(R.layout.episode_row) {}
            homeEpisodeRv.adapter = xAdapter

            lifecycleScope.launch {
                homeViewModel.episodeResult.collectLatest {
                    xAdapter.submitData(it)
                }
            }

        }.root
    }

}