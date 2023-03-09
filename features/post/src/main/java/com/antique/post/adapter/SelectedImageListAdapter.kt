package com.antique.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antique.post.R
import com.antique.post.databinding.ListItemSelectedImageBinding
import com.bumptech.glide.Glide

class SelectedImageListAdapter : ListAdapter<String, SelectedImageListAdapter.SelectedImageListViewHolder>(diffUtil) {
    inner class SelectedImageListViewHolder(private val binding: ListItemSelectedImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.selectedImageView.context)
                .load(item)
                .into(binding.selectedImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageListViewHolder {
        val binding = DataBindingUtil.inflate<ListItemSelectedImageBinding>(LayoutInflater.from(parent.context), R.layout.list_item_selected_image, parent, false)
        return SelectedImageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedImageListViewHolder, position: Int) {
        holder.bind(getItem(position))
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

}