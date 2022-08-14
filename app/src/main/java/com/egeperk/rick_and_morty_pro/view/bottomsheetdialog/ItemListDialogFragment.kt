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
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.egeperk.rick_and_morty.CharactersQuery
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
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemListDialogFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModel<HomeViewModel>()
    private val args by navArgs<ItemListDialogFragmentArgs>()
    private var binding: FragmentBottomSheetDialogBinding? = null
    private var episodeAdapter: GenericAdapter<EpisodeQuery.Result>? = null
    private var charAdapter: GenericAdapter<CharactersQuery.Result>? = null


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

            viewModel.isDialogShown.postValue(true)

            when (args.type) {
                TYPE_CHAR -> {
                    seasonsCard.isVisible = false
                    filterBtn.isVisible = false
                    viewModel.charactersCount.observe(viewLifecycleOwner) {
                        itemCount.text = it.toString()
                    }
                    headerTitle.text = resources.getString(R.string.characters)
                    charAdapter =
                        GenericAdapter<CharactersQuery.Result>(R.layout.character_row) {}
                    genericRv.apply {
                        adapter = charAdapter
                        layoutManager = GridLayoutManager(requireContext(), 2)
                    }
                    viewModel.isDialogShown.observe(viewLifecycleOwner) {
                        if (it) {
                            viewModel.getCharacterData(EMPTY_VALUE)
                        }
                    }
                    lifecycleScope.launch {
                        viewModel.charResult.collectLatest {
                            charAdapter?.submitData(it)
                        }
                    }

                }
                TYPE_EPISODE -> {
                    viewModel.episodeCount.observe(viewLifecycleOwner) {
                        itemCount.text = it.toString()
                    }
                    headerTitle.text = resources.getString(R.string.episodes)
                    filterBtn.isVisible = true
                    episodeAdapter = GenericAdapter<EpisodeQuery.Result>(R.layout.episode_row) {}
                    genericRv.apply {
                        adapter = episodeAdapter
                        layoutManager = LinearLayoutManager(requireContext())
                    }
                    viewModel.isDialogShown.observe(viewLifecycleOwner) {
                        if (it) {
                            viewModel.getEpisodeData()
                        }
                    }
                    lifecycleScope.launch {
                        viewModel.episodeResult.collectLatest {
                            episodeAdapter?.submitData(it)
                        }
                    }
                    //setRv<EpisodeQuery.Result>(GenericAdapter(R.layout.episode_row){},viewModel.episodeResult.value,viewModel.getEpisodeData())
                }
                else -> Unit
            }
            filterBtn.setOnClickListener {
                setButtons()
            }
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isDialogShown.postValue(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isDialogShown.postValue(false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.isDialogShown.postValue(false)
    }

    private fun setButtons() {
        if (binding?.seasonsCard?.isVisible == false) {
            binding?.apply {
                seasonsCard.isVisible = true
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
            lifecycleScope.launch {
                episodeAdapter?.submitData(PagingData.empty())
                viewModel.episodeResult.collectLatest {
                    if (filter != null) {
                        episodeAdapter?.submitData(it.filter { it.episode?.contains(filter) == true })
                    } else {
                        episodeAdapter?.submitData(it)
                    }
                }
            }
            binding?.apply {
                allSeasonBtn.text = v.text
                seasonsCard.isVisible = false
                downArrow.rotation = ROTATE_DOWN
            }
        }


        /*   private fun <T> setRv(
               adapter: GenericAdapter<Any>,
               data: Any,
               query: StateFlow<PagingData<T>>,
           ) {
               binding?.genericRv?.adapter = adapter
               viewModel.isDialogShown.observe(viewLifecycleOwner) {
                   if (it) {
                       query
                   }
               }
               lifecycleScope.launch {
                   adapter.submitData(PagingData.from(listOf(data)))
               }
           }*/
    }
}