package com.antique.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.antique.common.ApiStatus
import com.antique.common.EventObserver
import com.antique.home.R
import com.antique.home.adapter.PostCategoryListAdapter
import com.antique.home.adapter.PostCategoryWrapperAdapter
import com.antique.home.adapter.PostListAdapter
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
    private lateinit var postListAdapter: PostListAdapter
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
        setupViewListener()
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
        postListAdapter = PostListAdapter {
            val action = HomeFragmentDirections.actionHomeFragmentToPostDetailsFragment(it)
            findNavController().navigate(action)
        }
        postCategoryListAdapter = PostCategoryListAdapter {
            homeViewModel.getNewCategoryPosts(it)
        }
        postCategoryWrapperAdapter = PostCategoryWrapperAdapter(postCategoryListAdapter)

        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(true)
        }.build()

        concatAdapter = ConcatAdapter(config, postCategoryWrapperAdapter, postListAdapter)

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

    private fun setupViewListener() {
        binding.swipeRefreshView.setOnRefreshListener {
            homeViewModel.refreshPosts()
        }

        binding.postListView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (binding.postListView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val totalItemCount = binding.postListView.adapter?.itemCount ?: -1

                if(lastVisibleItemPosition > 0 && !recyclerView.canScrollVertically(1)) {
                    homeViewModel.getMorePosts()
                }
            }
        })
    }

    private fun setupViewState() {
        homeViewModel.initialize()
    }

    private fun setupObservers() {
        homeViewModel.categories.observe(viewLifecycleOwner) {
            when(it) {
                is ApiStatus.Success -> {
                    postCategoryListAdapter.submitList(it.items)
                }
                is ApiStatus.Error -> {
                    Snackbar.make(binding.root, getString(R.string.category_loading_error_text), Snackbar.LENGTH_SHORT).show()
                }
                is ApiStatus.Loading -> {}
            }
        }

        homeViewModel.posts.observe(viewLifecycleOwner) {
            it?.let {
                when(it) {
                    is ApiStatus.Success -> {
                        postListAdapter.submitList(it.items)
                    }
                    is ApiStatus.Error -> {
                        Snackbar.make(binding.root, getString(R.string.post_loading_error_text), Snackbar.LENGTH_SHORT).show()
                    }
                    is ApiStatus.Loading -> {}
                }
            }
        }

        homeViewModel.category.observe(viewLifecycleOwner) {
            postCategoryListAdapter.updateCategory(it)
        }

        homeViewModel.isRefresh.observe(viewLifecycleOwner, EventObserver {
            binding.swipeRefreshView.isRefreshing = it
        })

        homeViewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            binding.loadingProgressView.isVisible = it
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}