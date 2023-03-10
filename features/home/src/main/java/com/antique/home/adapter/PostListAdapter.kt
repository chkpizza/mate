package com.antique.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antique.home.R
import com.antique.home.data.PostUiState
import com.antique.home.databinding.ListItemPostBinding

class PostListAdapter(
    private val onItemClickListener: (PostUiState) -> Unit
) : ListAdapter<PostUiState, PostListAdapter.PostListViewHolder>(diffUtil) {
    inner class PostListViewHolder(private val binding: ListItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostUiState) {
            binding.post = item

            binding.root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostUiState>() {
            override fun areItemsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder {
        val binding = DataBindingUtil.inflate<ListItemPostBinding>(LayoutInflater.from(parent.context), R.layout.list_item_post, parent, false)
        return PostListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}