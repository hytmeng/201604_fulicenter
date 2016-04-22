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
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by ucai on 2016/4/22.
 */
public class UpdateCartTask extends BaseActivity {

    Context mContext;
    CartBean mCart;
    String path;

    public UpdateCartTask(Context mContext, CartBean mCart) {
        this.mContext = mContext;
        this.mCart = mCart;
        initPath();
    }
    private void initPath() {
        try {
            ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();

            if (cartList != null && cartList.contains(mCart)) {
                if (mCart.getCount() <= 0) {
                    path = new ApiParams().with(I.Cart.ID, mCart.getId() + "")
                            .getRequestUrl(I.REQUEST_DELETE_CART);
                } else {
                    path = new ApiParams().with(I.Cart.IS_CHECKED, mCart.isChecked() + "")
                            .with(I.Cart.ID, mCart.getId() + "")
                            .with(I.Cart.COUNT, mCart.getCount() + "")
                            .getRequestUrl(I.REQUEST_UPDATE_CART);
                }
            } else {
                path=new ApiParams()
                        .with(I.Cart.USER_NAME,mCart.getUserName())
                        .with(I.Cart.GOODS_ID,mCart.getGoodsId()+"")
                        .with(I.Cart.COUNT,mCart.getCount()+"")
                        .with(I.Cart.IS_CHECKED,mCart.isChecked()+"")
                        .getRequestUrl(I.REQUEST_ADD_CART);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        if (path==null||path.isEmpty())
            return;
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseListener(), errorListener()));
    }

    private Response.Listener<MessageBean> responseListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(final MessageBean message) {
                if (message.isSuccess()) {
                    final ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                    if (mCart.getCount() <= 0) {
                        cartList.remove(mCart);
                    } else {
                        if (cartList.contains(mCart)) {
                            cartList.set(cartList.indexOf(mCart), mCart);
                        } else {
                            try {
                                path = new ApiParams().with(I.CategoryGood.GOODS_ID, mCart.getGoodsId() + "").getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                                executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                                        new Response.Listener<GoodDetailsBean>() {
                                            @Override
                                            public void onResponse(GoodDetailsBean goodDetailsBean) {
                                                if (goodDetailsBean != null) {
                                                    mCart.setGoods(goodDetailsBean);
                                                    mCart.setId(Integer.parseInt(message.getMsg()));
                                                    cartList.add(mCart);
                                                    Intent intent = new Intent("update_cart");
                                                    mContext.sendStickyBroadcast(intent);
                                                }
                                            }
                                        },errorListener()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Intent intent = new Intent("update_cart");
                    mContext.sendStickyBroadcast(intent);
                }
            }
        };
    }

}
