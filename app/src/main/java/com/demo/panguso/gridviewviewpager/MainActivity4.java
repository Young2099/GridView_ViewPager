package com.demo.panguso.gridviewviewpager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity4 extends AppCompatActivity {
    private String[] titles = {"美食", "电影", "酒店住宿", "休闲娱乐", "外卖", "自助餐", "KTV", "机票/火车票", "周边游", "美甲美睫",
            "火锅", "生日蛋糕", "甜品饮品", "水上乐园", "汽车服务", "美发", "丽人", "景点", "足疗按摩", "运动健身", "健身", "超市", "买菜",
            "今日新单", "小吃快餐", "面膜", "洗浴/汗蒸", "母婴亲子", "生活服务", "婚纱摄影", "学习培训", "家装", "结婚", "全部分配"};
    private ViewPager mPager;
    private List<View> mPagerList;
    private List<Model> mDatas;
    private LinearLayout mIndicatorLayout;
    private LayoutInflater inflater;
    private LinearLayout imageView;
    GridView gridView;
    private int mIndicatorSelected;//指示器图片，被选择状态

    private int mIndicatorUnselected;//指示器图片，未被选择状态
    /**
     * 总的页数
     */
    private int pageCount;
    /**
     * 每一页显示的个数
     */
    private int pageSize = 10;
    /**
     * 当前显示的是第几页
     */

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHEEL && mPagerList.size() > 0) {
                if (!isScrolling) {
                    //当前为非滚动状态，切换到下一页
                    int posttion = (mCurrentPosition + 1) % mPagerList.size();
                    mPager.setCurrentItem(posttion, true);
                }
                releaseTime = System.currentTimeMillis();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delay);
                return;
            }
            if (msg.what == WHEEL_WAIT && mPagerList.size() > 0) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delay);
            }
        }
    };
    ;//每几秒后执行下一张的切换

    private int WHEEL = 100; // 转动

    private int WHEEL_WAIT = 101; // 等待
    private boolean isScrolling = false; // 滚动框是否滚动着

    private boolean isCycle = true; // 是否循环，默认为true

    private boolean isWheel = true; // 是否轮播，默认为true

    private int delay = 4000; // 默认轮播时间

    private int mCurrentPosition = 0; // 轮播当前位置

    private long releaseTime = 0; // 手指松开、页面不滚动时间，防止手机松开后短时间进行切换

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isWheel) {
                long now = System.currentTimeMillis();
                // 检测上一次滑动时间与本次之间是否有触击(手滑动)操作，有的话等待下次轮播
                if (now - releaseTime > delay - 500) {
                    handler.sendEmptyMessage(WHEEL);
                } else {
                    handler.sendEmptyMessage(WHEEL_WAIT);
                }
            }
        }
    };
    private int showPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.ll_dot);

        //初始化数据源
        initDatas();
        initView();
    }


    private void initView() {
        inflater = LayoutInflater.from(this);
        //总的页数=总数/每页数量，并取整
        pageCount = (int) Math.ceil(mDatas.size() * 1.0 / pageSize);
        mPagerList = new ArrayList<>();
        if (isCycle) {
            gridView = (GridView) inflater.inflate(R.layout.girdview, mPager, false);
            gridView.setAdapter(new GridViewAdapter(this, mDatas, pageCount - 1, pageSize));
            mPagerList.add(gridView);
            for (int i = 0; i < pageCount; i++) {
                //每个页面都是inflate出一个新实例
                gridView = (GridView) inflater.inflate(R.layout.girdview, mPager, false);
                gridView.setAdapter(new GridViewAdapter(this, mDatas, i, pageSize));
                mPagerList.add(gridView);
            }
            gridView = (GridView) inflater.inflate(R.layout.girdview, mPager, false);
            gridView.setAdapter(new GridViewAdapter(this, mDatas, 0, pageSize));
            mPagerList.add(gridView);
        }
        if (mPagerList == null || mPagerList.size() == 0) {
            //没有View时隐藏整个布局
            mPager.setVisibility(View.GONE);
            return;
        }

        // 设置指示器
        if (isCycle) {
            mIndicatorLayout.removeAllViews();
            for (int i = 0; i < mPagerList.size() - 2; i++) {
                mIndicatorLayout.addView(inflater.inflate(R.layout.ll_radius, null));
            }
        }
        setIndicator(0);
        mPager.setOffscreenPageLimit(3);
        mPager.setAdapter(new ViewPagerAdapter(mPagerList));
        if (showPosition < 0 || showPosition >= mPagerList.size()) {
            showPosition = 0;
        }
        if (isCycle) {
            showPosition = showPosition + 1;
        }
        setOvalLayout();
        //设置圆点
        mPager.setCurrentItem(showPosition);
        setWheel(true);//设置轮播
    }


    /**
     * 初始化数据源
     */
    private void initDatas() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            mDatas.add(new Model(titles[i]));
        }
    }

    /**
     * 设置圆点
     */
    public void setOvalLayout() {

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                Log.e("TAG", "position" + position);
                int max = mPagerList.size() - 1;
                int t = position;
                mCurrentPosition = position;
                if (isCycle) {
                    if (position == 0) {
                        //滚动到mView的1个（界面上的最后一个），将mCurrentPosition设置为max - 1
                        mCurrentPosition = max - 1;
                    } else if (position == max) {
                        //滚动到mView的最后一个（界面上的第一个），将mCurrentPosition设置为1
                        mCurrentPosition = 1;
                    }
                    t = mCurrentPosition - 1;
                }
                Log.e("TAG", "mCurrentPosition" + t);
                setIndicator(t);

            }

            public void onPageScrolled(int position, float offset, int offsetPixels) {

            }

            public void onPageScrollStateChanged(int arg0) {
                if (arg0 == 1) { // viewPager在滚动
                    isScrolling = true;
                    return;
                } else if (arg0 == 0) { // viewPager滚动结束

                    releaseTime = System.currentTimeMillis();
                    //跳转到第mCurrentPosition个页面（没有动画效果，实际效果页面上没变化）
                    mPager.setCurrentItem(mCurrentPosition, false);
                }
                isScrolling = false;
            }
        });
    }

    public void setWheel(boolean wheel) {
        this.isWheel = wheel;
        isCycle = true;
        if (isWheel) {
            handler.postDelayed(runnable, delay);
        }
    }

    public void setIndicator(int selectedPosition) {
        try {
            for (int i = 0; i < mIndicatorLayout.getChildCount(); i++) {
                mIndicatorLayout.getChildAt(i).findViewById(R.id.v_dot).setBackgroundResource(R.drawable.dot_normal);
            }
            if (mIndicatorLayout.getChildCount() > selectedPosition) {
                mIndicatorLayout.getChildAt(selectedPosition).findViewById(R.id.v_dot).setBackgroundResource(R.drawable.dot_selected);
            }

        } catch (Exception e) {
            Log.i("TAG", "指示器路径不正确");
        }
    }
}
