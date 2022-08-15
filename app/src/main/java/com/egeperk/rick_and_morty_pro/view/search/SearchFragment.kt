package com.egeperk.rick_and_morty_pro.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.MotionEvent
import com.egeperk.rick_and_morty_pro.util.Constants.EMPTY_VALUE
import com.egeperk.rick_and_morty_pro.util.onRightDrawableClicked


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

            homeViewModel.apply {
                isDialogShown.postValue(true)
                isSearch.postValue(true)
            }

            val itemAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.character_row) {}
            searchResultRv.adapter = itemAdapter


            searchBar.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchRvCard.isVisible = false
                    lifecycleScope.launch {
                        if (homeViewModel.search.value.isEmpty()) {
                            itemAdapter.submitData(PagingData.from(emptyList()))
                            itemHeader.isVisible = false
                            itemCount.isVisible = false
                        } else {

                            itemAdapter.addLoadStateListener {
                                if (it.source.append is LoadState.NotLoading && it.append.endOfPaginationReached) {
                                    itemCount.apply {
                                        text = itemAdapter.itemCount.toString()
                                        isVisible = true
                                    }
                                    itemHeader.apply {
                                        text = resources.getString(R.string.characters)
                                        this.isVisible = true
                                    }
                                }
                            }
                            homeViewModel.apply {
                                homeViewModel.search.value.let { getCharacterData(it).value }
                                charResult.collect {
                                    itemAdapter.submitData(it)

                                }
                            }
                        }
                    }
                }
                false
            }
            searchBar.doOnTextChanged { text, start, before, count ->
                val searchAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.search_row) {}
                searchTextRv.adapter = searchAdapter

                if (text.toString().isNotEmpty()) {
                    searchBar.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search,
                        0,
                        R.drawable.ic_baseline_clear_24,
                        0
                    );
                } else {
                    searchBar.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search,
                        0,
                        0,
                        0
                    );
                }

                if (text.isNullOrEmpty()) {
                      searchRvCard.isVisible = true
                  }

                lifecycleScope.launch {
                    homeViewModel.search.collectLatest {
                        searchAdapter.submitData(homeViewModel.getCharacterData(it).value)

                    }

                }
               searchAdapter.addLoadStateListener { loadState ->
                    lifecycleScope.launch {
                        searchRvCard.isVisible =
                            !(loadState.append.endOfPaginationReached && searchAdapter.itemCount < 1)
                    }
                }
            }

            searchBar.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    searchRvCard.isVisible = false
                }
            }

            searchBar.onRightDrawableClicked {
                searchRvCard.isVisible = false
                it.text.clear()
                it.clearFocus()
            }

        }.root
    }

    override fun onDestroy() {
        super.onDestroy()

        homeViewModel.apply {
            isDialogShown.postValue(false)
            isSearch.postValue(false)
        }
    }
}