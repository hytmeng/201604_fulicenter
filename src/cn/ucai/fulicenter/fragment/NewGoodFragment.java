package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
//        View layout = View.inflate(mContext, R.layout.fragmeng_new_good, null);
        View layout = View.inflate(mContext, R.layout.fragmeng_new_good,null);
        mGoodList = new ArrayList<NewGoodBean>();
        initView(layout);
        setListener();
        initData();
        return layout;
    }

    private void setListener() {
//        setPullDownRefreshListener();
//        setPullUpRefreshListener();
    }

//    private void setPullUpRefreshListener() {
//        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            int lastItemPosition;
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
//                       lastItemPosition==mAdapter.getItemCount()-1) {
//                    if (mAdapter.isMore()) {
//                        mSwipeRefreshLayout.setRefreshing(true);
//                        action = I.ACTION_PULL_UP;
//                        pageId += I.PAGE_SIZE_DEFAULT;
//                        getPath(pageId);
//                        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
//                                NewGoodBean[].class,responseSownloadNewGoodListener(),mContext.errorListener()));
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                //获取最后列表项的下标
//                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
//
//                mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
//            }
//        });
//    }
//
//    private Response.Listener<NewGoodBean[]> responseSownloadNewGoodListener() {
//        return new Response.Listener<NewGoodBean[]>() {
//            @Override
//            public void onResponse(NewGoodBean[] newGoodBeen) {
//                if (newGoodBeen != null) {
//                    mAdapter.setMore(true);
//                    mSwipeRefreshLayout.setRefreshing(false);
//                    mtvHint.setVisibility(View.GONE);
//                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
//                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
//                    Log.i("main","initItems");
//                    mAdapter.initItem(list);
//                } else if (action == I.ACTION_PULL_UP) {
//                    Log.i("main","addItems");
//                    mAdapter.addItem(list);
//                }
//                    if (newGoodBeen.length < I.PAGE_SIZE_DEFAULT) {
//                        mAdapter.setMore(false);
//                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
//                    }
//                }
//            }
//        };
//    }

//    private void setPullDownRefreshListener() {
//mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//    @Override
//    public void onRefresh() {
//        mtvHint.setVisibility(View.VISIBLE);
//        pageId = 0;
//        action = I.ACTION_PULL_DOWN;
//        getPath(pageId);
//        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,NewGoodBean[].class,
//                responseDownloadNewGoodListener(),mContext.errorListener()));
//    }
//});
//    }

    private void initData() {
        try {
            String path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, I.CAT_ID + "")
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            Log.i("main", "REQUEST_FIND_NEW_BOUTIQUE_GOODS:" + path);
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
                Log.i("main", newGoodBeen.length + "daxiao");
                mSwipeRefreshLayout.setRefreshing(false);
                mtvHint.setVisibility(View.GONE);
                ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                Log.i("main", list.size() + " list.size");
                for (NewGoodBean newGoodBean : list) {
                    Log.i("main", "list" + newGoodBean.toString());
                }
                ArrayList<NewGoodBean> list1 = new ArrayList<>();
                list1.add(list.get(0));
                for (NewGoodBean newGoodBean : list1) {
                    Log.i("main", "list1" + newGoodBean.toString());
                }
//                mGoodList.addAll(list1);
                if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                    Log.i("main","initItems");
                    mAdapter.initItems(list);
                } else if (action == I.ACTION_PULL_UP) {
                    Log.i("main","addItems");
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

//    class NewGoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//        Context context;
//        ArrayList<NewGoodBean> list;
//
//
//
//        public NewGoodsAdapter(Context context, ArrayList<NewGoodBean> list) {
//            this.context = context;
//            this.list = list;
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            Log.i("main", "viewType" + viewType);
//            LayoutInflater layoutInflater = LayoutInflater.from(context);
//            RecyclerView.ViewHolder holder = null;
//            if (viewType == I.TYPE_ITEM) {
//                holder =new NewGoodsViewHolder(layoutInflater.inflate(R.layout.item_new_goods, parent, false));
//            }
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            NewGoodBean goodBean = list.get(position);
//            if (holder instanceof NewGoodsViewHolder) {
//                ((NewGoodsViewHolder) holder).tvGoodsDetails.setText(goodBean.getGoodsName());
//                ((NewGoodsViewHolder) holder).tvGoodsPrice.setText(goodBean.getCurrencyPrice());
//            }
//        }
//
//     public  void  initItem(ArrayList<NewGoodBean> goodlist) {
//         if (list.size() != 0) {
//             list.clear();
//         }
//         list.addAll(goodlist);
//         Log.i("main", "list" + list.size());
//         notifyDataSetChanged();
//        }
//        public  void  addItem(ArrayList<NewGoodBean> goodlist) {
//            list.addAll(goodlist);
//            notifyDataSetChanged();
//        }
//        @Override
//        public int getItemCount() {
//            Log.i("main", "getItemCount....:" + list.size());
//            return 1;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (position == list.size()) {
//                return I.TYPE_FOOTER;
//            } else {
//                Log.i("main","getItemViewType:TYPE_ITEM");
//                return I.TYPE_ITEM;
//            }
//        }
//    }

//    class NewGoodsViewHolder extends RecyclerView.ViewHolder {
//        LinearLayout goodItem;
//        NetworkImageView ivNewGood;
//        TextView tvGoodsDetails;
//        TextView tvGoodsPrice;
//
//        public NewGoodsViewHolder(View itemView) {
//            super(itemView);
//            goodItem = (LinearLayout) itemView.findViewById(R.id.layout_good);
//            ivNewGood = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
//            tvGoodsDetails = (TextView) itemView.findViewById(R.id.tv_name);
//            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tvGoodPrice);
//        }
//    }
}
