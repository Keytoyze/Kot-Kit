/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.databinding.StreamItemBinding
import team.henrykey.minitiktok.extension.inflateDataBinding
import team.henrykey.minitiktok.model.Video

class StreamAdapter(
    private var mData: List<Video>
) : RecyclerView.Adapter<StreamAdapter.StreamViewHolder>() {

    private lateinit var mContext: Context

    class StreamViewHolder(
        val fieldItemBinding: StreamItemBinding
    ) : RecyclerView.ViewHolder(fieldItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        mContext = parent.context
        return StreamViewHolder(parent.inflateDataBinding(R.layout.stream_item))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        holder.fieldItemBinding.run {
            model = mData[position]
        }
    }

    fun setList(newList: List<Video>) {
        val oldList = mData
        mData = newList
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }).dispatchUpdatesTo(this)
    }
}