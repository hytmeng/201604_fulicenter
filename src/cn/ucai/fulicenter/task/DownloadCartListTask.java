package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by ucai on 2016/4/7.
 */
public class DownloadCartListTask extends BaseActivity {



    Context mContext;
    String userNane;
    int pageId;
    int pageSize;
    String path;

    public DownloadCartListTask(Context mContext, String userNane, int pageId, int pageSize) {
        this.mContext = mContext;
        this.userNane = userNane;
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath() {
        try {
            path=new ApiParams()
                    .with(I.User.USER_NAME,userNane)
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,pageSize+"")
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<CartBean[]>(path, CartBean[].class, responseListener(), errorListener()));
    }

    private Response.Listener<CartBean[]> responseListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] Carts) {
                if (Carts == null) {
                    return;
                }
                ArrayList<CartBean> cartlist = Utils.array2List(Carts);
                final ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                for (int i=0;i<cartlist.size();i++) {
                    CartBean cartBean = cartlist.get(i);
                    if (!cartList.contains(cartBean)) {
                        cartList.add(cartBean);
                        try {
                            String path = new ApiParams().with(I.Cart.GOODS_ID, cartBean.getGoodsId() + "").getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                                    new Response.Listener<GoodDetailsBean>() {
                                        @Override
                                        public void onResponse(GoodDetailsBean goodDetailsBean) {
                                            if (goodDetailsBean != null) {
                                                for (int j = 0; j < cartList.size(); j++) {
                                                    if (cartList.get(j).getGoodsId() == goodDetailsBean.getGoodsId()) {
                                                        cartList.get(j).setGoods(goodDetailsBean);
                                                        Intent intent = new Intent("update_cart");
                                                        mContext.sendStickyBroadcast(intent);
                                                    }
                                                }
                                            }
                                        }
                                    },errorListener()
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Intent intent = new Intent("update_cart");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
