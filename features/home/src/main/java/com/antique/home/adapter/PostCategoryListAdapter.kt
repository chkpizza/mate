package com.antique.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antique.home.R
import com.antique.home.databinding.ListItemPostCategoryBinding

class PostCategoryListAdapter(
    private val onItemClickListener: (String) -> Unit
) : ListAdapter<String, PostCategoryListAdapter.CategoryListViewHolder>(diffUtil) {
    private var selectedCategory = ""

    inner class CategoryListViewHolder(private val binding: ListItemPostCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.category = item

            if(selectedCategory == item) {
                binding.categoryView.setTextColor(binding.root.context.resources.getColor(R.color.white, null))
                binding.categoryView.setBackgroundResource(R.drawable.shape_rect_4dp_purple)
            } else {
                binding.categoryView.setTextColor(binding.root.context.resources.getColor(R.color.gray_9d, null))
                binding.categoryView.setBackgroundResource(R.drawable.shape_rect_4dp_gray_e9)
            }

            binding.root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE
    }

    companion object {
        const val VIEW_TYPE = 1001
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = DataBindingUtil.inflate<ListItemPostCategoryBinding>(LayoutInflater.from(parent.context), R.layout.list_item_post_category, parent, false)
        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateCategory(category: String) {
        selectedCategory = category
        notifyDataSetChanged()
    }
}