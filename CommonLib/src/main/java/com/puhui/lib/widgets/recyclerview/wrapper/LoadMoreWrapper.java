package com.puhui.lib.widgets.recyclerview.wrapper;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puhui.lib.R;
import com.puhui.lib.widgets.recyclerview.base.ViewHolder;
import com.puhui.lib.widgets.recyclerview.WrapperUtils;


/***
 * Created by zhy on 16/6/23.
 */
@SuppressWarnings("unchecked")
public class LoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;

    private RecyclerView.Adapter mInnerAdapter;
    private View mLoadMoreView;
    private View loadMoreImg;
    private TextView loadMoreTv;

    private boolean hasMore = true;

    public LoadMoreWrapper(RecyclerView.Adapter adapter, Context context) {
        mInnerAdapter = adapter;
        mLoadMoreView = LayoutInflater.from(context).inflate(R.layout.load_more_layout, null);

        loadMoreImg = mLoadMoreView.findViewById(R.id.load_more_img);
        loadMoreTv = (TextView) mLoadMoreView.findViewById(R.id.load_more_tv);

        AnimationDrawable ad = (AnimationDrawable) context.getResources().getDrawable(R.drawable.loading_progress_round);
        mLoadMoreView.findViewById(R.id.load_more_img).setBackgroundDrawable(ad);
        ad.start();
    }

    private boolean isShowLoadMore(int position) {
        return position >= mInnerAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMore(position)) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD_MORE) {
            return ViewHolder.createLoadMoreViewHolder(parent.getContext(), mLoadMoreView);
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null && hasMore) {
                mOnLoadMoreListener.onLoadMoreRequested();
            }
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (isShowLoadMore(position)) {
                    return layoutManager.getSpanCount();
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

        if (isShowLoadMore(holder.getLayoutPosition())) {
            setFullSpan(holder);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

            p.setFullSpan(true);
        }
    }

    @Override
    public int getItemCount() {
        return mInnerAdapter.getItemCount() + 1;
    }


    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreWrapper setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
        return this;
    }

    /**
     * 设置是否可以加载更多
     *
     * @param hasMore
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        if (hasMore) {
            loadMoreImg.setVisibility(View.VISIBLE);
            loadMoreTv.setText(loadMoreTv.getContext().getString(R.string.loading));
        } else {
            loadMoreImg.setVisibility(View.GONE);
            loadMoreTv.setText(loadMoreTv.getContext().getString(R.string.no_data_to_load));
        }
    }

    /**
     * 设置是否可以加载更多
     *
     * @param loadMoreText 设置不能加载更多时底部TextView的文字
     * @param hasMore
     */
    public void setHasMore(boolean hasMore, String loadMoreText) {
        this.hasMore = hasMore;
        if (hasMore) {
            loadMoreImg.setVisibility(View.VISIBLE);
            loadMoreTv.setText(loadMoreTv.getContext().getString(R.string.loading));
        } else {
            loadMoreImg.setVisibility(View.GONE);
            loadMoreTv.setText(loadMoreText);
        }
    }
}
