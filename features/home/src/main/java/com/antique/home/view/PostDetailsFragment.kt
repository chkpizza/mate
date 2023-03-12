package com.antique.home.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.antique.common.*
import com.antique.home.OnReportClickListener
import com.antique.home.R
import com.antique.home.adapter.CommentListAdapter
import com.antique.home.adapter.ImageListAdapter
import com.antique.home.data.CommentUiState
import com.antique.home.databinding.FragmentPostDetailsBinding
import com.antique.home.viewmodel.HomeViewModel
import com.antique.home.viewmodel.HomeViewModelFactory
import com.antique.home.viewmodel.PostDetailsViewModel
import com.antique.home.viewmodel.PostDetailsViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostDetailsFragment() : Fragment() {
    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by navGraphViewModels<HomeViewModel>(R.id.home_nav_graph) { HomeViewModelFactory() }
    private val postDetailsViewModel by lazy { ViewModelProvider(this, PostDetailsViewModelFactory()).get(PostDetailsViewModel::class.java) }

    private val args: PostDetailsFragmentArgs by navArgs()
    private val post by lazy { args.post }

    private lateinit var commentListAdapter: CommentListAdapter
    private lateinit var imageListAdapter: ImageListAdapter

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
        setupViewListener()
        setupObservers()
    }

    /*
    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            view.updatePadding(
                top = insets.systemWindowInsets.top,
                bottom = insets.systemWindowInsets.bottom
            )
            insets
        }
    }

     */
    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, ViewInsetsCallback(insetTypes = WindowInsetsCompat.Type.systemBars(), insetTypes2 = WindowInsetsCompat.Type.ime()))
    }

    private fun setupRecyclerView() {
        commentListAdapter = CommentListAdapter()
        commentListAdapter.onRemoveComment = {
            postDetailsViewModel.removeComment(it)
        }
        commentListAdapter.onReportComment = {
            val reportDialog = ReportCommentFragment(object : OnReportClickListener {
                override fun onClick(position: Int) {
                    when(position) {
                        1 -> postDetailsViewModel.reportComment(it)
                        2 -> {
                            AlertDialog.Builder(requireActivity())
                                .setTitle("사용자를 신고하시겠습니까?")
                                .setMessage("신고된 사용자는 차단되어 게시글과 댓글이 숨겨지고\n차단은 취소할 수 없습니다")
                                .setNegativeButton("취소", null)
                                .setPositiveButton("확인") { _, _ ->
                                    postDetailsViewModel.blockUser(it.author.uid)
                                }.create().show()
                        }
                    }
                }
            })
            reportDialog.show(requireActivity().supportFragmentManager, null)
        }

        binding.commentListView.layoutManager = LinearLayoutManager(requireActivity())
        binding.commentListView.adapter = commentListAdapter

        imageListAdapter = ImageListAdapter {
            val action = PostDetailsFragmentDirections.actionPostDetailsFragmentToFullScreenFragment(it)
            findNavController().navigate(action)
        }
        binding.postImageListView.adapter = imageListAdapter
    }

    private fun setupViewListener() {
        binding.commentSubmitButton.setOnClickListener {
            postDetailsViewModel.registerComment(binding.commentInputView.text.toString())
        }

        binding.commentInputView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    binding.commentSubmitButton.isEnabled = it.isNotEmpty()
                }
            }
        })

        binding.postRemoveView.setOnClickListener {
            postDetailsViewModel.removePost()
        }

        binding.postReportView.setOnClickListener {
            val reportDialog = ReportPostFragment(object : OnReportClickListener {
                override fun onClick(position: Int) {
                    when(position) {
                        1 -> postDetailsViewModel.reportPost()
                        2 -> {
                            AlertDialog.Builder(requireActivity())
                                .setTitle("사용자를 신고하시겠습니까?")
                                .setMessage("신고된 사용자는 차단되어 게시글과 댓글이 숨겨지고\n차단은 취소할 수 없습니다")
                                .setNegativeButton("취소", null)
                                .setPositiveButton("확인") { _, _ ->
                                    postDetailsViewModel.blockUser(postDetailsViewModel.postOverview.author)
                                }.create().show()
                        }
                    }
                }
            })
            reportDialog.show(requireActivity().supportFragmentManager, null)
        }

        binding.postImageListView.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                postDetailsViewModel.imagePosition = position
            }
        })
    }

    private fun setupViewState() {
        postDetailsViewModel.getPostDetails(post.postId, post.category)
    }

    private fun setupObservers() {
        postDetailsViewModel.postDetails.observe(viewLifecycleOwner) {
            when(it) {
                is ApiStatus.Success -> {
                    binding.post = it.items

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

                    if(it.items.images.isNotEmpty()) {
                        imageListAdapter.submitList(it.items.images)
                        binding.postImageListView.visibility = View.VISIBLE
                        binding.postImageListView.setCurrentItem(postDetailsViewModel.imagePosition, false)
                        if(it.items.images.size > 1) {
                            binding.postImageListIndicatorView.visibility = View.VISIBLE
                            binding.postImageListIndicatorView.attachTo(binding.postImageListView)
                        } else {
                            binding.postImageListIndicatorView.visibility = View.GONE
                        }
                    } else {
                        binding.postImageListView.visibility = View.GONE
                    }
                }
                is ApiStatus.Error -> {
                    when(it.exception.message) {
                        Constant.CLASS_CAST_EXCEPTION -> Snackbar.make(binding.root, getString(R.string.post_not_found_error_text), Snackbar.LENGTH_SHORT).show()
                    }
                }

                is ApiStatus.Loading -> {}
            }
        }

        postDetailsViewModel.comments.observe(viewLifecycleOwner) {
            when(it) {
                is ApiStatus.Success -> {
                    commentListAdapter.submitList(it.items)
                    binding.commentInputView.text.clear()
                    (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { imm ->
                        imm.hideSoftInputFromWindow(binding.commentInputView.windowToken, 0)
                    }
                }
                is ApiStatus.Error -> {}
                is ApiStatus.Loading -> {}
            }
        }

        postDetailsViewModel.removePostState.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                true -> {
                    homeViewModel.clear()
                    findNavController().navigateUp()
                }
                false -> Snackbar.make(binding.root, getString(R.string.post_remove_error_text), Snackbar.LENGTH_SHORT).show()
            }
        })

        postDetailsViewModel.reportPostStatus.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                true -> {
                    Snackbar.make(binding.root, getString(R.string.post_report_success_text), Snackbar.LENGTH_SHORT).show()
                }
                false -> {
                    Snackbar.make(binding.root, getString(R.string.post_report_failure_text), Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        postDetailsViewModel.reportCommentState.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                true -> {
                    Snackbar.make(binding.root, getString(R.string.comment_report_success_text), Snackbar.LENGTH_SHORT).show()
                }
                false -> {
                    Snackbar.make(binding.root, getString(R.string.comment_report_failure_text), Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        postDetailsViewModel.blockUserState.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                true -> {
                    Toast.makeText(requireActivity(), getString(R.string.block_user_success_text), Toast.LENGTH_SHORT).show()
                }
                false -> {
                    Toast.makeText(requireActivity(), getString(R.string.block_user_failure_text), Toast.LENGTH_SHORT).show()
                }
            }
        })

        postDetailsViewModel.finish.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                true -> {
                    homeViewModel.clear()
                    findNavController().navigateUp()
                }
                false -> {}
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}