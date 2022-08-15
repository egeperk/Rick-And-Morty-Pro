package com.egeperk.rick_and_morty_pro.view.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.apollographql.apollo3.api.BooleanExpression
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentSearchBinding
import com.egeperk.rick_and_morty_pro.util.Constants.EMPTY_VALUE
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentSearchBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel

            homeViewModel.isDialogShown.postValue(true)

            val itemAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.character_row) {}
            searchResultRv.adapter = itemAdapter

            searchBar.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    lifecycleScope.launch {
                        if (homeViewModel.search.value.toString().isEmpty()) {
                            itemAdapter.submitData(PagingData.empty())
                        } else {
                            homeViewModel.apply {
                                getCharacterData(homeViewModel.search.value.toString())
                                charResult.collectLatest {
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

                if (text != null) {
                    searchTextRv.isVisible = true
                }
                if (text == "" || text.isNullOrBlank() ||text.isNullOrEmpty()){
                    searchTextRv.isVisible = false
                }

                lifecycleScope.launch {
                homeViewModel.search.collectLatest { text ->

                    searchAdapter.apply {
                        submitData(PagingData.from(emptyList()))
                        submitData(homeViewModel.getCharacterData(text).value)
                    }
                    if (text == EMPTY_VALUE) {
                        searchAdapter.apply {
                            submitData(PagingData.from(emptyList()))
                            submitData(homeViewModel.getCharacterData(EMPTY_VALUE).value)
                        }
                    }

                    if (searchAdapter.snapshot().items.size == 0) {
                        searchAdapter.submitData(PagingData.from(emptyList()))
                        searchTextRv.isVisible = false
                    }

                }
                }

            }

        }.root
    }
}