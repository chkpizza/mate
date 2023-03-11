package com.antique.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antique.common.User
import com.antique.home.R
import com.antique.home.data.CommentUiState
import com.antique.home.databinding.ListItemCommentBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CommentListAdapter(
) : ListAdapter<CommentUiState, CommentListAdapter.CommentListViewHolder>(diffUtil) {
    lateinit var onRemoveComment: (CommentUiState) -> Unit
    lateinit var onReportComment: (CommentUiState) -> Unit

    inner class CommentListViewHolder(private val binding: ListItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentUiState) {
            binding.comment = item

            val uid = Firebase.auth.currentUser?.uid.toString()
            if(item.author.uid == uid) {
                binding.commentRemoveView.visibility = View.VISIBLE
                binding.reportView.visibility = View.GONE
                binding.commentRemoveView.setOnClickListener {
                    onRemoveComment(item)
                }
            } else {
                binding.commentRemoveView.visibility = View.GONE
                binding.reportView.visibility = View.VISIBLE
                binding.reportView.setOnClickListener {
                    onReportComment(item)
                }
            }

            Glide.with(binding.commentAuthorProfileImageView.context)
                .load(item.author.profileImage)
                .into(binding.commentAuthorProfileImageView)
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