package cn.ucai.fulicenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;

/**
 * Created by ucai on 2016/4/22.
 */
public class FragmentCart extends Fragment {
    FuLiCenterMainActivity mContext;
    ArrayList<CartBean> mCartList;
    CartAdapter mAdapter;

    String path;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;

    TextView mtvSumPrice;
    TextView mtvSavePrice;
    TextView mNothingHint;

    UpdataCartReceiver mUpdataCartReceiver;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_cart, null);
        mCartList = new ArrayList<>();
        initView(layout);
        refresh();
        updataCartReceiverRegister();
        return layout;
    }

    private void refresh() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        mCartList.clear();
        mCartList.addAll(cartList);
        mAdapter.notifyDataSetChanged();
        sumPrice();
        if (mCartList != null && mCartList.size() > 0) {
            mNothingHint.setVisibility(View.GONE);
        } else {
            mNothingHint.setVisibility(View.VISIBLE);
        }
    }





    private void initView(View layout) {

        mtvSumPrice = (TextView) layout.findViewById(R.id.tvPriceAmount);
        mtvSavePrice = (TextView) layout.findViewById(R.id.tvPriceSave);
        mNothingHint = (TextView) layout.findViewById(R.id.tvCartHint);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CartAdapter(mContext,mCartList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUpdataCartReceiver != null) {
            mContext.unregisterReceiver(mUpdataCartReceiver);
        }
    }

    protected void sumPrice() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        int sumRankPrice=0;
        int sumCurrentPrice=0;
        for (int i=0;i<cartList.size();i++) {
            CartBean cart = cartList.get(i);
            GoodDetailsBean goods = cart.getGoods();
            if (cart.isChecked()) {
                for (int j=0;j<cart.getCount();j++) {
                    if (goods != null) {
                        int rankPrice = convertPrice(goods.getRankPrice());
                        int currentPrice = convertPrice(goods.getCurrencyPrice());
                        sumRankPrice += rankPrice;
                        sumCurrentPrice += currentPrice;
                    }
                }
            }
        }
        int sumSavePrice = sumCurrentPrice - sumRankPrice;
        mtvSumPrice.setText("合计：￥" + sumRankPrice);
        mtvSavePrice.setText("节省：￥" + sumSavePrice);
    }
    private int convertPrice(String price){
        price = price.substring(price.indexOf("￥")+1);
        int p1 = Integer.parseInt(price);
        return p1;
    }

    private void updataCartReceiverRegister() {
        mUpdataCartReceiver = new UpdataCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        mContext.registerReceiver(mUpdataCartReceiver, filter);
    }

    class UpdataCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }
}

