/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.databinding.StreamStaggeredItemBinding
import team.henrykey.minitiktok.extension.inflateDataBinding
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.view.ui.VideoActivity

class StaggeredAdapter(
    private var mData: List<Video>
) : RecyclerView.Adapter<StaggeredAdapter.StaggeredViewHolder>() {

    private lateinit var mContext: Context

    class StaggeredViewHolder(
        val fieldItemBinding: StreamStaggeredItemBinding
    ) : RecyclerView.ViewHolder(fieldItemBinding.root) {
        val imageView: SimpleDraweeView = fieldItemBinding.root.findViewById(R.id.coverView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredViewHolder {
        mContext = parent.context
        return StaggeredViewHolder(parent.inflateDataBinding(R.layout.stream_staggered_item))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: StaggeredViewHolder, position: Int) {
        holder.fieldItemBinding.run {
            holder.imageView.post {
                val current = mData[position]
                holder.imageView.run {
                    val newHeight = width / current.imageWidth * current.imageHeight
                    updateLayoutParams {
                        height = newHeight.toInt()
                    }
                }
                model = current
            }
            root.setOnClickListener {
                mContext.startActivity(VideoActivity.newIntent(mContext, mData[position]))
            }
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