package com.antique.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.antique.common.ApiStatus
import com.antique.common.Constant
import com.antique.home.R
import com.antique.home.adapter.CommentListAdapter
import com.antique.home.data.PostUiState
import com.antique.home.databinding.FragmentPostDetailsBinding
import com.antique.home.viewmodel.HomeViewModel
import com.antique.home.viewmodel.HomeViewModelFactory
import com.antique.home.viewmodel.PostDetailsViewModel
import com.antique.home.viewmodel.PostDetailsViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PostDetailsFragment : Fragment() {
    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by navGraphViewModels<HomeViewModel>(R.id.home_nav_graph) { HomeViewModelFactory() }
    private val postDetailsViewModel by lazy { ViewModelProvider(this, PostDetailsViewModelFactory()).get(PostDetailsViewModel::class.java) }

    private val args: PostDetailsFragmentArgs by navArgs()
    private val post by lazy { args.post }

    private lateinit var commentListAdapter: CommentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_details, container, false)
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
                top = insets.systemWindowInsets.top,
                bottom = insets.systemWindowInsets.bottom
            )
            insets
        }
    }

    private fun setupRecyclerView() {
        commentListAdapter = CommentListAdapter()
        binding.commentListView.layoutManager = LinearLayoutManager(requireActivity())
        binding.commentListView.adapter = commentListAdapter
    }

    private fun setupViewState() {
        postDetailsViewModel.getPostDetails(post.postId, post.category)
    }

    private fun setupObservers() {
        postDetailsViewModel.postDetails.observe(viewLifecycleOwner) {
            when(it) {
                is ApiStatus.Success -> {
                    binding.post = it.items
                    commentListAdapter.submitList(it.items.comments)

                    Glide.with(binding.authorProfileImageView.context)
                        .load(it.items.author.profileImage)
                        .into(binding.authorProfileImageView)

                    val uid = Firebase.auth.currentUser?.uid ?: Constant.UNINITIALIZED_VALUE
                    if(it.items.author.uid == uid) {
                        binding.postRemoveView.visibility = View.VISIBLE
                        binding.postReportView.visibility = View.GONE
                    } else {
                        binding.postReportView.visibility = View.VISIBLE
                        binding.postRemoveView.visibility = View.GONE
                    }

                    binding.postDetailLoadView.visibility = View.GONE
                }
                is ApiStatus.Error -> {
                    when(it.exception.message) {
                        Constant.CLASS_CAST_EXCEPTION -> {
                            Snackbar.make(binding.root, getString(R.string.post_not_found_error_text), Snackbar.LENGTH_SHORT).show()
                        }
                        else -> {
                            Snackbar.make(binding.root, getString(R.string.post_loading_error_text), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    binding.postDetailLoadView.visibility = View.GONE
                }
                is ApiStatus.Loading -> {
                    binding.postDetailLoadView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}