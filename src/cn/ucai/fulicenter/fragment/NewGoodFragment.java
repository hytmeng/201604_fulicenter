package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FulicenterMainActivity;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by ucai on 2016/4/16.
 */
public class NewGoodFragment  extends Fragment{

    FulicenterMainActivity mContext;
    ArrayList<NewGoodBean> mGoodList;

    GoodsAdapter mAdapter;
    private int pageId=0;
    private int action = I.ACTION_DOWNLOAD;
//下拉刷新控件
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext= (FulicenterMainActivity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragmeng_new_good, null);
        mGoodList = new ArrayList<>();
        initView(layout);
        initData();
        return layout;
    }

    private void initData() {
        try {
            String path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, I.CAT_ID + "")
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,NewGoodBean[].class,
                    responseDownloadNewGoodListener(),mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<NewGoodBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                mSwipeRefreshLayout.setRefreshing(false);
                mtvHint.setVisibility(View.GONE);
                ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                    mAdapter.initItems(list);
                } else if (action == I.ACTION_PULL_UP) {
                    mAdapter.addItems(list);
                }
            }
        };
    }

    private void initView(View layout)  {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.sfl_newgood);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_newgood);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodsAdapter(mContext, mGoodList);
        mRecyclerView.setAdapter(mAdapter);

    }
}
