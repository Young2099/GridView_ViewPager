package com.demo.panguso.gridviewviewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ${yangfang} on 2016/10/11.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<View> mListView;

    public ViewPagerAdapter(List<View> mPagerList) {
        mListView = mPagerList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(mListView.get(position));
        return mListView.get(position);
    }

    @Override
    public int getCount() {
        if (mListView == null) {
            return 0;
        }
        return mListView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
