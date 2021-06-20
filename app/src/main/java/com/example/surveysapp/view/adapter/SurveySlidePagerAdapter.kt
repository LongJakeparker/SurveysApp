package com.example.surveysapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.surveysapp.databinding.ItemSurveyCoverBinding
import com.example.surveysapp.model.Survey

/**
 * @author longtran
 * @since 14/06/2021
 */
class SurveySlidePagerAdapter : ListAdapter<
        Survey,
        RecyclerView.ViewHolder
        >(IssueDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemSurveyCoverBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IssueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as IssueViewHolder).onBind(getItem(position))
    }

    class IssueViewHolder(val binding: ItemSurveyCoverBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: Survey) {
            binding.apply {
                survey = item
                executePendingBindings()
            }
        }

    }

    /**
     * DiffUtil.ItemCallback of Survey
     */
    class IssueDiffUtil : DiffUtil.ItemCallback<Survey>() {

        override fun areItemsTheSame(oldItem: Survey, newItem: Survey): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Survey, newItem: Survey): Boolean {
            return oldItem == newItem
        }

    }
}