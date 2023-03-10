package com.antique.post.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.antique.common.ApiStatus
import com.antique.common.EventObserver
import com.antique.post.R
import com.antique.post.adapter.SelectedImageListAdapter
import com.antique.post.databinding.FragmentPostBinding
import com.antique.post.viewmodel.PostViewModel
import com.antique.post.viewmodel.PostViewModelFactory
import com.google.android.material.snackbar.Snackbar

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val postViewModel by navGraphViewModels<PostViewModel>(R.id.post_nav_graph) { PostViewModelFactory() }
    private lateinit var registerPostMenuItem: MenuItem
    private lateinit var selectedImageListAdapter: SelectedImageListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = postViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupInsets()
        setupToolbar()
        setupRecyclerView()
        setupViewListener()
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

    private fun setupToolbar() {
        binding.postToolbarView.inflateMenu(R.menu.post_toolbar_menu)
        registerPostMenuItem = binding.postToolbarView.menu.findItem(R.id.register_post)
        registerPostMenuItem.isEnabled = false

        binding.postToolbarView.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.register_post -> {
                    postViewModel.registerPost()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        selectedImageListAdapter = SelectedImageListAdapter()
        binding.selectedImageListView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.selectedImageListView.adapter = selectedImageListAdapter
    }

    private fun setupViewListener() {
        binding.postCategoryView.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_categoryFragment)
        }

        binding.selectImageView.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_galleryFragment)
        }

        binding.postDetailInputView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    postViewModel.bindDetails(it.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        fun validate(): Boolean {
            return postViewModel.selectedCategory.value != "카테고리를 선택해주세요" && !postViewModel.details.value.isNullOrEmpty()
        }

        postViewModel.selectedCategory.observe(viewLifecycleOwner) {
            registerPostMenuItem.isEnabled = validate()
        }

        postViewModel.details.observe(viewLifecycleOwner) {
            registerPostMenuItem.isEnabled = validate()
        }

        postViewModel.selectedImages.observe(viewLifecycleOwner) {
            selectedImageListAdapter.submitList(it)
        }

        postViewModel.registerState.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                is ApiStatus.Success -> {
                    binding.postProgressView.visibility = View.GONE
                    binding.postDetailInputView.text.clear()
                    postViewModel.bindImages(emptyList())
                    postViewModel.bindCategory("카테고리를 선택해주세요")
                    Snackbar.make(binding.root, getString(R.string.register_post_success_text), Snackbar.LENGTH_SHORT).show()
                }
                is ApiStatus.Error -> {
                    binding.postProgressView.visibility = View.GONE
                    Snackbar.make(binding.root, getString(R.string.register_post_error_text), Snackbar.LENGTH_SHORT).show()
                }
                is ApiStatus.Loading -> {
                    binding.postProgressView.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}