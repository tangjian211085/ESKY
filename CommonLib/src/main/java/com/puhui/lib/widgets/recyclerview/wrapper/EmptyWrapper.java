package com.puhui.lib.widgets.recyclerview.wrapper;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.puhui.lib.R;
import com.puhui.lib.utils.StringUtils;
import com.puhui.lib.widgets.recyclerview.base.ViewHolder;
import com.puhui.lib.widgets.recyclerview.WrapperUtils;


/***
 * Created by zhy on 16/6/23.
 */
@SuppressWarnings("unchecked")
public class EmptyWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_EMPTY = Integer.MAX_VALUE - 1;

    private RecyclerView.Adapter mInnerAdapter;
    private View mEmptyView;

    private ImageView empty_img;
    private TextView empty_tv;

    public EmptyWrapper(RecyclerView.Adapter adapter, Context context) {
        mEmptyView = LayoutInflater.from(context).inflate(R.layout.empty_view, null);
        empty_img = (ImageView) mEmptyView.findViewById(R.id.empty_img);
        empty_tv = (TextView) mEmptyView.findViewById(R.id.empty_tv);
        mInnerAdapter = adapter;
    }

    public void setEmptyImg(int imgResourceId) {
        if (imgResourceId > 0) {
            empty_img.setImageResource(imgResourceId);
        }
    }

    public void setEmptyText(String text) {
        if (!StringUtils.isEmpty(text))
            empty_tv.setText(text);
    }

    private boolean isEmpty() {
        return mInnerAdapter.getItemCount() == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isEmpty()) {
            return ViewHolder.createEmptyViewHolder(parent.getContext(), mEmptyView);
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (isEmpty()) {
                    return gridLayoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });


    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        if (isEmpty()) {
            WrapperUtils.setFullSpan(holder);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isEmpty()) {
            return ITEM_TYPE_EMPTY;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isEmpty()) {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        if (isEmpty()) return 1;
        return mInnerAdapter.getItemCount();
    }

}
