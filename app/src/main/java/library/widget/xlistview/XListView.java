package library.widget.xlistview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.cstz.cstz_android.R;
import com.cstz.tools.PxUtil;

/**
 * XListView, it's based on <a href="https://github.com/Maxwin-z/XListView-Android">XListView(Maxwin)</a>
 *
 * @author markmjw
 * @date 2013-10-08
 */
public class XListView extends ListView implements OnScrollListener {
//    private static final String TAG = "XListView";

    private final static int SCROLL_BACK_HEADER = 0;
    private final static int SCROLL_BACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400;

    // when pull up >= 50px
    private final static int PULL_LOAD_MORE_DELTA = 50;

    // support iOS like pull
    private final static float OFFSET_RADIO = 1.8f;

    private float mLastY = -1;

    // used for scroll back
    private Scroller mScroller;
    // user's scroll listener
    private OnScrollListener mScrollListener;
    // for mScroller, scroll back from header or footer.
    private int mScrollBack;

    // the interface to trigger refresh and load more.
    private IXListViewListener mListener;

    private XListViewHeaderView mHeader;
    // header view content, use it to calculate the Header's height. And hide it when disable pull refresh.
    private RelativeLayout mHeaderContent;
    private TextView mHeaderTime;
    private int mHeaderHeight;

    private LinearLayout mFooterLayout;
    private XListViewFooterView mFooterView;
    private boolean mIsFooterReady = false;

    private boolean mEnablePullRefresh = false;
    private boolean mPullRefreshing = false;

    private boolean mEnablePullLoad = true;
    private boolean mEnableAutoLoad = false;
    private boolean mPullLoading = false;

    // total list items, used to detect is at the bottom of ListView
    private int mTotalItemCount;

    public XListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);

        // init header view
        mHeader = new XListViewHeaderView(context);
        mHeaderContent = (RelativeLayout) mHeader.findViewById(R.id.xlistview_header_content);
        mHeaderTime = (TextView) mHeader.findViewById(R.id.xlistview_header_time);
        addHeaderView(mHeader);

        // init footer view
        mFooterView = new XListViewFooterView(context);
        mFooterLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mFooterLayout.addView(mFooterView, params);

        // init header height
        ViewTreeObserver observer = mHeader.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    mHeaderHeight = mHeaderContent.getHeight();
                    ViewTreeObserver observer = getViewTreeObserver();

                    if (null != observer) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            observer.removeGlobalOnLayoutListener(this);
                        } else {
                            observer.removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XFooterView is the last footer view, and only add once.
        if (!mIsFooterReady) {
            mIsFooterReady = true;
            addFooterView(mFooterLayout);
        }

        super.setAdapter(adapter);
    }

    /**
     * Enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;

        // disable, hide the content
        mHeaderContent.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;

        if (!mEnablePullLoad) {
            mFooterView.setBottomMargin(0);
            mFooterView.hide();
            mFooterView.setPadding(0, 0, 0, 0);
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.setPadding(0, 0, 0, 0);
            mFooterView.show();
            mFooterView.setState(XListViewFooterView.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    public void setNoMoreData() {
        mEnablePullLoad = false;
        mPullLoading = false;
        mEnableAutoLoad=false;
        mFooterView.setOnClickListener(null);
        mFooterView.setPadding(0, 0, 0, 0);
        mFooterView.show();
        mFooterView.setState(XListViewFooterView.STATE_NO_MORE_DATA);
    }

    /**
     * Enable or disable auto load more feature when scroll to bottom.
     *
     * @param enable
     */
    public void setAutoLoadEnable(boolean enable) {
        mEnableAutoLoad = enable;
    }

    /**
     * Stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * Stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(XListViewFooterView.STATE_NORMAL);
        }
    }

    /**
     * Set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTime.setText(time);
    }

    /**
     * Set listener.
     *
     * @param listener
     */
    public void setXListViewListener(IXListViewListener listener) {
        mListener = listener;
    }

    /**
     * Auto call back refresh.
     */
    public void autoRefresh() {
        mHeader.setVisibleHeight(mHeaderHeight);

        if (mEnablePullRefresh && !mPullRefreshing) {
            // update the arrow image not refreshing
            if (mHeader.getVisibleHeight() > mHeaderHeight) {
                mHeader.setState(XListViewHeaderView.STATE_READY);
            } else {
                mHeader.setState(XListViewHeaderView.STATE_NORMAL);
            }
        }

        mPullRefreshing = true;
        mHeader.setState(XListViewHeaderView.STATE_REFRESHING);
        refresh();
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener listener = (OnXScrollListener) mScrollListener;
            listener.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeader.setVisibleHeight((int) delta + mHeader.getVisibleHeight());

        if (mEnablePullRefresh && !mPullRefreshing) {
            // update the arrow image unrefreshing
            if (mHeader.getVisibleHeight() > mHeaderHeight) {
                mHeader.setState(XListViewHeaderView.STATE_READY);
            } else {
                mHeader.setState(XListViewHeaderView.STATE_NORMAL);
            }
        }

        // scroll to top each time
        setSelection(0);
    }

    private void resetHeaderHeight() {
        int height = mHeader.getVisibleHeight();
        if (height == 0) return;

        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderHeight) return;

        // default: scroll back to dismiss header.
        int finalHeight = 0;
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderHeight) {
            finalHeight = mHeaderHeight;
        }

        mScrollBack = SCROLL_BACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);

        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;

        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) {
                // height enough to invoke load more.
                mFooterView.setState(XListViewFooterView.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooterView.STATE_NORMAL);
            }
        }

        mFooterView.setBottomMargin(height);

        // scroll to bottom
        // setSelection(mTotalItemCount - 1);
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();

        if (bottomMargin > 0) {
            mScrollBack = SCROLL_BACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(XListViewFooterView.STATE_LOADING);
        loadMore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();

                if (getFirstVisiblePosition() == 0 && (mHeader.getVisibleHeight() > 0 ||
                        deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    if (mEnablePullRefresh) {
                        updateHeaderHeight(deltaY / OFFSET_RADIO);
                        invokeOnScrolling();
                    }
                } else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView
                        .getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                // reset
                mLastY = -1;
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh && mHeader.getVisibleHeight() > mHeaderHeight) {
                        mPullRefreshing = true;
                        mHeader.setState(XListViewHeaderView.STATE_REFRESHING);
                        refresh();
                    }

                    resetHeaderHeight();

                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();//这里API16不能向上滑动
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLL_BACK_HEADER) {
                mHeader.setVisibleHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }

            postInvalidate();
            invokeOnScrolling();
        }

        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }

        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            if (mEnableAutoLoad && getLastVisiblePosition() == getCount() - 1) {
                startLoadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    private void refresh() {
        if (mEnablePullRefresh && null != mListener) {
            mListener.onRefresh();
        }
    }

    private void loadMore() {
        if (mEnablePullLoad && null != mListener) {
            mListener.onLoadMore();
        }
    }

    /**
     * You can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * Implements this interface to get refresh/load more event.
     *
     * @author markmjw
     */
    public interface IXListViewListener {
        public void onRefresh();

        public void onLoadMore();
    }

    /**
     * @param resId        DrawableTop ResId
     * @param mEmptyTextId
     */
    public void setEmptyViewUI(int resId, int mEmptyTextId) {
        setEmptyViewUI(resId, getResources().getText((mEmptyTextId > 0 ? mEmptyTextId : R.string.app_no_data_tips)).toString());
    }

    /**
     * @param resId      DrawableTop ResId
     * @param mEmptyText
     */
    public void setEmptyViewUI(int resId, String mEmptyText) {
        initEmptyView();
        if (mTextEmptyView != null) {
            mTextEmptyView.setText(mEmptyText);
            if (resId > 0) {
                Drawable mDrawable = getResources().getDrawable(resId);
                int height = mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), height);
                mTextEmptyView.setCompoundDrawables(null, mDrawable, null, null);
                mTextEmptyView.setCompoundDrawablePadding(PxUtil.dip2px(getContext(), 5));
                mTextEmptyView.setPadding(0, 0, 0, height * 2);
            } else {
                mTextEmptyView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dimen_35));
//				mTextEmptyView.set
            }
            setEmptyView(mTextEmptyView);
        }
    }

    public void setEmptyImage(int drawableId) {
        ViewGroup mViewGroup = ((ViewGroup) getParent());
        if (drawableId > 0) {
            Drawable mDrawable = getResources().getDrawable(drawableId);
            int height = mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), height);
            ImageView iv = new ImageView(getContext());
            iv.setBackground(mDrawable);
            RelativeLayout.LayoutParams params, imageParams;
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            mViewGroup.setLayoutParams(params);
            imageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mViewGroup.addView(iv, imageParams);
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (View.VISIBLE != getVisibility())
                    setVisibility(View.VISIBLE);
            }
        });
        super.setEmptyView(mViewGroup);
    }

    private void initEmptyView() {
        mTextEmptyView = new TextView(getContext());
        mTextEmptyView.setTextColor(Color.parseColor("#777777"));
        mTextEmptyView.setText("暂无记录");
        mTextEmptyView.setGravity(Gravity.CENTER);
    }

    @Override
    public void setEmptyView(View emptyView) {
        ViewGroup mViewGroup = ((ViewGroup) getParent());
        FrameLayout.LayoutParams params = null;
        if (isDrawableTop()) {
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);//WRAP_CONTENT
        } else
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.dimen_100), 0, 0);
        emptyView.setLayoutParams(params);
        mViewGroup.addView(emptyView);
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (View.VISIBLE != getVisibility())
                    setVisibility(View.VISIBLE);
            }
        });
        super.setEmptyView(emptyView);
    }

    public void setEmptyViewText(int mEmptyTextId) {
        setEmptyViewUI(0, mEmptyTextId);//R.drawable.data_no
    }

    public void setEmptyViewText(String mEmptyText) {
        setEmptyViewUI(R.drawable.data_no, mEmptyText);
    }

    /**
     * @param resId DrawableTop ResId
     */
    public void setEmptyViewImage(int resId) {
        setEmptyViewUI(resId, R.string.app_no_data_tips);
    }

    /**
     * 开启EmptyView
     *
     * @param enable
     */
    public void setEmptyViewEnable(boolean enable) {
        if (enable) {
            setEmptyViewUI(R.drawable.data_no, 0);
        }
    }

    private TextView mTextEmptyView;

    private boolean isDrawableTop() {
        Drawable[] mDrawables = mTextEmptyView != null ? mTextEmptyView.getCompoundDrawables() : null;
        return mDrawables != null && mDrawables[1] != null;
    }

    public void setFooterViewInvisible() {
        if (mFooterView != null) {
            mFooterView.setVisibility(View.GONE);
        }
    }

    public void setFooterViewVisible() {
        if (mFooterView != null) {
            mFooterView.setVisibility(View.VISIBLE);
        }
    }

    public boolean getIsPullRefreshing() {
        return mPullRefreshing;
    }
}
