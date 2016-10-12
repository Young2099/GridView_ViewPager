package com.demo.panguso.gridviewviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ${yangfang} on 2016/10/12.
 */

public class KeyWordViewPager extends FrameLayout implements ViewPager.OnPageChangeListener {
    //立即滑动
    private static final int WHELL = 1;
    //等待
    private static final int WHELL_WAIT = 2;

    private LayoutInflater inflater;
    private Context mContext;
    private ViewPager mViewPager;
    private LinearLayout mIndicatorLayout;
    private GridView mGridView;

    //viewpager显示的页面的Hige
    private List<View> mPagerViewList = new ArrayList<>();

    private List<Model> mDatas = new ArrayList<>();
    private GridViewAdapter mAdapter;
    private String[] titles;//模拟数据
    //item的监听
    private AdapterView.OnItemClickListener OnItemOnClickListener;

    //总的页数
    private int mPagerCount;
    //每一页显示的个数
    private int mPagerSize = 6;
    //当前的页面位置
    private int mCurrentPosition = 0;
    //显示的位置
    private int showPosition;
    //是否轮播开始
    private boolean isCycle = true;
    //是否在滑动,自动
    private boolean isSrolling = false;
    //手指松开，页面不滚动时间，防止手指松开后短时间进行切换
    private long releaseTime = 0;
    //是否轮播
    private boolean isWheel = true;
    //延播时间
    private long delayTime = 4000;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHELL && mPagerViewList.size() > 0) {
                if (!isSrolling) {
                    //处于自动轮播，切换到下一页
                    int position = (mCurrentPosition + 1) % mPagerCount;
                    mViewPager.setCurrentItem(position, true);
                }
                releaseTime = SystemClock.currentThreadTimeMillis();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            }
            if (msg.what == WHELL_WAIT && mPagerViewList.size() > 0) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isWheel) {
                long nowTime = SystemClock.currentThreadTimeMillis();
                //检测上一次滑动时间与本次之间是否有触击（手滑动）
                if (nowTime - releaseTime > delayTime - 500) {
                    handler.sendEmptyMessage(WHELL);
                } else {
                    handler.sendEmptyMessage(WHELL_WAIT);
                }
            }
        }
    };

    public KeyWordViewPager(Context context) {
        this(context, null);
    }

    public KeyWordViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public KeyWordViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = getContext();
        initView();

    }

    private void initView() {
        inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_keyword_viewpager, null);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mIndicatorLayout = (LinearLayout)view.findViewById(R.id.ll_dot);

    }

    private void initData(List<Model> titles) {
        this.mDatas = titles;
        initViewPager();
        initIndicator();
    }

    private void initViewPager() {
        mPagerCount = (int) Math.ceil(mDatas.size() * 1.0 / mPagerSize);

        //防止热搜词不够显示2页的时候
        if (isCycle) {
            mGridView = (GridView) inflater.inflate(R.layout.girdview, mViewPager, false);
            mGridView.setAdapter(new GridViewAdapter(mContext, mDatas, mPagerCount - 1, mPagerSize));
            mPagerViewList.add(mGridView);
            for (int i = 0; i < mPagerCount; i++) {
                //每个页面都是inflate出一个新实例
                mGridView = (GridView) inflater.inflate(R.layout.girdview, mViewPager, false);
                mGridView.setAdapter(new GridViewAdapter(mContext, mDatas, i, mPagerSize));
                mPagerViewList.add(mGridView);
            }
            mGridView = (GridView) inflater.inflate(R.layout.girdview, mViewPager, false);
            mGridView.setAdapter(new GridViewAdapter(mContext, mDatas, 0, mPagerSize));
            mPagerViewList.add(mGridView);
        }

    }

    /**
     * 显示底下圆点指示器
     */
    private void initIndicator() {
        if (isCycle) {
            for (int i = 0; i < mPagerViewList.size() - 2; i++) {
                mIndicatorLayout.addView(inflater.inflate(R.layout.ll_radius, null));
            }
        }
        //默认显示第一页
        setIndicator(0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new ViewPagerAdapter(mPagerViewList));
        //将设置的多的两个页面强制转换
        if (showPosition < 0 || showPosition >= mPagerViewList.size()) {
            showPosition = 0;
        }
        if (isCycle) {
            showPosition = showPosition + 1;
        }
        mViewPager.setCurrentItem(showPosition);
        setWheelView(true);//设置循环轮播
    }

    private void setWheelView(boolean b) {
        this.isWheel = b;
        isCycle = true;
        if (isWheel) {
            handler.postDelayed(runnable, delayTime);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        int max = mPagerViewList.size() - 1;//最后一个位置
        int choiceId = position;
        mCurrentPosition = position;
        if (isCycle) {
            if (position == 0) {
                //向左滑动如果position是0，则设置到最后一个位置
                mCurrentPosition = max - 1;
            } else if (position == max) {//最后一个
                mCurrentPosition = 1;
            }
            choiceId = mCurrentPosition - 1;
        }
        setIndicator(choiceId);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 1) {//viewpager在滑动
            isSrolling = true;
        } else if (state == 0) { //滑动结束
            releaseTime = SystemClock.currentThreadTimeMillis();
            mViewPager.setCurrentItem(mCurrentPosition, false);
        }
        isSrolling = false;
    }

    /**
     * 设置指示器的显示
     *
     * @param indicator
     */
    public void setIndicator(int indicator) {
        if (isCycle) {
            for (int i = 0; i < mIndicatorLayout.getChildCount(); i++) {
                mIndicatorLayout.getChildAt(i).findViewById(R.id.v_dot).setBackgroundResource(R.drawable.dot_normal);
            }
            if (mIndicatorLayout.getChildCount() > indicator) {
                mIndicatorLayout.getChildAt(indicator).findViewById(R.id.v_dot).setBackgroundResource(R.drawable.dot_selected);
            }
        }
    }

    public void setDelay(int time) {
        this.delayTime = time;
    }

    /**
     * 刷新数据，当外部视图更新后，通知刷新数据
     */
    public void refreshData() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public void setData(List<Model> titles) {
        initData(titles);

    }

}
