package cn.ucai.fulicenter.fragment;

import android.content.Intent;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_persional_center, null);
        initview(layout);
        initData();
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

    private void initData() {
        if (mUserName != null) {
            mUserName.setText(mCurrentUserName);
            UserUtils.setCurrentUserBeanAvatar(mUserAvatar);
        }
    }

    private void initview(View layout) {
        mCurrentUserName = FuLiCenterApplication.getInstance().getUserName();
        mUserAvatar = (NetworkImageView) layout.findViewById(R.id.nivUserAvatar);
        mUserName = (TextView) layout.findViewById(R.id.tvUserName);
        mbtnSetting = (Button) layout.findViewById(R.id.btnSetting);
    }
}
