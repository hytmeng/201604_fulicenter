package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by ucai on 2016/4/7.
 */
public class DownloadPublicGroupListTask extends BaseActivity {

Context mContext;
    String userNane;
    String path;
    int pageId;
    static final int PAGE_SIZE=20;

    public DownloadPublicGroupListTask(Context mContext, String userNane,int pageId) {
        this.mContext = mContext;
        this.userNane = userNane;
        this.pageId = pageId;
        initPath();
    }

    private void initPath() {
        try {
            path=new ApiParams()
                    .with(I.User.USER_NAME,userNane)
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,PAGE_SIZE+"")
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<GroupBean[]>(path, GroupBean[].class, responseListener(), errorListener()));
    }

    private Response.Listener<GroupBean[]> responseListener() {
        return new Response.Listener<GroupBean[]>() {
            @Override
            public void onResponse(GroupBean[] Groups) {
                ArrayList<GroupBean> groupBeen = new ArrayList<>();
                for (GroupBean Group:Groups){
                    groupBeen.add(Group);
                }
                FuLiCenterApplication instance = FuLiCenterApplication.getInstance();
                ArrayList<GroupBean> contactMap = instance.getPublicGroupList();
                contactMap.addAll(groupBeen);
                Intent intent = new Intent("update_PublicGroupList");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
