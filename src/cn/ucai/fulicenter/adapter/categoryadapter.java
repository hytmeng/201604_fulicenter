package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by ucai on 2016/4/19.
 */
public class Categoryadapter extends BaseExpandableListAdapter{

    Context mContext;
    ArrayList<CategoryGroupBean> categoryList;
    ArrayList<ArrayList<CategoryChildBean>> childList;

    public Categoryadapter(Context mContext, ArrayList<CategoryGroupBean> categoryList, ArrayList<ArrayList<CategoryChildBean>> childList) {
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return categoryList.size() == 0 ? 0 : categoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size()==0?0:childList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        return categoryList.get(groupPosition);
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return categoryList.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroupHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_category_group, null);
            holder = new ViewGroupHolder();
            holder.ivGroupSelect = (ImageView) convertView.findViewById(R.id.iv_group_select);
            holder.nivGroupThumb = (NetworkImageView) convertView.findViewById(R.id.niv_group_thumb);
            holder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewGroupHolder) convertView.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        holder.tvGroupName.setText(group.getName());
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_GROUP_IMAGE_URL + group.getImageUrl();
        ImageUtils.setThumb(url, holder.nivGroupThumb);

        if (isExpanded) {
            holder.ivGroupSelect.setImageResource(R.drawable.expand_off);
        } else {
            holder.ivGroupSelect.setImageResource(R.drawable.expand_on);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_category_child, null);
            holder = new ViewChildHolder();
            holder.layoutItem = (RelativeLayout) convertView.findViewById(R.id.layout_child_category);
            holder.nivChildThumb = (NetworkImageView) convertView.findViewById(R.id.niv_child_thumb);
            holder.tvChildName = (TextView) convertView.findViewById(R.id.tv_child_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewChildHolder) convertView.getTag();
        }
        final CategoryChildBean child = getChild(groupPosition, childPosition);
        holder.tvChildName.setText(child.getName());
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL + child.getImageUrl();
        ImageUtils.setThumb(url, holder.nivChildThumb);

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CategoryChildActivity.class);
                intent.putExtra(I.CategoryChild.CAT_ID, child.getId());
                intent.putExtra(I.CategoryChild.NAME, categoryList.get(groupPosition).getName());
                intent.putExtra("childList", childList.get(groupPosition));
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    public void addItems(ArrayList<CategoryGroupBean> grouplist, ArrayList<ArrayList<CategoryChildBean>> childlist) {
        categoryList.addAll(grouplist);
        childList.addAll(childlist);
        notifyDataSetChanged();
    }

    class ViewGroupHolder{
        NetworkImageView nivGroupThumb;
        TextView tvGroupName;
        ImageView ivGroupSelect;

    }

    class ViewChildHolder {
        RelativeLayout layoutItem;
        NetworkImageView nivChildThumb;
        TextView tvChildName;

    }
}
