package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.viewholder.FooterViewHolder;

/**
 * Created by ucai on 2016/4/16.
 */
public class GoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    ArrayList<NewGoodBean> mGoodsList;

    GoodViewHolder mGoodViewHolder;
    FooterViewHolder mFooterViewHolder;

    private String footerText;
    private boolean isMore;

    public GoodsAdapter(Context mContext, ArrayList<NewGoodBean> mGoodsList) {
        this.mContext = mContext;
        this.mGoodsList = mGoodsList;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layout = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_ITEM:
                holder = new GoodViewHolder(layout.inflate(R.layout.item_new_goods, parent, false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(layout.inflate(R.layout.item_footer, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FooterViewHolder) {
            mFooterViewHolder = (FooterViewHolder) holder;
            mFooterViewHolder.tvFooterText.setText(footerText);
            mFooterViewHolder.tvFooterText.setVisibility(View.VISIBLE);
        }
        if (position == mGoodsList.size()) {
            return;
        }
        if (holder instanceof GoodViewHolder) {
            mGoodViewHolder = (GoodViewHolder) holder;
            NewGoodBean newgood = mGoodsList.get(position);
            mGoodViewHolder.tvGoodsDetails.setText(newgood.getGoodsName());
            mGoodViewHolder.tvGoodsPrice.setText(newgood.getCurrencyPrice());
            ImageUtils.setNewGoodThumb(newgood.getGoodsThumb(),mGoodViewHolder.ivNewGood);
        }
    }

    @Override
    public int getItemCount() {
        return mGoodsList==null?0:mGoodsList.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == getItemCount()) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<NewGoodBean> list) {
        if (mGoodsList != null && !mGoodsList.isEmpty()) {
            mGoodsList.clear();
        }
        mGoodsList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<NewGoodBean> list) {
        mGoodsList.addAll(list);
        notifyDataSetChanged();
    }


    class GoodViewHolder extends RecyclerView.ViewHolder {
        LinearLayout goodItem;
        NetworkImageView ivNewGood;
        TextView tvGoodsDetails;
        TextView tvGoodsPrice;
        public GoodViewHolder(View itemView) {
            super(itemView);
            goodItem = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivNewGood = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodsDetails = (TextView) itemView.findViewById(R.id.tv_name);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tvGoodPrice);
        }
    }
}
