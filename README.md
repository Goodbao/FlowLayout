# FlowLayout
- **[Android View的测量](https://www.jianshu.com/p/7e4accd25602)** 最好先了解一下
- **[Android view的测绘练习-流式布局-FlowLayout](https://www.jianshu.com/p/59ad0d7612eb)**

- 这次要实现的功能先看一下
![FlowLayout](https://upload-images.jianshu.io/upload_images/1627327-9106741de5719d4d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 1.流式布局分析
#### 1.每一行的宽高
- 换行条件就是，已使用的宽度 + 当前子view的宽度 + 子view之间的间隔 + FlowLayout的 paddingLeft + paddingRight,如果这个值大于FlowLayout的宽度，那就需要另起一行，再addView
![image.png](https://upload-images.jianshu.io/upload_images/1627327-35611069560a9130.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 每一行需要的高度就是所有子view的高度最大值
- 当换行时，高度的值就是已使用的高度 + 这一行需要的高度 ，高度用List<Integer>保存，后面onLayout布置view的位置
- 子view用List<List<View>>来保存，就是二维数组，用于后面onLayout使用

#### 2.子view测量
- 每个子view拿到自己的LayoutParams，加上FlowLayout的MeasureSpec
- 通过getChildMeasureSpec 方法获取到子view的MeasureSpec
- 子view再调用 childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
- 最后childView.measuredWidth就是子view测量过的实际宽高

#### 3.FlowLayout的大小（onMeasure）
![image.png](https://upload-images.jianshu.io/upload_images/1627327-5e836e9035fd002b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 这里黑色代表Activity,最外层的ViewGroup
- 红色代表FlowLayout,如果他的测量模式是EXACTLY，精确模式，那他的宽度就是自己的MeasureSpec.getSize()方法的大小，可能是match_parent，也可能是写的具体dp，子view大小不关心；如果不是，那就要受到子view大小影响，就要先测量子View加起来需要多少空间，再把需要的空间大小赋值。前面相当于给你两百平米的房子，里面怎么分小房间，都不会超过两百平，而后面一种是小房间加起来，最后你需要多大，再给多大的空间。
- 蓝色代表子View ,子View的宽高影响自己的排列，如果宽度达到了FlowLayout的最大值，就需要换行了。


#### 4.布局位置（onLayout）
- 布局位置起始点在FlowLayout左上角，但要算上paddingTop 和 paddingLeft
- 利用在onMeasure时计算的子view大小，循环将子view放到指定的位置



