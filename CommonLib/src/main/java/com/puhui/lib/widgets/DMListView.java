package com.puhui.lib.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.puhui.lib.R;

/**
 * 滑动自动刷新，底部提示加载更多的listView
 *
 * @author tangjian
 */
public class DMListView extends ListView {

    private OnMoreListener onMoreListener;

    private View mFooterView;

    private Context mContext;

    private TextView mFooterTextView;

    private View mFooterProgressLL;

    /**
     * 标记是否正在加载更多，防止再次调用加载更多接口
     */
    private boolean mIsLoadingMore;

    /**
     * 数据为空时的展示文字
     */
    private String emptyText = "暂无更多数据";

    /**
     * 是否有更多的数据
     */
    private boolean hasMoreData;

    /**
     * 0 更多  1没有更多  2网络不给力
     */
    private int mWhat;

    private int scrollState;

    public DMListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DMListView);
        String emptyString = mTypedArray.getString(R.styleable.DMListView_emptyText);
        if (emptyString != null && !emptyString.isEmpty()) {
            //设置没有数据时显示的内容
            this.setEmptyText(emptyString);
        }
        mTypedArray.recycle();
    }

    public DMListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DMListView(Context context) {
        this(context, null);
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    private void init(Context context) {
        /**
         * 自定义脚部文件
         */
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.pulldown_footer, null);
        mFooterTextView = (TextView) mFooterView.findViewById(R.id.pulldown_footer_text);
        mFooterProgressLL = mFooterView.findViewById(R.id.footer_loading_progress_ll);
        AnimationDrawable ad = (AnimationDrawable) context.getResources().getDrawable(R.drawable.loading_progress_round);
        mFooterView.findViewById(R.id.imageView).setBackgroundDrawable(ad);
        ad.start();
        this.addFooterView(mFooterView, null, false);
        mFooterView.setVisibility(View.GONE);
        initScrollListener();
    }

    private void initScrollListener() {
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                DMListView.this.scrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (DMListView.this.scrollState != OnScrollListener.SCROLL_STATE_IDLE
                        && firstVisibleItem + visibleItemCount == totalItemCount && !mIsLoadingMore && hasMoreData) {
                    if (onMoreListener != null) {
                        setLoadingMore(true);
                        mWhat = 0;
                        // 有更多数据才去加载更多 -- 只能在此判断，否则不会出现加载view
                        if (hasMoreData) {
                            onMoreListener.onLoadMore();
                        }
                    }
                }
            }
        });
    }

    public void setOnMoreListener(OnMoreListener onMoreListener) {
        this.onMoreListener = onMoreListener;
    }

    /**
     * 是否还有更多的数据
     */
    public void hasMoreDate(boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
        setLoadingMore(false);
        refreshFooterView(hasMoreData);
    }

    /**
     * 空数据  该方法有效，ListView必须先调用了setAdapter()方法
     */
    public void hasEmptyData() {
        mFooterView.setVisibility(View.VISIBLE);
        mFooterProgressLL.setVisibility(View.GONE);
        mFooterTextView.setVisibility(View.VISIBLE);
        mFooterTextView.setText(getEmptyText());
    }

    /**
     * 网络连接失败
     */
    public void netNotReady() {
        setLoadingMore(false);
        mFooterView.setVisibility(View.VISIBLE);
        mFooterProgressLL.setVisibility(View.GONE);
        mFooterTextView.setVisibility(View.VISIBLE);
        mFooterTextView.setText(getContext().getString(R.string.more_data));
    }

    /**
     * 加载数据出错,点击再次加载
     */
    public void loadMoreError() {
        setLoadingMore(false);
        mFooterView.setVisibility(View.VISIBLE);
        mFooterProgressLL.setVisibility(View.GONE);
        mFooterTextView.setVisibility(View.VISIBLE);
        mFooterTextView.setText("加载数据出错，点击重新加载~");
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMoreListener != null) {
                    mWhat = 0;
                    refreshFooterView(hasMoreData);
                    onMoreListener.onLoadMore();
                }
            }
        });
    }

    /**
     * 刷新底部加载view
     *
     * @param hasMoreData
     */
    private void refreshFooterView(boolean hasMoreData) {
        if (hasMoreData) {
            mFooterView.setVisibility(View.VISIBLE);
            mFooterProgressLL.setVisibility(View.VISIBLE);
            mFooterTextView.setVisibility(View.GONE);
            mFooterTextView.setText("加载中");
        } else {
            mFooterView.setVisibility(View.VISIBLE);
            mFooterProgressLL.setVisibility(View.GONE);
            mFooterTextView.setVisibility(View.VISIBLE);
            mFooterTextView.setText("暂无更多数据");
        }
    }

    /**
     * 设置正在加载更多
     *
     * @param loadingMore
     */
    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
    }

    /**
     * 获取空数据时，底部view展示的文字提示
     *
     * @return
     */
    public String getEmptyText() {
        return this.emptyText;
    }

    /**
     * 设置空数据时，底部view展示的文字提示
     *
     * @param emptyText
     */
    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    /**
     * 加载更多监听
     */
    public interface OnMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }
}