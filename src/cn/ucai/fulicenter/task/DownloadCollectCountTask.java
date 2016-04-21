package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by ucai on 2016/4/21.
 */
public class DownloadCollectCountTask extends BaseActivity{

    Context mContext;
    String userNane;
    String path;

    public DownloadCollectCountTask(Context mContext, String userNane) {
        this.mContext = mContext;
        this.userNane = userNane;
        initPath();
    }

    private void initPath() {
        try {
            path=new ApiParams()
                    .with(I.User.USER_NAME,userNane)
                    .getRequestUrl(I.REQUEST_FIND_COLLECT_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseListener(), errorListener()));
    }

    private Response.Listener<MessageBean> responseListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean message) {
                if (message == null) {
                    return;
                }

                if (message.isSuccess()) {
                     FuLiCenterApplication.getInstance().setCollectCount(Integer.parseInt(message.getMsg()));
                } else {
                    FuLiCenterApplication.getInstance().setCollectCount(0);
                }

                Intent intent = new Intent("update_collectCount");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
