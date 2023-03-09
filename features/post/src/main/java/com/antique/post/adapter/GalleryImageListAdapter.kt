package com.antique.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antique.post.R
import com.antique.post.databinding.ListItemGalleryImageBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class GalleryImageListAdapter(
    private val onItemClickListener: (Int) -> Unit
) : ListAdapter<String, GalleryImageListAdapter.GalleryImageListViewHolder>(diffUtil) {
    private val selectedImages = mutableListOf<String>()

    inner class GalleryImageListViewHolder(private val binding: ListItemGalleryImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.galleryImageView.context)
                .load(item)
                .into(binding.galleryImageView)

            if(selectedImages.contains(item)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.orange))
            } else {
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }

            binding.root.setOnClickListener {
                if(selectedImages.contains(item)) {
                    selectedImages.remove(item)
                    binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    onItemClickListener(selectedImages.size)
                } else {
                    if(selectedImages.size < 3) {
                        binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.orange))
                        selectedImages.add(item)
                        onItemClickListener(selectedImages.size)
                    } else {
                        Snackbar.make(binding.root, "3개까지 선택할 수 있습니다", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryImageListViewHolder {
        val binding = DataBindingUtil.inflate<ListItemGalleryImageBinding>(LayoutInflater.from(parent.context), R.layout.list_item_gallery_image, parent, false)
        return GalleryImageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryImageListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedImages() = selectedImages

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }


}