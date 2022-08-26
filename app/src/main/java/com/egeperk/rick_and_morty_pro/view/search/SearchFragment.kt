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
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentSearchBinding
import com.egeperk.rick_and_morty_pro.util.Constants
import com.egeperk.rick_and_morty_pro.util.onRightDrawableClicked
import com.egeperk.rick_and_morty_pro.util.safeNavigate
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private var charAdapter: GenericAdapter<CharactersQuery.Result>? = null
    private var searchAdapter: GenericAdapter<CharactersQuery.Result>? = null
    private var episodeAdapter: GenericAdapter<EpisodeQuery.Result>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSearchBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel

            homeViewModel.isSearch.postValue(true)

            characterBtnLy.setOnClickListener {
                findNavController().safeNavigate(
                    SearchFragmentDirections.actionSearchFragmentToItemListDialogFragment(
                        Constants.TYPE_SEARCH,
                        Constants.TYPE_SEARCH_CHAR,homeViewModel.search.value))
            }

            episodeBtnLy.setOnClickListener {
                findNavController().safeNavigate(
                    SearchFragmentDirections.actionSearchFragmentToItemListDialogFragment(
                        Constants.TYPE_SEARCH,
                        Constants.TYPE_SEARCH_EPISODE,homeViewModel.search.value))
            }

            charAdapter = GenericAdapter(R.layout.character_row) { position ->
                findNavController().safeNavigate(
                    SearchFragmentDirections.actionSearchFragmentToDetailFragment(
                        charAdapter?.snapshot()?.items?.map { it.id }
                            ?.get(position)
                            .toString(), Constants.TYPE_DIALOG)
                )
            }
            searchResultRv.adapter = charAdapter

            searchAdapter = GenericAdapter(R.layout.search_row) { position ->
                findNavController().safeNavigate(
                    SearchFragmentDirections.actionSearchFragmentToDetailFragment(
                        searchAdapter?.snapshot()?.items?.map { it.id }
                            ?.get(position)
                            .toString(), Constants.TYPE_DIALOG)
                )
            }
            searchTextRv.adapter = searchAdapter

            episodeAdapter = GenericAdapter(R.layout.episode_row) { position ->
                findNavController().safeNavigate(
                    SearchFragmentDirections.actionSearchFragmentToEpisodeDetailFragment(
                        episodeAdapter?.snapshot()?.items?.map { it.id }
                            ?.get(position)
                            .toString(), Constants.TYPE_DIALOG)
                )
            }
            episodeResultRv.adapter = episodeAdapter

            searchBar.apply {

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchHeader.text = requireContext().getString(R.string.results)
                        lifecycleScope.launch {
                            if (homeViewModel.search.value.isEmpty()) {
                            } else {

                                charAdapter?.addLoadStateListener {
                                    if (it.source.append is LoadState.NotLoading && it.append.endOfPaginationReached) {
                                        loadingLy.isVisible = false
                                        characterBtnLy.isVisible = true
                                    }
                                    if (charAdapter?.itemCount == 0) {
                                        characterBtnLy.isVisible = false
                                    }
                                    if (it.source.append is LoadState.Loading) {
                                        itemLy.isVisible = false
                                        loadingLy.isVisible = true
                                    }
                                }

                                homeViewModel.apply {
                                    homeViewModel.search.value.let { filter ->
                                        getCharacterData(
                                            filter,
                                            showFour = true
                                        )
                                        getCharacterCount(filter)
                                        getEpisodeData(showFour = true, filter)
                                        getEpisodeCount(filter)
                                        charactersCount.observe(viewLifecycleOwner) {
                                            characterCount.text = it.toString()
                                            searchResultRv.isVisible = it != null
                                            characterBtnLy.isVisible = it != null
                                        }
                                        episodeCount.observe(viewLifecycleOwner) {
                                            episodeCountTv.text = it.toString()
                                            episodeBtnLy.isVisible = it != null
                                        }
                                    }
                                    episodeResult.onEach {
                                        episodeAdapter?.apply {
                                            submitData(PagingData.empty())
                                            submitData(it)
                                        }
                                    }.launchIn(this@launch)
                                    charResult.onEach {
                                        charAdapter?.apply {
                                            submitData(PagingData.empty())
                                            submitData(it)
                                        }
                                    }.launchIn(this@launch)
                                }
                            }
                        }
                    }
                    false
                }

                onRightDrawableClicked {
                    it.apply {
                        text.clear()
                        clearFocus()
                    }
                    searchHeader.text = requireContext().getString(R.string.search)
                }

                doOnTextChanged { text, _, _, _ ->

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
                                searchAdapter?.submitData(PagingData.empty())
                                homeViewModel.searchCharacterData(it)
                                searchAdapter?.submitData(homeViewModel.charSearchResult.value)

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
                    searchAdapter?.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchAdapter?.itemCount == 0) {
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