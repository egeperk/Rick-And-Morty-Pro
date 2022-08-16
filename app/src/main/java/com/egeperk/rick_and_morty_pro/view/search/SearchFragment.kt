package com.egeperk.rick_and_morty_pro.view.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.databinding.FragmentSearchBinding
import com.egeperk.rick_and_morty_pro.util.onRightDrawableClicked
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
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
                isDialogShown.postValue(true)
                isSearch.postValue(true)
            }

            val itemAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.character_row) {}
            searchResultRv.adapter = itemAdapter

            val searchAdapter = GenericAdapter<CharactersQuery.Result>(R.layout.search_row) {}
            searchTextRv.adapter = searchAdapter

            searchBar.apply {

                setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchResultRv.isVisible = true
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
                                    if (itemAdapter.itemCount < 1 || !searchResultRv.isVisible) {
                                        itemCount.isVisible = false
                                        itemHeader.isVisible = false
                                    }
                                }
                                homeViewModel.apply {
                                    homeViewModel.search.value.let { getCharacterData(it).value }
                                    charResult.collectLatest {
                                        itemAdapter.submitData(it)
                                    }
                                }
                            }
                        }
                    }
                    false
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
                    lifecycleScope.launch {
                        homeViewModel.search.collectLatest {
                            searchAdapter.submitData(homeViewModel.getCharacterData(it).value)
                        }
                    }


                    if (searchAdapter.itemCount > 1) {
                        searchTextRv.layoutParams.height = mainLy.height / 2
                    } else {
                        searchTextRv.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
                    }
                }


                setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        searchRvCard.isVisible = false
                        val keyboard =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        keyboard.hideSoftInputFromWindow(this.windowToken, 0);
                    }
                }

                onRightDrawableClicked {
                    searchRvCard.isVisible = false
                    searchResultRv.isVisible = false
                    it.text.clear()
                    it.clearFocus()
                    itemHeader.isVisible = false
                    itemCount.isVisible = false
                }
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