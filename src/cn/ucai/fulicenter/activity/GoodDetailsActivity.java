package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by ucai on 2016/4/18.
 */
public class GoodDetailsActivity extends BaseActivity{
    GoodDetailsActivity mContext;
    GoodDetailsBean mGoodDetails;
    int mGoodsId;
    /** 用于收藏、支付的商品信息实体*/
    NewGoodBean mGood;
    /** 封装了显示商品信息的view*/
//    ViewHolder mHolder;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    /** 显示颜色的容器布局*/
    LinearLayout mLayoutColors;
    ImageView mivCollect;
    ImageView mivAddCart;
    ImageView mivShare;
    TextView mtvCartCount;

    TextView tvGoodName;
    TextView tvGoodEngishName;
    TextView tvShopPrice;
    TextView tvCurrencyPrice;
    WebView wvGoodBrief;

    /** 当前的颜色值*/
    int mCurrentColor;

    String mCurrentUserName;
    /**
     * 当前商品是否收藏
     */
    boolean isCollect;
    private int actionCollect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.good_details);
        mContext=this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setCollectListener();
    }

    private void setCollectListener() {
        mivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = FuLiCenterApplication.getInstance().getUserName();
                if (userName != null && isCollect) {
                    deleteCollect(userName,mGoodsId);
                } else if (userName != null && !isCollect) {
                    addCollect(userName,mGoodsId);
                } else {
                    gotoLogin("goodDetails");
                }
            }
        });
    }

    private void addCollect(String userName, int mGoodsId) {
        try {
            String path = new ApiParams()
                    .with(I.Collect.USER_NAME,userName)
                    .with(I.Collect.GOODS_ENGLISH_NAME,mGoodDetails.getGoodsEnglishName())
                    .with(I.Collect.GOODS_NAME,mGoodDetails.getGoodsName())
                    .with(I.Collect.GOODS_IMG,mGoodDetails.getGoodsImg())
                    .with(I.Collect.ADD_TIME,mGoodDetails.getAddTime()+"")
                    .with(I.Collect.GOODS_THUMB,mGoodDetails.getGoodsThumb())
                    .with(I.Collect.GOODS_ID, mGoodsId+"")
                    .getRequestUrl(I.REQUEST_ADD_COLLECT);
            Log.i("main", path);
            executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                    responseDownloadAddCollectListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<MessageBean> responseDownloadAddCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean != null & messageBean.isSuccess()) {
                    Toast.makeText(mContext, "添加收藏成功", Toast.LENGTH_SHORT).show();
                    mivCollect.setImageResource(R.drawable.bg_collect_out);
                    isCollect = true;
                    int count = FuLiCenterApplication.getInstance().getCollectCount();
                    FuLiCenterApplication.getInstance().setCollectCount(count+1);
                    sendStickyBroadcast(new Intent("update_collectCount"));
                } else {
                    Toast.makeText(mContext, "添加收藏失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void deleteCollect(String UserName, int mGoodsId) {
        try {
            String path = new ApiParams()
                    .with(I.Collect.USER_NAME,UserName)
                    .with(I.Collect.GOODS_ID, mGoodsId+"")
                    .getRequestUrl(I.REQUEST_DELETE_COLLECT);
            executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                    responseDownloadDeleteCollectListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<MessageBean> responseDownloadDeleteCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean != null & messageBean.isSuccess()) {
                    Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
                    mivCollect.setImageResource(R.drawable.bg_collect_in);
                    isCollect = false;
                    int count = FuLiCenterApplication.getInstance().getCollectCount();
                    FuLiCenterApplication.getInstance().setCollectCount(count-1);
                    sendStickyBroadcast(new Intent("update_collectCount"));
                } else {
                    Toast.makeText(mContext, "取消收藏失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void gotoLogin(String action) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("action", action);
        startActivity(intent);
    }
    private void initData() {
        mGoodsId=getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        mCurrentUserName = FuLiCenterApplication.getInstance().getUserName();

        try {
            String path = new ApiParams().with(I.CategoryGood.GOODS_ID, mGoodsId+"")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
            executeRequest(new GsonRequest<GoodDetailsBean>(path,GoodDetailsBean.class,
                    responseDownloadGoodDetailsListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCollect(String UserName, int mGoodsId) {
        try {
            String path = new ApiParams()
                    .with(I.Collect.USER_NAME,UserName)
                    .with(I.Collect.GOODS_ID, mGoodsId+"")
                    .getRequestUrl(I.REQUEST_IS_COLLECT);
            executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                    responseDownloadIsCollectListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<MessageBean> responseDownloadIsCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean != null && messageBean.isSuccess()) {
                    mivCollect.setImageResource(R.drawable.bg_collect_out);
                    isCollect = true;
                } else {
                    mivCollect.setImageResource(R.drawable.bg_collect_in);
                    isCollect = false;
                }
            }
        };
    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailsListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if(goodDetailsBean!=null){
                    mGoodDetails = goodDetailsBean;
                    DisplayUtils.initBackwithTitle(mContext,"商品详情");
                    tvCurrencyPrice.setText(mGoodDetails.getCurrencyPrice());
                    tvGoodEngishName.setText(mGoodDetails.getGoodsEnglishName());
                    tvGoodName.setText(mGoodDetails.getGoodsName());
                    wvGoodBrief.loadDataWithBaseURL(null, mGoodDetails.getGoodsBrief().trim(), D.TEXT_HTML, D.UTF_8, null);

                    //初始化颜色面板
                    initColorsBanner();
                }else {
                    Utils.showToast(mContext, "商品详情下载失败", Toast.LENGTH_LONG);
                }
            }
        };
    }

    private void initColorsBanner() {
        //设置第一个颜色的图片轮播
        updateColor(0);
        for(int i=0;i<mGoodDetails.getProperties().length;i++){
            mCurrentColor=i;
            View layout=View.inflate(mContext, R.layout.layout_property_color, null);
            final NetworkImageView ivColor=(NetworkImageView) layout.findViewById(R.id.ivColorItem);
            String colorImg = mGoodDetails.getProperties()[i].getColorImg();
            if(colorImg.isEmpty()){
                continue;
            }
            ImageUtils.setGoodDetailThumb(colorImg,ivColor);
            mLayoutColors.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);
                }
            });
        }
    }
    /**
     * 设置指定属性的图片轮播
     * @param i
     */
    private void updateColor(int i) {
        AlbumsBean[] albums = mGoodDetails.getProperties()[i].getAlbums();
        String[] albumImgUrl=new String[albums.length];
        for(int j=0;j<albumImgUrl.length;j++){
            albumImgUrl[j]=albums[j].getImgUrl();
        }
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, albumImgUrl, albumImgUrl.length);
    }

    private void initView() {
        mivCollect= (ImageView) findViewById(R.id.ivCollect);
        mivAddCart=(ImageView) findViewById(R.id.ivAddCart);
        mivShare = (ImageView) findViewById(R.id.ivShare);
        mtvCartCount=(TextView) findViewById(R.id.tvCartCount);

        mSlideAutoLoopView= (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator= (FlowIndicator) findViewById(R.id.indicator);
        mLayoutColors= (LinearLayout) findViewById(R.id.layoutColorSelector);
        tvCurrencyPrice= (TextView) findViewById(R.id.tvCurrencyPrice);
        tvGoodEngishName=(TextView) findViewById(R.id.tvGoodEnglishName);
        tvGoodName=(TextView) findViewById(R.id.tvGoodName);
        tvShopPrice=(TextView) findViewById(R.id.tvShopPrice);
        wvGoodBrief= (WebView) findViewById(R.id.wvGoodBrief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollect();
    }

    private void initCollect() {
        String userName = FuLiCenterApplication.getInstance().getUserName();
        if (userName != null) {
            showCollect(userName, mGoodsId);
        } else {
            mivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }
}
