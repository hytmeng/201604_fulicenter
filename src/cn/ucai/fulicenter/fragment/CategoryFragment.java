package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.Categoryadapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by ucai on 2016/4/19.
 */
public class CategoryFragment extends Fragment {
    FuLiCenterMainActivity mContext;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList< ArrayList<CategoryChildBean>> mChildList;
    ExpandableListView melvCategory;
    Categoryadapter mAdapter;
    ImageView mivGroupindicator;

    int groupCount;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_category,null);
        initView(layout);
        initData();
//        setListener();
        return layout;
    }

//    private void setListener() {
//        //分类列表项大类点击事件
//        setcategoryGroupClickListener();
//        setcategoryExpandOffClickListener();
//        setcategoryGroupExpandOnListener();
//    }
//
//    private void setcategoryGroupExpandOnListener() {
//        melvCategory.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                mivGroupindicator.setImageResource(R.drawable.expand_off);
//            }
//        });
//    }
//
//    private void setcategoryExpandOffClickListener() {
//        melvCategory.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                mivGroupindicator.setImageResource(R.drawable.expand_on);
//            }
//        });
//    }
//
//    private void setcategoryGroupClickListener() {
//        melvCategory.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                Log.i("main","mivGroupindicator"+mivGroupindicator);
//                mivGroupindicator = (ImageView) v.findViewById(R.id.iv_group_select);
//                return false;
//            }
//        });
//    }

    private void initData() {

        try {
            String pathGroup = new ApiParams().getRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP);
            mContext.executeRequest(new GsonRequest<CategoryGroupBean[]>(pathGroup,CategoryGroupBean[].class,
                    responseFindCategoryGroup(),mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private Response.Listener<CategoryGroupBean[]> responseFindCategoryGroup() {
        return new Response.Listener<CategoryGroupBean[]>() {
            @Override
            public void onResponse(CategoryGroupBean[] categoryGroupBeen) {
                if (categoryGroupBeen != null) {
                   mGroupList= Utils.array2List(categoryGroupBeen);
                    int i=0;
                   for (CategoryGroupBean group:mGroupList ) {
                        final ArrayList<CategoryChildBean> childBeen = new ArrayList<>();
                        try {
                            mChildList.add(i, new ArrayList<CategoryChildBean>());
                            String pathChild = new ApiParams()
                                    .with(I.CategoryChild.PARENT_ID, group.getId()+"")
                                    .with(I.PAGE_ID, I.PAGE_ID_DEFAULT + "")
                                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                                    .getRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN);
                            mContext.executeRequest(new GsonRequest<CategoryChildBean[]>(pathChild, CategoryChildBean[].class,
                                    new Response.Listener<CategoryChildBean[]>() {
                                        @Override
                                        public void onResponse(CategoryChildBean[] categoryChildBeen) {
                                            if (categoryChildBeen != null) {
                                                ArrayList<CategoryChildBean> childBeen1 = Utils.array2List(categoryChildBeen);
                                                if (childBeen1 != null) {
                                                    childBeen.addAll(childBeen1);
                                                }
                                            }
                                        }
                                    }, mContext.errorListener()));
                            mChildList.set(i,childBeen);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                       i++;
                       groupCount++;
                    }
                }
                if (mGroupList.size() ==groupCount) {
                    mAdapter.addItems(mGroupList, mChildList);
                }
            }
        };
    }



    private void initView(View layout) {
        melvCategory = (ExpandableListView) layout.findViewById(R.id.elvCategory);
        mivGroupindicator = (ImageView) layout.findViewById(R.id.iv_group_select);
        melvCategory.setGroupIndicator(null);
        mGroupList = new ArrayList<>();
        mChildList = new ArrayList<>();
        mAdapter = new Categoryadapter(mContext, mGroupList, mChildList);
        melvCategory.setAdapter(mAdapter);

    }
}
