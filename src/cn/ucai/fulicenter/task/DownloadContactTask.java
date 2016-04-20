package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.ContactBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by ucai on 2016/4/7.
 */
public class DownloadContactTask extends BaseActivity {
    Context mContext;
   String userNane;
    int pageId;
    int pageSize;
    String path;

    public DownloadContactTask(Context mContext, String userNane, int pageId, int pageSize) {
        this.mContext = mContext;
        this.userNane = userNane;
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath() {
        try {
            path=new ApiParams()
                    .with(I.User.USER_NAME,userNane)
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,pageSize+"")
                    .getRequestUrl(I.REQUEST_DOWNLOAD_CONTACTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<ContactBean[]>(path, ContactBean[].class, responseListener(), errorListener()));
    }

    private Response.Listener<ContactBean[]> responseListener() {
        return new Response.Listener<ContactBean[]>() {
            @Override
            public void onResponse(ContactBean[] contacrs) {
                if (contacrs == null) {
                    return;
                }
                HashMap<Integer,ContactBean> map=new HashMap<>();
                for (ContactBean contact:contacrs){
                    map.put(contact.getCuid(), contact);
                }
                FuLiCenterApplication instance = FuLiCenterApplication.getInstance();
                HashMap<Integer, ContactBean> contactMap = instance.getContacts();
                contactMap.putAll(map);
                Intent intent = new Intent("update_contacts");
                mContext.sendBroadcast(intent);
            }
        };
    }
}
