package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.FooterViewHolder;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by clawpo on 16/4/16.
 */
public class CartAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context mContext;
    ArrayList<CartBean> mCartList;

    CartItemViewHolder cartHolder;
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

    public CartAdapter(Context mContext, ArrayList<CartBean> list) {
        this.mContext = mContext;
        this.mCartList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType){
            case I.TYPE_ITEM:
                holder = new CartItemViewHolder(inflater.inflate(R.layout.item_cart,parent,false));
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

        if(holder instanceof CartItemViewHolder){
            cartHolder = (CartItemViewHolder) holder;
            final CartBean good = mCartList.get(position);
            cartHolder.tvName.setText(good.getGoods().getGoodsName());
            cartHolder.tvPrice.setText("￥"+good.getGoods().getCurrencyPrice());
            cartHolder.tvCartCount.setText("("+good.getCount()+")");
            String path= I.DOWNLOAD_BOUTIQUE_IMG_URL+good.getGoods().getGoodsImg();
            ImageUtils.setThumb(path,cartHolder.nivThumb);


            AddOrDelListener listener = new AddOrDelListener(good);
            cartHolder.ivAddCart.setOnClickListener(listener);
            cartHolder.ivDelCart.setOnClickListener(listener);
            cartHolder.rbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    good.setChecked(isChecked);
                    new UpdateCartTask(mContext, good).execute();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCartList==null?1:mCartList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()-1){
            return I.TYPE_FOOTER;
        }else{
            return I.TYPE_ITEM;
        }
    }

//    public void initItems(ArrayList<CartBean> list) {
//        if(mCartList!=null){
//            mCartList.clear();
//        }
//        mCartList.addAll(list);
//        notifyDataSetChanged();
//    }

//    public void addItems(ArrayList<CartBean> list) {
//        mCartList.addAll(list);
//        notifyDataSetChanged();
//    }



    class CartItemViewHolder extends ViewHolder{
        LinearLayout layoutCart;
        CheckBox rbCheck;
        NetworkImageView nivThumb;
        TextView tvName;
        TextView tvPrice;
        ImageView ivAddCart;
        ImageView ivDelCart;
        TextView tvCartCount;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            layoutCart = (LinearLayout) itemView.findViewById(R.id.layout_cart);
            nivThumb = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvCartCount = (TextView) itemView.findViewById(R.id.tvCartCount);
            ivAddCart = (ImageView) itemView.findViewById(R.id.ivAddCart);
            ivDelCart = (ImageView) itemView.findViewById(R.id.ivDelCart);
            rbCheck = (CheckBox) itemView.findViewById(R.id.rb_Check);
        }
    }

    private class AddOrDelListener implements View.OnClickListener{
        CartBean cartBean;

        public AddOrDelListener(CartBean cartBean) {
            this.cartBean = cartBean;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivAddCart:
                    Utils.addCart(mContext, cartBean.getGoods());
                    break;
                case R.id.ivDelCart:
                    Utils.delCart(mContext,cartBean.getGoods());
                    break;
            }
        }
    }
}
