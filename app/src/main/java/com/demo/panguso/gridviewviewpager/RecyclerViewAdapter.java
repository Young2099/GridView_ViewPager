package com.demo.panguso.gridviewviewpager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ${yangfang} on 2016/10/11.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context mContex;
    private List<Model> mDatas;
    //页数下标。从0开始.当前的页面
    private int curIndex;
    //每一页显示的个数
    private int pageSize;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapter(Context mainActivity, List<Model> mDatas, int i, int pageSize) {
        layoutInflater = LayoutInflater.from(mainActivity);
        this.mDatas = mDatas;
        curIndex = i;
        this.pageSize = pageSize;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_gridview,null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tv.setText(mDatas.get(position).name);

    }

    @Override
    public int getItemCount() {
        return pageSize;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.show_text);
        }
    }

//
//
//    /**
//     * 这里要判断数据集的大小是否够显示满本页，如果够，就直接返回每一个页面的最大条目个数pageSize
//     * ,如果不够，则有几项就返回几,(也就是最后一页的时候就显示剩余item
//     *
//     * @return
//     */
//    @Override
//    public int getCount() {
//        return mDatas.size() > (curIndex + 1) * pageSize ? pageSize : (mDatas.size() - curIndex * pageSize);
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return mDatas.get(i + curIndex * pageSize);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i + curIndex * pageSize;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup viewGroup) {
//        ViewHolder viewHolder = null;
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.item_gridview, null);
//            viewHolder = new ViewHolder();
//            viewHolder.tv = (TextView) convertView.findViewById(R.id.show_text);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        int pos = position + curIndex * pageSize;
//        viewHolder.tv.setText(mDatas.get(pos).name);
//        return convertView;
//    }
//
//    class ViewHolder {
//        public TextView tv;
//    }

}
