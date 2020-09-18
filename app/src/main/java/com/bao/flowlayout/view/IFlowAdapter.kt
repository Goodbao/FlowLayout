package com.bao.flowlayout.view

import android.view.View
import android.view.ViewGroup
import java.util.*

abstract class IFlowAdapter<VH : IFlowAdapter.FlowViewHolder> {
    abstract class FlowViewHolder protected constructor(val itemView: View)


    companion object {
        /**
         * 默认item类型
         */
        const val ITEM_TYPE_DEFAULT = -1
    }

    private val vhList: MutableList<VH> = ArrayList()

    abstract fun getItemCount(): Int

    abstract fun onCreateViewHolder(parent: ViewGroup, itemType: Int): VH

    abstract fun onBindView(viewHolder: VH, position: Int)

    open fun getItemType(position: Int): Int {
        return ITEM_TYPE_DEFAULT
    }

    fun createViewHolder(flowLayout: FlowLayout, itemType: Int): VH {
        val viewHolder = onCreateViewHolder(flowLayout, itemType)
        vhList.add(viewHolder)
        return viewHolder
    }

    fun getViewHolder(position: Int): VH {
        return vhList[position]
    }

    fun bindView(viewHolder: VH, position: Int) {
        onBindView(viewHolder, position)
    }
}