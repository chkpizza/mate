package com.antique.post.view

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.antique.post.R
import com.antique.post.adapter.GalleryImageListAdapter
import com.antique.post.databinding.FragmentGalleryBinding
import com.antique.post.viewmodel.PostViewModel
import com.antique.post.viewmodel.PostViewModelFactory

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val postViewModel by navGraphViewModels<PostViewModel>(R.id.post_nav_graph) { PostViewModelFactory() }
    private lateinit var selectImageMenuItem: MenuItem
    private lateinit var galleryImageListAdapter: GalleryImageListAdapter

    private val storagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            loadImages()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
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
        setupPermissions()
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
        binding.selectImageToolbarView.inflateMenu(R.menu.gallery_toolbar_menu)
        selectImageMenuItem = binding.selectImageToolbarView.menu.findItem(R.id.select_image)
        selectImageMenuItem.isEnabled = false

        binding.selectImageToolbarView.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.select_image -> {
                    postViewModel.bindImages(galleryImageListAdapter.getSelectedImages())
                    findNavController().navigateUp()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        galleryImageListAdapter = GalleryImageListAdapter {
            selectImageMenuItem.isEnabled = it > 0
        }
        binding.imageListView.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.imageListView.adapter = galleryImageListAdapter
    }

    private fun setupPermissions() {
        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadImages()
        } else {
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadImages() {
        val collection = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = requireActivity().contentResolver.query(
            collection,
            projection,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        val uris = mutableListOf<String>()

        cursor?.let {
            val columnIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while(cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(columnIdx))
                uris.add(uri.toString())
            }
            it.close()
        }

        galleryImageListAdapter.submitList(uris)
    }
}