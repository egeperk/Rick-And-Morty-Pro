package com.egeperk.rick_and_morty_pro.view.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentSearchBinding
import com.egeperk.rick_and_morty_pro.util.onRightDrawableClicked
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSearchBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel

            homeViewModel.isSearch.postValue(true)

            val charAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.character_row) {}
            searchResultRv.adapter = charAdapter

            val searchAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.search_row) {}
            searchTextRv.adapter = searchAdapter

            val episodeAdapter = GenericAdapter<EpisodeQuery.Result>(R.layout.episode_row) {}
            episodeResultRv.adapter = episodeAdapter

            searchBar.apply {

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchResultRv.isVisible = true
                        episodeResultRv.isVisible = true
                        searchHeader.text = requireContext().getString(R.string.results)
                        lifecycleScope.launch {
                            if (homeViewModel.search.value.isEmpty()) {
                                charAdapter.submitData(PagingData.empty())
                                characterBtnLy.isVisible = false
                                episodeBtnLy.isVisible = false
                            } else {

                                charAdapter.addLoadStateListener {
                                    if (it.source.append is LoadState.NotLoading && it.append.endOfPaginationReached) {

                                        searchResultRv.isVisible = true
                                        episodeResultRv.isVisible = true

                                        characterBtnLy.isVisible = true
                                        episodeBtnLy.isVisible = true
                                    }
                                    if (charAdapter.itemCount < 1 || !searchResultRv.isVisible) {
                                        characterBtnLy.isVisible = false
                                    }
                                }

                                episodeAdapter.addLoadStateListener {
                                    if (episodeAdapter.itemCount < 1 || !episodeResultRv.isVisible) {
                                        episodeBtnLy.isVisible = false
                                    }
                                }

                                homeViewModel.apply {
                                    homeViewModel.search.value.let { filter ->
                                        getCharacterData(
                                            filter,
                                            showFour = true
                                        )
                                        getEpisodeData(showFour = true, filter)
                                        charactersCount.observe(viewLifecycleOwner) {
                                            characterCount.text = it.toString()
                                        }
                                        episodeCount.observe(viewLifecycleOwner) {
                                            episodeCountTv.text = it.toString()
                                        }
                                    }
                                    episodeResult.onEach {
                                        episodeAdapter.submitData(it)
                                    }.launchIn(this@launch)
                                    charResult.onEach {
                                        charAdapter.submitData(it)
                                    }.launchIn(this@launch)
                                }
                            }
                        }
                    }
                    false
                }

                onRightDrawableClicked {
                    lifecycleScope.launch {
                        charAdapter.submitData(PagingData.empty())
                        episodeAdapter.submitData(PagingData.empty())
                    }
                    it.apply {
                        text.clear()
                        clearFocus()
                    }
                    searchHeader.text = requireContext().getString(R.string.search)
                    characterBtnLy.isVisible = false
                    episodeBtnLy.isVisible = false
                }

                doOnTextChanged { text, _, _, _ ->

                    lifecycleScope.launch {
                        charAdapter.submitData(PagingData.empty())
                        episodeAdapter.submitData(PagingData.empty())
                    }
                    characterBtnLy.isVisible = false
                    episodeBtnLy.isVisible = false

                    if (text.toString().isNotEmpty()) {
                        searchRvCard.isVisible = true
                        searchBar.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_search,
                            0,
                            R.drawable.ic_baseline_clear_24,
                            0
                        )

                        lifecycleScope.launch {
                            homeViewModel.search.collect {
                                searchAdapter.submitData(PagingData.empty())
                                homeViewModel.searchCharacterData(it)
                                searchAdapter.submitData(homeViewModel.charSearchResult.value)

                            }
                        }

                    } else {
                        searchRvCard.isVisible = false
                        searchBar.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_search,
                            0,
                            0,
                            0
                        )
                    }
                    searchAdapter.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchAdapter.itemCount < 1) {
                            searchRvCard.isVisible = false
                        }
                    }

                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        searchRvCard.isVisible = false

                        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                            this.windowToken,
                            0
                        )
                    }
                }
            }
        }.root
    }

    override fun onDestroy() {
        super.onDestroy()

        homeViewModel.apply {
            isSearch.postValue(false)
        }
    }
}