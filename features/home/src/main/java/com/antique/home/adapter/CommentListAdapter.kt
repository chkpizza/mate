package com.antique.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antique.home.R
import com.antique.home.data.CommentUiState
import com.antique.home.databinding.ListItemCommentBinding

class CommentListAdapter : ListAdapter<CommentUiState, CommentListAdapter.CommentListViewHolder>(diffUtil) {

    inner class CommentListViewHolder(private val binding: ListItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentUiState) {
            binding.comment = item
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CommentUiState>() {
            override fun areItemsTheSame(oldItem: CommentUiState, newItem: CommentUiState): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CommentUiState, newItem: CommentUiState): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder {
        val binding = DataBindingUtil.inflate<ListItemCommentBinding>(LayoutInflater.from(parent.context), R.layout.list_item_comment, parent, false)
        return CommentListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}