package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueChildGoodActivity;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by clawpo on 16/4/16.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context mContext;
    ArrayList<BoutiqueBean> mBoutiqueList;

    BoutiqueItemViewHolder boutiqueHolder;
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

    public BoutiqueAdapter(Context mContext, ArrayList<BoutiqueBean> list) {
        this.mContext = mContext;
        this.mBoutiqueList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType){
            case I.TYPE_ITEM:
                holder = new BoutiqueItemViewHolder(inflater.inflate(R.layout.item_boutique,parent,false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer,parent,false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder instanceof FooterViewHolder){
            footerHolder = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);
        }

        if(holder instanceof BoutiqueItemViewHolder){
            boutiqueHolder = (BoutiqueItemViewHolder) holder;
            final BoutiqueBean good = mBoutiqueList.get(position);
            boutiqueHolder.tvBoutiqueTitle.setText(good.getTitle());
            boutiqueHolder.tvBoutiqueName.setText(good.getName());
            boutiqueHolder.tvBoutiqueDescription.setText(good.getDescription());
            String path= I.DOWNLOAD_BOUTIQUE_IMG_URL+good.getImageurl();
            ImageUtils.setThumb(path,boutiqueHolder.nivThumb);



            boutiqueHolder.layoutBoutique.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BoutiqueChildGoodActivity.class);
                    intent.putExtra(I.Boutique.CAT_ID, good.getId());
                    intent.putExtra(I.Boutique.NAME, good.getName());
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mBoutiqueList==null?1:mBoutiqueList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()-1){
            return I.TYPE_FOOTER;
        }else{
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<BoutiqueBean> list) {
        if(mBoutiqueList!=null){
            mBoutiqueList.clear();
        }
        mBoutiqueList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<BoutiqueBean> list) {
        mBoutiqueList.addAll(list);
        notifyDataSetChanged();
    }



    class BoutiqueItemViewHolder extends ViewHolder{
        RelativeLayout layoutBoutique;
        NetworkImageView nivThumb;
        TextView tvBoutiqueTitle;
        TextView tvBoutiqueName;
        TextView tvBoutiqueDescription;

        public BoutiqueItemViewHolder(View itemView) {
            super(itemView);
            layoutBoutique = (RelativeLayout) itemView.findViewById(R.id.layout_boutique);
            nivThumb = (NetworkImageView) itemView.findViewById(R.id.niv_boutique_thumb);
            tvBoutiqueTitle = (TextView) itemView.findViewById(R.id.tv_good1_name);
            tvBoutiqueName = (TextView) itemView.findViewById(R.id.tv_good2_name);
            tvBoutiqueDescription = (TextView) itemView.findViewById(R.id.tv_good3_name);
        }
    }
}
