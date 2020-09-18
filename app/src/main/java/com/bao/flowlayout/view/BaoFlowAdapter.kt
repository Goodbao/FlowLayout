package com.bao.flowlayout.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bao.flowlayout.R
import com.bao.flowlayout.view.IFlowAdapter.FlowViewHolder
import com.google.android.material.button.MaterialButton
import java.util.*

class BaoFlowAdapter(var mData: List<String>) : IFlowAdapter<FlowViewHolder>() {

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): FlowViewHolder {
        return BaoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.flow_item, parent, false)
        )
    }

    override fun onBindView(viewHolder: FlowViewHolder, position: Int) {
        if (viewHolder is BaoViewHolder) {
            viewHolder.button.text = mData[position]
        }
    }

    inner class BaoViewHolder(itemView: View) : FlowViewHolder(itemView) {
        var button: MaterialButton

        init {
            button = itemView.findViewById(R.id.button)
        }
    }
}