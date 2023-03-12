package com.antique.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antique.home.R
import com.antique.home.databinding.ListItemImageBinding
import com.bumptech.glide.Glide

class ImageListAdapter(
    private val onItemClickListener: (String) -> Unit
) : ListAdapter<String, ImageListAdapter.ImageListViewHolder>(diffUtil) {

    inner class ImageListViewHolder(private val binding: ListItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.postImageView.context)
                .load(item)
                .into(binding.postImageView)

            binding.root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val binding = DataBindingUtil.inflate<ListItemImageBinding>(LayoutInflater.from(parent.context), R.layout.list_item_image, parent, false)
        return ImageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}