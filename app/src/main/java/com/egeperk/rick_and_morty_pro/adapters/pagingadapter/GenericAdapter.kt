package com.egeperk.rick_and_morty_pro.adapters.pagingadapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egeperk.rick_and_morty_pro.BR

class GenericAdapter<T : Any>(
    private val layoutId: Int,
    private val onItemClick: (Int) -> Unit,
) : PagingDataAdapter<T, GenericAdapter.GenericViewHolder<T>>(BaseItemCallback()) {

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        holder.bind(getItem(position), onItemClick,position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), viewType, parent, false)
        return GenericViewHolder(binding)

    }

    override fun getItemViewType(position: Int): Int = layoutId

    class GenericViewHolder<T>(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: Any?, onItemClick: (Int) -> Unit,position: Int) {
            binding.setVariable(BR.item, obj)
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onItemClick(position)
            }
        }
    }

    class BaseItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) =
            oldItem.toString() == newItem.toString()

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    }
}