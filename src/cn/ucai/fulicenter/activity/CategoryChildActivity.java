package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.ColorBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.CatChildFilterButton;
import cn.ucai.fulicenter.view.ColorFilterButton;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by ucai on 2016/4/19.
 */
public class CategoryChildActivity extends  BaseActivity{

    ArrayList<NewGoodBean> mGoodList;
    GoodAdapter mAdapter;
    private  int pageId = 0;
    private int action = I.ACTION_DOWNLOAD;
    String path;
    int catId;


    /** 下拉刷新控件*/
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    CatChildFilterButton mCatChildFilterButton;
    ColorFilterButton mColorFilterButton;
    Button mbtnPriceSort;
    Button mbtnaddtimeSort;
    ArrayList<CategoryChildBean> mChildlist;

    int sortBy;
    boolean mSortByPriceAsc;
    boolean mSortByAddtimeAsc;
    Context mContext;
    String mGroupName;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext=this;
        setContentView(R.layout.activity_child_category);
        mGoodList = new ArrayList<NewGoodBean>();
        sortBy = I.SORT_BY_ADDTIME_DESC;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        SortStateChangerdListener mSortStateChangerdListener = new SortStateChangerdListener();
        mbtnPriceSort.setOnClickListener(mSortStateChangerdListener);
        mbtnaddtimeSort.setOnClickListener(mSortStateChangerdListener);
        mCatChildFilterButton.setOnCatFilterClickListener(mGroupName, mChildlist);
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    int lastItemPosition;
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if(newState == RecyclerView.SCROLL_STATE_IDLE &&
                                lastItemPosition == mAdapter.getItemCount()-1){
                            if(mAdapter.isMore()){
                                mSwipeRefreshLayout.setRefreshing(true);
                                action = I.ACTION_PULL_UP;
                                pageId +=I.PAGE_SIZE_DEFAULT;
                                getPath(pageId);
                                executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                        NewGoodBean[].class,responseDownloadNewGoodListener(),
                                        errorListener()));
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        //获取最后列表项的下标
                        lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                        //解决RecyclerView和SwipeRefreshLayout共用存在的bug
                        mSwipeRefreshLayout.setEnabled(mGridLayoutManager
                                .findFirstCompletelyVisibleItemPosition() == 0);
                    }
                }
        );
    }

    /**
     * 下拉刷新事件监听
     */
    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener(){
                    @Override
                    public void onRefresh() {
                        mtvHint.setVisibility(View.VISIBLE);
                        pageId = 0;
                        action = I.ACTION_PULL_DOWN;
                        getPath(pageId);
                        executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                NewGoodBean[].class,responseDownloadNewGoodListener(),
                                errorListener()));
                    }
                }
        );
    }

    private void initData() {
        catId = getIntent().getIntExtra(I.CategoryChild.CAT_ID, 0);
        mChildlist = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
        try {
            getPath(pageId);
            executeRequest(new GsonRequest<NewGoodBean[]>(path,
                    NewGoodBean[].class,responseDownloadNewGoodListener(),
                    errorListener()));
            String colorUrl = new ApiParams().with(I.Color.CAT_ID, catId + "")
                    .getRequestUrl(I.REQUEST_FIND_COLOR_LIST);
            executeRequest(new GsonRequest<ColorBean[]>(colorUrl,
                    ColorBean[].class,responseDownloadColorListener(),
                    errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<ColorBean[]> responseDownloadColorListener() {
        return new Response.Listener<ColorBean[]>() {
            @Override
            public void onResponse(ColorBean[] colorBeen) {
                if (colorBeen != null) {
                    ArrayList<ColorBean> list = Utils.array2List(colorBeen);
                    mColorFilterButton.setVisibility(View.VISIBLE);
                    mColorFilterButton.setOnColorFilterClickListener(mGroupName, mChildlist, list);
                }
            }
        };
    }

    private String getPath(int pageId){
        try {
            path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, catId+"")
                    .with(I.PAGE_ID, pageId+"")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT+"")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response.Listener<NewGoodBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                if(newGoodBeen!=null) {
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(R.string.loading_more+"");
                    //将数组转换为集合
                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItems(list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if(newGoodBeen.length<I.PAGE_SIZE_DEFAULT){
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(R.string.no_more_data+"");
                    }
                }
            }
        };
    }

    private void initView() {
        //更改显示精品二级标题
       mGroupName = getIntent().getStringExtra(I.Boutique.NAME);
        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatFilter);
        mCatChildFilterButton.setText(mGroupName);
        DisplayUtils.initBack(this);
        mColorFilterButton = (ColorFilterButton) findViewById(R.id.cfbColor);
        mColorFilterButton.setVisibility(View.INVISIBLE);
        mbtnPriceSort = (Button) findViewById(R.id.btnPriceSort);
        mbtnaddtimeSort = (Button) findViewById(R.id.btnAddTimeSort);


        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.sfl_newgood);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView)findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(this, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_newgood);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(this,mGoodList,sortBy);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class SortStateChangerdListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Drawable right;
            int resId;
            switch (v.getId()) {
                case R.id.btnPriceSort:
                    if (mSortByPriceAsc) {
                        sortBy = I.SORT_BY_PRICE_ASC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                        resId = R.drawable.arrow_order_up;
                    } else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                        resId = R.drawable.arrow_order_down;
                    }
                    mSortByPriceAsc = !mSortByPriceAsc;
                    right.setBounds(0, 0, ImageUtils.getDrawableWidth(mContext, resId), ImageUtils.getDrawableHeight(mContext, resId));
                    mbtnPriceSort.setCompoundDrawables(null, null, right, null);
                    break;
                case R.id.btnAddTimeSort:
                    if (mSortByAddtimeAsc) {
                        sortBy = I.SORT_BY_ADDTIME_ASC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                        resId = R.drawable.arrow_order_up;
                    } else {
                        sortBy = I.SORT_BY_ADDTIME_DESC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                        resId = R.drawable.arrow_order_down;
                    }
                    mSortByAddtimeAsc = !mSortByAddtimeAsc;
                    right.setBounds(0, 0, ImageUtils.getDrawableWidth(mContext, resId), ImageUtils.getDrawableHeight(mContext, resId));
                    mbtnaddtimeSort.setCompoundDrawables(null, null, right, null);
                    break;
            }
            mAdapter.setSortBy(sortBy);
        }
    }
}
