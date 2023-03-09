package com.antique.post.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.antique.post.ApiStatus
import com.antique.post.R
import com.antique.post.adapter.CategoryListAdapter
import com.antique.post.databinding.FragmentCategoryBinding
import com.antique.post.viewmodel.PostViewModel
import com.antique.post.viewmodel.PostViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class CategoryFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private val postViewModel by navGraphViewModels<PostViewModel>(R.id.post_nav_graph) { PostViewModelFactory() }
    private lateinit var categoryListAdapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupRecyclerView()
        setupViewState()
        setupObservers()
    }

    private fun setupRecyclerView() {
        categoryListAdapter = CategoryListAdapter {
            postViewModel.bindCategory(it)
            dismiss()
        }

        binding.categoryListView.layoutManager = LinearLayoutManager(requireActivity())
        binding.categoryListView.adapter = categoryListAdapter

    }

    private fun setupViewState() {
        postViewModel.getCategories()
    }

    private fun setupObservers() {
        postViewModel.categories.observe(viewLifecycleOwner) {
            when(it) {
                is ApiStatus.Success -> {
                    categoryListAdapter.submitList(it.items)
                }
                is ApiStatus.Error -> {
                    Snackbar.make(binding.root, getString(R.string.category_loading_error_text), Snackbar.LENGTH_LONG).show()
                }
                is ApiStatus.Loading -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}