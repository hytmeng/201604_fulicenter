package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by ucai on 2016/4/7.
 */
public class DownloadContactListTask extends BaseActivity {



    Context mContext;
    String userNane;
    int pageId;
    int pageSize;
    String path;

    public DownloadContactListTask(Context mContext, String userNane, int pageId, int pageSize) {
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
                    .getRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<UserBean[]>(path, UserBean[].class, responseListener(), errorListener()));
    }

    private Response.Listener<UserBean[]> responseListener() {
        return new Response.Listener<UserBean[]>() {
            @Override
            public void onResponse(UserBean[] contacrs) {
                ArrayList<UserBean> contact = new ArrayList<>();
//                HashMap<String, UserBean> contact = new HashMap<>();
                for (UserBean user:contacrs){
                    contact.add(user);
                }
                FuLiCenterApplication instance = FuLiCenterApplication.getInstance();
               ArrayList<UserBean> contactList = instance.getContactList();
                Log.i("main", "Listener:" + contactList.size());
                contactList.clear();
                Log.i("main", "Listener:" + contactList.size());
                contactList.addAll(contact);
                Log.i("main", "Listener:" + contactList.size());


                FuLiCenterApplication instance1 = new FuLiCenterApplication();
                HashMap<String, UserBean> userList = instance.getUserList();
                userList.clear();
                for (UserBean user2 : contact) {
                    userList.put(user2.getUserName(), user2);
                }


                Intent intent = new Intent("update_contactsList");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
