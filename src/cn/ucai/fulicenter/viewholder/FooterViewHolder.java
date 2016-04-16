package cn.ucai.fulicenter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by ucai on 2016/4/16.
 */
public class FooterViewHolder extends RecyclerView.ViewHolder{
  public   TextView tvFooterText;

    public FooterViewHolder(View itemView) {
        super(itemView);
        tvFooterText = (TextView) itemView.findViewById(R.id.layout_footer);
    }
}
