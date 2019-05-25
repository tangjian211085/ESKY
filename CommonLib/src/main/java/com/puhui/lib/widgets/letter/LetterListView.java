package com.puhui.lib.widgets.letter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.puhui.lib.R;
import com.puhui.lib.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 版    权:  深圳市迪蒙网络科技有限公司
 * 描    述:
 * Created by tangjian on 2016/9/12.
 */
public class LetterListView extends ListView {

    private static final int DEFAULT_LETTER_COLOR = 0X383838;

    private static final int DEFAULT_LETTER_SIZE = 14;

    private Context mContext;

    private List<Letter> mLettersList = new ArrayList<>(10);

    private int selectedLetterColor = DEFAULT_LETTER_COLOR;

    private int commonTextColor = DEFAULT_LETTER_COLOR;

    private int letterSize = DEFAULT_LETTER_SIZE;

    private int mLetterCount = 26;

    private OnItemTextClickListener mOnItemClickListener;

    private LetterListAdapter adapter;
    /**
     * 字母导航
     */
    private String[] letter = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public LetterListView(Context context) {
        super(context);
        init(context, null);
    }

    public LetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LetterListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        setOverScrollMode(OVER_SCROLL_NEVER);
        setSelector(R.color.transparent);
        setVerticalScrollBarEnabled(false);
        setDividerHeight(0);

        //setPadding(0, 20, 0, 20);

        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LetterListView);
            selectedLetterColor = typedArray.getInt(R.styleable.LetterListView_selectedTextColor, DEFAULT_LETTER_COLOR);
            commonTextColor = typedArray.getInt(R.styleable.LetterListView_commonTextColor, DEFAULT_LETTER_COLOR);
            letterSize = (int) typedArray.getDimension(R.styleable.LetterListView_commonTextSize,
                    DensityUtil.dip2px(mContext, DEFAULT_LETTER_SIZE));
            typedArray.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                setPressed(false);
                updateAdapter(mLettersList.size());
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemTextClick(null, -1);
                }
                break;
            default:
                setPressed(true);
                float pointY = ev.getY();
                if (pointY < getPaddingTop() || pointY > getMeasuredHeight() - getPaddingBottom()) {
                    break;
                }

                float itemHeight = (float) (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) / mLetterCount;
                float actClickPosition = pointY - getPaddingTop();
                float temp = actClickPosition % itemHeight > 0.5 ?
                        actClickPosition / itemHeight : actClickPosition / itemHeight - 1;
                int position = (int) temp;
                updateAdapter(position);
                if (null != mOnItemClickListener && position >= 0 && mLettersList.size() > position) {
                    mOnItemClickListener.onItemTextClick(mLettersList.get(position).getLetterName(), position);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnItemTextClickListener(OnItemTextClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemTextClickListener {
        void onItemTextClick(String text, int position);
    }

    class LetterListAdapter extends BaseAdapter {

        public LetterListAdapter() {
        }

        @Override
        public int getCount() {
            return mLettersList.size();
        }

        @Override
        public Object getItem(int position) {
            return mLettersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final TextView textView = new TextView(mContext);
            textView.setText(mLettersList.get(position).getLetterName());
            textView.setGravity(Gravity.CENTER);
            if (!mLettersList.get(position).isSelected()) {
                textView.setTextColor(commonTextColor);
            } else {
                textView.setTextColor(selectedLetterColor);
            }
            textView.setTextSize(letterSize);

            if (mLettersList.size() <= 15) {
                int height = getMeasuredHeight() / mLettersList.size() - dip2px(mContext, 40);
                int padding = (getMeasuredHeight() - mLettersList.size() * height) / 2;
                setPadding(0, padding, 0, padding);
                textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height));
            } else if (mLettersList.size() <= 20) {
                int height = getMeasuredHeight() / mLettersList.size() - dip2px(mContext, 5);
                int padding = (getMeasuredHeight() - mLettersList.size() * height) / 2;
                setPadding(0, padding, 0, padding);
                textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height));
            } else {
                textView.setPadding(0, 0, 0, 0);
                textView.setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getMeasuredHeight() / mLettersList
                        .size()));
                textView.setTextSize(letterSize - 2);
            }

//            textView.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.text_size_6));
            textView.setTextSize(14);
//            int height = (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) /
// mLetterCount;
//            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//            textView.setPadding(0, padding, 0, 0);
            return textView;
        }
    }

    /****
     * 更新数据显示状态
     *
     * @param position 表示在position位置上的textView被点击，或者选中
     */
    public void updateAdapter(int position) {
        for (int i = 0; i < mLettersList.size(); i++) {
            if (i == position) {
                mLettersList.get(position).setSelected(true);
            } else {
                mLettersList.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public List<Letter> getLettersList() {
        return mLettersList;
    }

    /***
     * 填充数据，设置适配器
     */
    public void setLetters(List<String> letters) {
        if (null == letters || letters.size() == 0) {
            return;
        }
        //获取第一个元素
        String s = letters.get(0);
        letters.clear();
        letters.add(s);
        List<String> asList = Arrays.asList(letter);
        letters.addAll(asList);
        //判断第一个元素是否是26个字母
        if (asList.contains(letters.get(0))) {
            letters.remove(0);
            mLetterCount = 26;
        } else {
            mLetterCount = 27;
        }
        for (String letter : letters) {
            mLettersList.add(new Letter(letter, false));
        }
        adapter = new LetterListAdapter();
        setAdapter(adapter);
    }

    public int getSelectedLetterColor() {
        return selectedLetterColor;
    }

    /***
     * 设置 LetterListView 字体颜色
     *
     * @param selectedLetterColor
     */
    public void setSelectedLetterColor(int selectedLetterColor) {
        this.selectedLetterColor = selectedLetterColor;
    }

    public int getLetterSize() {
        return letterSize;
    }

    /***
     * 设置 LetterListView 字体大小
     *
     * @param letterSize
     */
    public void setLetterSize(int letterSize) {
        this.letterSize = letterSize;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
