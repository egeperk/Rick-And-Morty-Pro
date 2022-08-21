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
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentSearchBinding
import com.egeperk.rick_and_morty_pro.util.onRightDrawableClicked
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentSearchBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel

            searchRvCard.isVisible = false

            homeViewModel.apply {
                isSearch.postValue(true)
            }

            val itemAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.character_row) {}
            searchResultRv.adapter = itemAdapter

            val searchAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.search_row) {}
            searchTextRv.adapter = searchAdapter

            searchBar.apply {

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchResultRv.isVisible = true
                        searchHeader.text = requireContext().getString(R.string.results)
                        lifecycleScope.launch {
                            if (homeViewModel.search.value.isEmpty()) {
                                itemAdapter.submitData(PagingData.empty())
                                itemHeader.isVisible = false
                                itemCount.isVisible = false
                            } else {

                                itemAdapter.addLoadStateListener {
                                    if (it.source.append is LoadState.NotLoading && it.append.endOfPaginationReached) {

                                        searchResultRv.isVisible = true
                                        itemCount.apply {
                                            text = itemAdapter.itemCount.toString()
                                            isVisible = true
                                        }
                                        itemHeader.apply {
                                            text = resources.getString(R.string.characters)
                                            this.isVisible = true
                                        }
                                    }
                                    if (itemAdapter.itemCount < 1 || !searchResultRv.isVisible) {
                                        itemCount.isVisible = false
                                        itemHeader.isVisible = false
                                    }
                                    if (it.source.append is LoadState.Loading) {
                                        searchResultRv.isVisible = false
                                    }
                                }

                                homeViewModel.apply {
                                    homeViewModel.search.value.let { getCharacterData(it,showFour = false).value }
                                    charResult.collectLatest {
                                        itemAdapter.submitData(PagingData.empty())
                                        itemAdapter.submitData(it)
                                    }
                                }
                            }
                        }
                    }
                    false
                }

                onRightDrawableClicked {
                    searchRvCard.isVisible = false
                    searchResultRv.isVisible = false
                    it.text.clear()
                    it.clearFocus()
                    searchHeader.text = requireContext().getString(R.string.search)
                    itemHeader.isVisible = false
                    itemCount.isVisible = false
                }

                doOnTextChanged { text, start, before, count ->

                    searchResultRv.isVisible = false
                    itemCount.isVisible = false
                    itemHeader.isVisible = false


                    if (text.toString().isNotEmpty()) {
                        searchRvCard.isVisible = true
                        searchBar.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_search,
                            0,
                            R.drawable.ic_baseline_clear_24,
                            0
                        )
                    } else {
                        itemCount.isVisible = false
                        itemHeader.isVisible = false
                        searchResultRv.isVisible = false
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
                lifecycleScope.launch {
                    homeViewModel.search.collect {
                        if (it.isEmpty()) {
                            itemAdapter.submitData(PagingData.empty())
                        } else {
                            searchAdapter.submitData(PagingData.empty())
                            searchAdapter.submitData(homeViewModel.getCharacterData(it,showFour = true).value)
                        }
                    }
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        searchRvCard.isVisible = false

                        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this.windowToken, 0)
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