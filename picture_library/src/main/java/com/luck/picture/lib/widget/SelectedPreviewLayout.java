package com.luck.picture.lib.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.BottomPreviewAdapter;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Time: 2019/6/25
 * Author:wyy
 * Description:
 */
public class SelectedPreviewLayout extends RelativeLayout {
    private Context mContext;
    private RecyclerView mPreview;
    private BottomPreviewAdapter adapter;
    private List<LocalMedia> selectImages = new ArrayList<LocalMedia>();

    public SelectedPreviewLayout(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public SelectedPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SelectedPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr, Context mContext) {
        super(context, attrs, defStyleAttr);
        this.mContext = mContext;
        init();
    }

    public SelectedPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Context mContext) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = mContext;
        init();
    }

    /**
     * 初始化控件
     */
    public void init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_preview, this);
        mPreview = findViewById(R.id.rv_preview);
        adapter = new BottomPreviewAdapter(mContext);

        mPreview.setHasFixedSize(true);
//        mPreview.addItemDecoration(new GridSpacingItemDecoration(1,
//                ScreenUtils.dip2px(mContext, 2), false));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mPreview.setLayoutManager(new GridLayoutManager(mContext, 1, OrientationHelper.HORIZONTAL, false));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        mPreview.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) mPreview.getItemAnimator())
                .setSupportsChangeAnimations(false);
        mPreview.setAdapter(adapter);

    }

    public void bindPreviewData(List<LocalMedia> localMediaList, int index) {
        adapter.notifyDataSetChanged(localMediaList);
        mPreview.scrollToPosition(localMediaList.size() - 1);
    }

    public RecyclerView getRecyclerView() {
        return mPreview;
    }

    /**
     * 由于存在布局复用问题，所以要准确的知道当前屏幕可见item数量，也就是item 何时开始复用
     *
     * @param ints
     */
    public void getLocation(int[] ints) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mPreview.getLayoutManager();
        int num = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
        if (layoutManager.findFirstVisibleItemPosition() == 0) {
            mPreview.getChildAt(layoutManager.findLastVisibleItemPosition()).getLocationInWindow(ints);
        } else if (layoutManager.findFirstVisibleItemPosition() > 0) {
            mPreview.getChildAt(num).getLocationInWindow(ints);
        } else {
            mPreview.getLocationInWindow(ints);
        }

    }

    public void setOnItemClickListener(BottomPreviewAdapter.OnItemClickListener onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
    }
}
