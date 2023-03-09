package com.antique.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antique.home.R
import com.antique.home.databinding.ListItemPostCategoryWrapperBinding

class PostCategoryWrapperAdapter(
    private val postCategoryListAdapter: PostCategoryListAdapter
) : RecyclerView.Adapter<PostCategoryWrapperAdapter.CategoryWrapperViewHolder>() {

    inner class CategoryWrapperViewHolder(private val binding: ListItemPostCategoryWrapperBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.wrapperView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.wrapperView.adapter = postCategoryListAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryWrapperViewHolder {
        val binding = DataBindingUtil.inflate<ListItemPostCategoryWrapperBinding>(LayoutInflater.from(parent.context), R.layout.list_item_post_category_wrapper, parent, false)
        return CategoryWrapperViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: CategoryWrapperViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE
    }

    companion object {
        const val VIEW_TYPE = 1000
    }
}