package com.egeperk.rick_and_morty_pro.view.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.adapters.pagingadapter.GenericAdapter
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.databinding.FragmentDetailBinding
import com.egeperk.rick_and_morty_pro.util.*
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_EPISODE_BY_ID
import com.egeperk.rick_and_morty_pro.util.Constants.TYPE_FAVORITES
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.favorites.FavoritesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by viewModel()
    private val favoritesVieModel: FavoritesViewModel by viewModel()
    private val args by navArgs<DetailFragmentArgs>()
    private var binding: FragmentDetailBinding? = null
    private var episodeAdapter: GenericAdapter<Episode>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false).apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
            activity?.setStatusBarDark(this.root)

            backBtn.setOnClickListener { findNavController().popBackStack() }

            if (args.from == TYPE_FAVORITES) {
                setDataFromDb()
            }

            checkDatabase()

            if (activity?.hasInternetConnection() == true) {

                if (arguments != null) {
                    detailViewModel.apply {
                        getCharacterData(args.uuid)
                        getCharacterEpisodes(args.uuid, true)
                    }
                }

                episodeAdapter =
                    GenericAdapter<Episode>(R.layout.episode_row) { position ->
                        findNavController().safeNavigate(
                            DetailFragmentDirections.actionDetailFragmentToEpisodeDetailFragment(
                                episodeAdapter?.snapshot()?.items?.map { it.id }?.get(position)
                                    .toString(), TYPE_EPISODE
                            )
                        )
                    }.apply {
                        episodeRv.adapter = this
                    }

                lifecycleScope.launch {
                    detailViewModel.episodeResult.collectLatest {
                        episodeAdapter?.submitData(it)
                    }
                }

                episodeBtnLy.setOnClickListener {
                    showEpisodeSheet()
                }

                favBtn.setOnClickListener {
                    addCharacter()
                }

            }
        }
        return binding?.root
    }

    private fun addCharacter() {
        binding?.apply {
            favImage.setImageResource(R.drawable.ic_icon_added_fav)
            addToFavsTv.text = resources.getString(R.string.added_fav)
        }

        favoritesVieModel.addCharacter(
            Character(
                id = detailViewModel.character.value?.id,
                name = detailViewModel.character.value?.name,
                image = detailViewModel.character.value?.image,
                status = detailViewModel.character.value?.status,
                gender = detailViewModel.character.value?.gender,
                species = detailViewModel.character.value?.species,
                type = detailViewModel.character.value?.type,
                origin = detailViewModel.character.value?.origin?.name,
                location = detailViewModel.character.value?.location?.name,
                pk = detailViewModel.character.value?.id?.toInt()!!,
            )
        )
    }

    private fun setDataFromDb() {

        lifecycleScope.launch {
            favoritesVieModel.readCharacterById(args.uuid).collectLatest {
                binding?.apply {
                    charName.text = it.name
                    charImage.load(it.image)
                    status.text = it.status
                    species.text = it.species
                    type.text = it.type
                    origin.text = it.origin
                    location.text = it.location
                    gender.text = it.gender
                }
            }
        }

    }

    private fun checkDatabase() {

        favoritesVieModel.characters.combineWith(detailViewModel.character)
            .observe(viewLifecycleOwner) { data ->

                if (data.first?.map {
                        it.id
                    }?.contains(data.second?.id) == true) {
                    binding?.apply {
                        favImage.setImageResource(R.drawable.ic_icon_added_fav)
                        addToFavsTv.text = resources.getString(R.string.added_fav)
                    }
                }
            }
    }

    private fun showEpisodeSheet() {
        findNavController().safeNavigate(
            DetailFragmentDirections.actionDetailFragmentToItemListDialogFragment(
                TYPE_EPISODE, TYPE_EPISODE_BY_ID, args.uuid, data = null
            )
        )
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