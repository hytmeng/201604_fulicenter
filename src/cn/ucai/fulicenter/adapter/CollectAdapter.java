package cn.ucai.fulicenter.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by clawpo on 16/4/16.
 */
public class CollectAdapter extends RecyclerView.Adapter<ViewHolder> {
    CollectActivity mContext;
    ArrayList<CollectBean> mCollectList;

    GoodItemViewHolder goodHolder;
    FooterViewHolder footerHolder;

    private String footerText;
    private boolean isMore;



    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CollectAdapter(CollectActivity mContext, ArrayList<CollectBean> collectList) {
        this.mContext = mContext;
        this.mCollectList = collectList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType){
            case I.TYPE_ITEM:
                holder = new GoodItemViewHolder(inflater.inflate(R.layout.item_collect,parent,false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer,parent,false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(holder instanceof FooterViewHolder){
            footerHolder = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);
        }

        if(holder instanceof GoodItemViewHolder){
            goodHolder = (GoodItemViewHolder) holder;
            final CollectBean good = mCollectList.get(position);
            goodHolder.tvGoodName.setText(good.getGoodsName());
            ImageUtils.setNewGoodThumb(good.getGoodsThumb(),goodHolder.nivThumb);


            //整体被点击进入商品详情
            goodHolder.layoutCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                            .putExtra(D.NewGood.KEY_GOODS_ID, good.getGoodsId()));
                }
            });
            //删除图标被点击，做删除操作，更新adapter
            goodHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCollect(good.getGoodsId(),position);
                }
            });
        }
    }

    private void deleteCollect(int goodsId,int position) {
        String userName = FuLiCenterApplication.getInstance().getUserName();
        if (userName != null) {
            try {
                String path = new ApiParams()
                        .with(I.Collect.USER_NAME, userName)
                        .with(I.Collect.GOODS_ID, goodsId + "")
                        .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                mContext.executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                        responseDeleteListener(position),mContext.errorListener()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Response.Listener<MessageBean> responseDeleteListener(final int position) {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean != null & messageBean.isSuccess()) {
                    mCollectList.remove(position);
                    notifyDataSetChanged();
                    String userName = FuLiCenterApplication.getInstance().getUserName();
                    new DownloadCollectCountTask(mContext,userName).execute();
                }
                Toast.makeText(mContext,messageBean.getMsg(), Toast.LENGTH_SHORT).show();
            }
        };
    }


    @Override
    public int getItemCount() {
        Log.i("main","getItemCount.size"+mCollectList.size());
        return mCollectList==null?1:mCollectList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()-1){
            return I.TYPE_FOOTER;
        }else{
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<CollectBean> list) {
        if(mCollectList!=null && !mCollectList.isEmpty()){
            mCollectList.clear();
        }
        Log.i("main","mCollectList.size1"+mCollectList.size());
        mCollectList.addAll(list);
        Log.i("main","mCollectList.size2"+mCollectList.size());
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<CollectBean> list) {
        mCollectList.addAll(list);
        notifyDataSetChanged();
    }



    class GoodItemViewHolder extends ViewHolder{
        LinearLayout layoutCollect;
        NetworkImageView nivThumb;
        TextView tvGoodName;
        ImageView ivDelete;

        public GoodItemViewHolder(View itemView) {
            super(itemView);
            layoutCollect = (LinearLayout) itemView.findViewById(R.id.layout_collect);
            nivThumb = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
