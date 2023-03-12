package com.antique.home.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.antique.home.R
import com.antique.home.databinding.FragmentFullScreenBinding
import com.bumptech.glide.Glide

class FullScreenFragment : Fragment() {
    private var _binding: FragmentFullScreenBinding? = null
    private val binding get() = _binding!!

    private val args: FullScreenFragmentArgs by navArgs()
    private val imageUrl by lazy { args.imageUrl }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView).isAppearanceLightStatusBars = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_full_screen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupInsets()
        setupViewListener()
        setupViewState()
    }


    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.closeView) { view, insets ->
            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = insets.systemWindowInsets.top + layoutParams.topMargin
            view.layoutParams = layoutParams
            insets
        }
    }



    private fun setupViewListener() {
        binding.closeView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViewState() {
        Glide.with(binding.imageView.context)
            .load(imageUrl)
            .into(binding.imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView).isAppearanceLightStatusBars = true
    }
}