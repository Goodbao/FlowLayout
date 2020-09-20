package com.bao.flowlayout.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.bao.flowlayout.R
import com.bao.flowlayout.view.IFlowAdapter.FlowViewHolder

class FlowLayout : ViewGroup {
    /**
     * 所有子View
     */
    private var allViewList: MutableList<List<View>> = ArrayList()

    /**
     * 每一行的高度
     */
    private var lineHeightList: MutableList<Int> = ArrayList()

    /**
     * item水平间距
     */
    private var horizontalSpace: Int = dp2px(6)

    /**
     * item垂直方向间距
     */
    private var verticalSpace: Int = dp2px(6)

    constructor(
        context: Context?
    ) : super(context) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initAttr(attrs!!, 0)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initAttr(attrs!!, defStyleAttr)
    }

    /**
     * 初始化xml设置的参数
     */
    private fun initAttr(attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyleAttr, 0)

        for (i in 0 until typeArray.indexCount) {
            val attr = typeArray.getIndex(i)

            when (attr) {
                R.styleable.FlowLayout_child_horizontal_space -> {
                    horizontalSpace = typeArray.getDimensionPixelSize(
                        attr,
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            0f,
                            resources.displayMetrics
                        ).toInt()
                    )
                }
                R.styleable.FlowLayout_child_vertical_space -> {
                    verticalSpace = typeArray.getDimensionPixelSize(
                        attr,
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            0f,
                            resources.displayMetrics
                        ).toInt()
                    )
                }
            }
        }
    }


    private lateinit var flowAdapter: IFlowAdapter<FlowViewHolder>

    /**
     * 利用adapter填充view
     */
    fun setAdapter(adapter: IFlowAdapter<FlowViewHolder>) {
        flowAdapter = adapter
        for (i in 0 until flowAdapter.getItemCount()) {
            val viewHolder: FlowViewHolder =
                flowAdapter.createViewHolder(this, adapter.getItemType(i))

            adapter.bindView(viewHolder, i)
            val itemView = adapter.getViewHolder(i).itemView

            addView(itemView)
        }
    }

    /**
     * 每次重新度量，都要清空
     */
    private fun clearMeasureParams() {
        allViewList.clear()
        lineHeightList.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        clearMeasureParams()

        //父布局的宽高
        var viewGroupWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewGroupHeight = MeasureSpec.getSize(heightMeasureSpec)

        //子View需要父控件的大小
        var viewGroupNeedWidth = 0
        var viewGroupNeedHeight = 0

        //每一行的子View
        var lineViews: MutableList<View> = ArrayList()
        //每一行已用的宽度
        var lineUseWidth = 0
        //每一行已用的高度
        var lineUseHeight = 0

        //遍历所有子view
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            //子view可见
            if (childView.visibility == View.VISIBLE) {
                //获取子View的
                val layoutParams = childView.layoutParams

                //子view的measureSpec,传入父布局的measureSpec,还有父布局设置的内边距，子view的大小（大于0就是确定的大小，-1是match_parent,-2是wrap_content）
                val childWidthMeasureSpec = getChildMeasureSpec(
                    widthMeasureSpec,
                    paddingLeft + paddingRight,
                    layoutParams.width
                )
                val childHeightMeasureSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom,
                    layoutParams.height
                )

                //子view调用了measure才能得出确切的宽高
                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)

                //换行
                if (lineUseWidth + childView.measuredWidth + paddingLeft + paddingRight > viewGroupWidth) {
                    //保存每一行view
                    allViewList.add(lineViews)
                    lineHeightList.add(lineUseHeight)

                    viewGroupNeedWidth = Math.max(viewGroupNeedWidth, lineUseWidth)
                    viewGroupNeedHeight += lineUseHeight + verticalSpace

                    lineViews = ArrayList()
                    lineUseWidth = 0;
                    lineUseHeight = 0;
                }

                //每一行保存子view
                lineViews.add(childView)
                //每一行宽度
                lineUseWidth += childView.measuredWidth + horizontalSpace
                //每一行高度
                lineUseHeight = Math.max(lineUseHeight, childView.measuredHeight)


                //最后一行特殊处理
                if (i == childCount - 1) {
                    allViewList.add(lineViews)
                    lineHeightList.add(lineUseHeight)

                    viewGroupNeedWidth = Math.max(viewGroupNeedWidth, lineUseWidth)
                    viewGroupNeedHeight += lineUseHeight + verticalSpace
                }
            }
        }

        //再测量ViewGroup
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        //真正需要的宽高，
        //如果ViewGroup的测量模式是 MeasureSpec.EXACTLY ，就用自己确定的大小，否则用子view测量大小
        //需要加水padding，才是真正需要的大小
        val realWidth =
            if (widthMode == MeasureSpec.EXACTLY)
                viewGroupWidth
            else
                viewGroupNeedWidth + paddingLeft + paddingRight

        val realHeight =
            if (heightMode == MeasureSpec.EXACTLY)
                viewGroupHeight
            else
                viewGroupNeedHeight + paddingTop + paddingBottom

        setMeasuredDimension(realWidth, realHeight)

    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {

        val lineCount = allViewList.size

        //子view布局的位置从ViewGroup内边距的左上角开始
        var curLeft = paddingLeft
        var curTop = paddingTop

        for (i in 0 until lineCount) {
            //把每一行子view拿出来排
            val lineViews = allViewList.get(i)
            for (childView in lineViews) {
                val left = curLeft
                val top = curTop
                val right = left + childView.measuredWidth
                val bottom = top + childView.measuredHeight

                childView.layout(left, top, right, bottom)
                curLeft = right + horizontalSpace
            }
            //换行
            val lineHeight = lineHeightList.get(i)
            curTop += lineHeight + verticalSpace
            curLeft = paddingLeft
        }
    }

    /**
     * dp转px
     */
    fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}