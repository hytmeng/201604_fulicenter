package cn.ucai.fulicenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.UserUtils;


/**
 * Created by ucai on 2016/4/20.
 */
public class PersionalCenterFragment extends Fragment {
    FuLiCenterMainActivity mContext;
    NetworkImageView mUserAvatar;
    TextView mUserName;
    String mCurrentUserName;
    Button mbtnSetting;
    TextView mCollectCount;
    String count;

    CollectCountChangerReceiver mCollectCountChangerReceiver;
    UserChangerReceiver mUserChangerReceiver;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_persional_center, null);
        CollectCountChangerReceiverRegister();
        UserChangerReceiverRegister();
        initview(layout);
        setListener();
        return layout;
    }

    private void setListener() {
        mbtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
            }
        });
    }



    private void initview(View layout) {
        mCurrentUserName = FuLiCenterApplication.getInstance().getUserName();
        mUserAvatar = (NetworkImageView) layout.findViewById(R.id.nivUserAvatar);
        mUserName = (TextView) layout.findViewById(R.id.tvUserName);
        mbtnSetting = (Button) layout.findViewById(R.id.btnSetting);
        mCollectCount = (TextView) layout.findViewById(R.id.ll_Collect_Baby_Count);
    }

    class CollectCountChangerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count1 = FuLiCenterApplication.getInstance().getCollectCount();
            count = count1 + "";
            refresh();
        }
    }

    private void CollectCountChangerReceiverRegister() {
        mCollectCountChangerReceiver = new CollectCountChangerReceiver();
        IntentFilter filter = new IntentFilter("update_collectCount");
        mContext.registerReceiver(mCollectCountChangerReceiver, filter);
    }

    class UserChangerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String userName = FuLiCenterApplication.getInstance().getUserName();
            new DownloadCollectCountTask(mContext, userName);
            refresh();
        }
    }

    private void refresh() {
        String userName = FuLiCenterApplication.getInstance().getUserName();
        if (userName != null) {
            mUserName.setText(userName);
            UserUtils.setCurrentUserBeanAvatar(mUserAvatar);
        }
        if (count != null) {
            mCollectCount.setText(count);
        }

    }

    private void UserChangerReceiverRegister() {
        mUserChangerReceiver = new UserChangerReceiver();
        IntentFilter filter = new IntentFilter("update_user");
        mContext.registerReceiver(mUserChangerReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCollectCountChangerReceiver != null) {
            mContext.unregisterReceiver(mCollectCountChangerReceiver);
        }
        if (mUserChangerReceiver != null) {
            mContext.unregisterReceiver(mUserChangerReceiver);
        }
    }
}
