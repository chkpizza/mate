package com.antique.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.antique.common.ApiStatus
import com.antique.home.R
import com.antique.home.adapter.PostCategoryListAdapter
import com.antique.home.adapter.PostCategoryWrapperAdapter
import com.antique.home.databinding.FragmentHomeBinding
import com.antique.home.viewmodel.HomeViewModel
import com.antique.home.viewmodel.HomeViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by navGraphViewModels<HomeViewModel>(R.id.home_nav_graph) { HomeViewModelFactory() }
    private lateinit var postCategoryListAdapter: PostCategoryListAdapter
    private lateinit var postCategoryWrapperAdapter: PostCategoryWrapperAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupInsets()
        setupRecyclerView()
        setupViewState()
        setupObservers()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            view.updatePadding(
                top = insets.systemWindowInsets.top
            )
            insets
        }
    }

    private fun setupRecyclerView() {
        postCategoryListAdapter = PostCategoryListAdapter {
            homeViewModel.updateCategory(it)
        }
        postCategoryWrapperAdapter = PostCategoryWrapperAdapter(postCategoryListAdapter)

        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(true)
        }.build()

        concatAdapter = ConcatAdapter(config, postCategoryWrapperAdapter)

        val layoutManager = GridLayoutManager(requireActivity(), 12)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(concatAdapter.getItemViewType(position)) {
                    PostCategoryWrapperAdapter.VIEW_TYPE -> 12
                    else -> 12
                }
            }
        }
        binding.postListView.layoutManager = layoutManager
        binding.postListView.adapter = concatAdapter
    }

    private fun setupViewState() {
        homeViewModel.getCategories()
    }

    private fun setupObservers() {
        homeViewModel.categories.observe(viewLifecycleOwner) {
            when(it) {
                is ApiStatus.Success -> {
                    postCategoryListAdapter.submitList(it.items)
                    homeViewModel.initCategory(it.items.first())
                }
                is ApiStatus.Error -> {
                    Snackbar.make(binding.root, getString(R.string.category_loading_error_text), Snackbar.LENGTH_SHORT).show()
                }
                is ApiStatus.Loading -> {}
            }
        }

        homeViewModel.category.observe(viewLifecycleOwner) {
            postCategoryListAdapter.updateCategory(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}