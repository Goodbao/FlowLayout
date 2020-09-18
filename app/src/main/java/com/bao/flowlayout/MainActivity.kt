package com.bao.flowlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bao.flowlayout.view.BaoFlowAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mData :MutableList<String> = ArrayList()
    private lateinit var adapter:BaoFlowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mData.add("aaaaa")
        mData.add("bbbbbbbbb")
        mData.add("aaaaa")
        mData.add("aaaaacccccccc")
        mData.add("aaaaa")
        mData.add("aaaaadddddddddddd")
        mData.add("aaaaa")
        mData.add("11111111111111111111")
        mData.add("22222222222222222222222222222222")
        mData.add("222222222222")

        adapter = BaoFlowAdapter(mData)

        flowLayout.setAdapter(adapter)

    }
}