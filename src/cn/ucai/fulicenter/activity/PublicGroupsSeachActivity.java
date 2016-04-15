package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.UserUtils;

public class PublicGroupsSeachActivity extends BaseActivity{
    private RelativeLayout containerLayout;
    private EditText idET;
    NetworkImageView ivAvatar;
    Button btnSearch;
    private TextView nameText;
//    public static EMGroup searchedGroup;
public static GroupBean searchedGroup;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_public_groups_search);
        
        containerLayout = (RelativeLayout) findViewById(R.id.rl_searched_group);
        idET = (EditText) findViewById(R.id.et_search_id);
        nameText = (TextView) findViewById(R.id.name);
        ivAvatar = (NetworkImageView) findViewById(R.id.avatar);

        searchedGroup = null;
    }



    /**
     * 搜索
     * @param v
     */
    ProgressDialog pd;
    public void searchGroup(View v){
        if(TextUtils.isEmpty(idET.getText())){
            return;
        }
        
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.searching));
        pd.setCancelable(false);
        pd.show();

        String groupName = idET.getText().toString();
        try {
            String path = new ApiParams()
                    .with(I.Group.NAME, groupName)
                    .getRequestUrl(I.REQUEST_FIND_GROUP);
            executeRequest(new GsonRequest<GroupBean>(path,GroupBean.class,
                    responseFindGroupListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        new Thread(new Runnable() {
//
//            public void run() {
//                try {
////                    searchedGroup = EMGroupManager.getInstance().getGroupFromServer(idET.getText().toString());
//                    ArrayList<GroupBean> searchedGroupList = FuLiCenterApplication.getInstance().getPublicGroupList();
//                    for (GroupBean bean : searchedGroupList) {
//                        if (bean.getName().equals(idET.getText().toString())) {
//                            searchedGroup = bean;
//                        }
//                    }
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            containerLayout.setVisibility(View.VISIBLE);
//                            nameText.setText(searchedGroup.getName());
//                            UserUtils.setPublicGroupBeanAvatar(searchedGroup.getName(), ivAvatar);
//                        }
//                    });
//
//                } catch (final Exception e) {
//                    e.printStackTrace();
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            searchedGroup = null;
//                            containerLayout.setVisibility(View.GONE);
////                            if(e.getErrorCode() == EMError.GROUP_NOT_EXIST){
////                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_not_existed), 0).show();
////                            }else{
////                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_search_failed) + " : " + getString(R.string.connect_failuer_toast), 0).show();
////                            }
//                        }
//                    });
//                }
//            }
//        }).start();
        
    }

    private Response.Listener<GroupBean> responseFindGroupListener() {
        return new Response.Listener<GroupBean>() {
            @Override
            public void onResponse(GroupBean groupBean) {
                if (groupBean != null) {
                    searchedGroup = groupBean;
                    pd.dismiss();
                    containerLayout.setVisibility(View.VISIBLE);
                    nameText.setText(searchedGroup.getName());
                    UserUtils.setPublicGroupBeanAvatar(searchedGroup.getName(), ivAvatar);
                } else {
                    pd.dismiss();
                    searchedGroup = null;
                    containerLayout.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_not_existed), 0).show();
                }
            }
        };
    }


    /**
     * 点击搜索到的群组进入群组信息页面
     * @param view
     */
    public void enterToDetails(View view){
        startActivity(new Intent(this, GroupSimpleDetailActivity.class));
    }
}
