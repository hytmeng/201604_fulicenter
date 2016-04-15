package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
public class DownloadGroupListTask extends BaseActivity {
    
Context mContext;
    String userNane;
    String path;

    public DownloadGroupListTask(Context mContext, String userNane) {
        this.mContext = mContext;
        this.userNane = userNane;
        initPath();
    }

    private void initPath() {
        try {
            path=new ApiParams()
                    .with(I.User.USER_NAME,userNane)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUPS);
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
                ArrayList<GroupBean> contactMap = instance.getGrouplist();
                contactMap.addAll(groupBeen);
                Log.i("main", "Response.Listener<ContactBean[]>+group"+contactMap.size());
                Intent intent = new Intent("update_GroupList");
                mContext.sendBroadcast(intent);
            }
        };
    }
}
