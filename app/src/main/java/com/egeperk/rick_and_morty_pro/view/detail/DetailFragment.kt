package com.egeperk.rick_and_morty_pro.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.databinding.FragmentDetailBinding
import com.egeperk.rick_and_morty_pro.util.setStatusBarDark
import com.egeperk.rick_and_morty_pro.util.setStatusBarLight
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by viewModel()
    val args by navArgs<DetailFragmentArgs>()
    private var binding: FragmentDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false).apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
            activity?.setStatusBarDark(this.root)

            if (arguments != null) {
                detailViewModel.getCharacterData(args.uuid.toString())
            }
        }
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.root?.let { activity?.setStatusBarLight(it) }
    }
}