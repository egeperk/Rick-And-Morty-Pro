package com.egeperk.rick_and_morty_pro.view.detail.episode

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.map
import coil.load
import com.egeperk.rick_and_morty.EpisodeByIdQuery
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.databinding.FragmentEpisodeDetailBinding
import com.egeperk.rick_and_morty_pro.util.*
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_CHAR
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.favorites.FavoritesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class EpisodeDetailFragment : Fragment() {

    private val args by navArgs<EpisodeDetailFragmentArgs>()
    private val detailViewModel: DetailViewModel by viewModel()
    private val favoritesVieModel: FavoritesViewModel by viewModel()
    private var binding: FragmentEpisodeDetailBinding? = null
    private var charAdapter: GenericAdapter<Character>? = null
    private var locationAdapter: GenericAdapter<EpisodeByIdQuery.Character>? = null
    private var textShader: Shader? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEpisodeDetailBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = detailViewModel
            activity?.setStatusBarDark(this.root)

            backBtn.setOnClickListener { findNavController().popBackStack() }

            if (args.from == TYPE_FAVORITES) {
                setDataFromDb()
            }

            if (activity?.hasInternetConnection() == true) {


                if (arguments != null) {
                    detailViewModel.apply {
                        getEpisodeData(args.uuid)
                        getEpisodeCharacters(args.uuid,showThree = true)
                    }
                }

                charAdapter =
                    GenericAdapter<Character>(R.layout.character_row) { position ->
                        findNavController().safeNavigate(
                            EpisodeDetailFragmentDirections.actionEpisodeDetailFragmentToDetailFragment(
                                charAdapter?.snapshot()?.items?.map {
                                    it.id.toString()
                                }?.get(position).toString(), TYPE_CHAR
                            )
                        )
                    }
                characterRv.adapter = charAdapter


                locationAdapter = GenericAdapter(R.layout.location_row) {}
                locationRv.adapter = locationAdapter


                lifecycleScope.launch {
                    detailViewModel.characterResult.collectLatest {
                        //locationAdapter?.submitData(it)
                    }
                }

                lifecycleScope.launch {
                    detailViewModel.characterResult.collectLatest {
                        charAdapter?.submitData(it)
                    }
                }

                characterBtnLy.setOnClickListener {
                    findNavController().safeNavigate(
                        EpisodeDetailFragmentDirections.actionEpisodeDetailFragmentToItemListDialogFragment(
                            TYPE_CHAR, Constants.TYPE_CHAR_BY_ID, args.uuid
                        )
                    )
                }

                textShader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    350f,
                    intArrayOf(Color.WHITE, Color.TRANSPARENT),
                    floatArrayOf(0f, 1f),
                    TileMode.CLAMP
                )
                episodeDescription.paint.shader = textShader

                favBtn.setOnClickListener {
                    favBtnImage.setImageResource(R.drawable.ic_icon_added_fav)
                    addEpisodeToDb()
                }

                showBtn.setOnClickListener {
                    setTextViewState()
                }
            }
        }
        return binding?.root
    }

    private fun addEpisodeToDb() {
        favoritesVieModel.addEpisode(
            Episode(
                id = detailViewModel.episode.value?.id,
                name = detailViewModel.episode.value?.name,
                episode = detailViewModel.episode.value?.episode,
                air_date = detailViewModel.episode.value?.air_date,
                pk = detailViewModel.episode.value?.id?.toInt()!!,
            )
        )
    }

    private fun setTextViewState() {

            if (detailViewModel.isExpanded.value == true) {
                detailViewModel.isExpanded.postValue(false)
                binding?.episodeDescription?.paint?.shader = textShader

            } else {
                detailViewModel.isExpanded.postValue(true)
                binding?.episodeDescription?.paint?.shader = null

            }

    }

    private fun setDataFromDb() {

        lifecycleScope.launch {
            favoritesVieModel.readEpisodeById(args.uuid).collectLatest {
                binding?.apply {
                    episodeHeaderNumber.text = it.episode
                    episodeName.text = it.name
                    episodeNumber.text = it.episode
                    airDate.text = it.air_date
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding?.root?.let { activity?.setStatusBarDark(it) }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.root?.let { activity?.setStatusBarLight(it) }
    }
}