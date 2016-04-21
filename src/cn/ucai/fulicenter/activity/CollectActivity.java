package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by ucai on 2016/4/21.
 */
public class CollectActivity extends BaseActivity {
    Context mContext;
    ArrayList<CollectBean> mCollectList;
    CollectAdapter mAdapter;
    private  int pageId = 0;
    private int action = I.ACTION_DOWNLOAD;
    String path;

    /** 下拉刷新控件*/
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        mContext=this;
        mCollectList = new ArrayList<CollectBean>();
        initView();
        setListener();
        initData();
    }

   
    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    /**
     * 上拉刷新事件监听
     */
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
//                                pageId +=I.PAGE_SIZE_DEFAULT;
                                pageId+=4;
                                getPath(pageId);
                                executeRequest(new GsonRequest<CollectBean[]>(path,
                                        CollectBean[].class,responseDownloadNewGoodListener(),
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
                        executeRequest(new GsonRequest<CollectBean[]>(path,
                                CollectBean[].class,responseDownloadNewGoodListener(),
                                errorListener()));
                    }
                }
        );
    }

    private void initData() {
        try {
            getPath(pageId);
            executeRequest(new GsonRequest<CollectBean[]>(path,
                    CollectBean[].class,responseDownloadNewGoodListener(),
                    errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getPath(int pageId){
        try {
            String userName = FuLiCenterApplication.getInstance().getUserName();
            if (userName != null) {
                path = new ApiParams()
                        .with(I.User.USER_NAME,userName)
                        .with(I.PAGE_ID, pageId+"")
                        .with(I.PAGE_SIZE, "4")
                        .getRequestUrl(I.REQUEST_FIND_COLLECTS);
                Log.i("main", path);
                return path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response.Listener<CollectBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<CollectBean[]>() {
            @Override
            public void onResponse(CollectBean[] CollectBean) {
                if(CollectBean!=null) {
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText("加载更多数据");
                    ArrayList<CollectBean> list = Utils.array2List(CollectBean);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItems(list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if(CollectBean.length<4){
                        mAdapter.setMore(false);
                        mAdapter.setFooterText("没有更多数据");

                    }
                }
                Log.i("main", "CollectBean为空");
            }
        };
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.sfl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        DisplayUtils.initBackwithTitle(this,"收藏列表");
        mtvHint = (TextView)findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_collect);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new CollectAdapter(this,mCollectList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
