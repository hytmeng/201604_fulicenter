/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.listener.OnSetAvatarListener;
import cn.ucai.fulicenter.utils.NetUtil;

public class NewGroupActivity extends BaseActivity {
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox checkBox;
    private CheckBox memberCheckbox;
    private LinearLayout openInviteContainer;

    OnSetAvatarListener mSetAvatarListener;
    private ImageView groupAvatar;
    private Button btnSave;
    final static int Action_Create_Group=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();
        setListener();
    }

    private void initView() {
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        checkBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);

        groupAvatar = (ImageView) findViewById(R.id.iv_avatar);
        btnSave = (Button) findViewById(R.id.btSave);
    }

    private void setListener() {
        setGroupAvatarListener();
        setCheckBoxChangeListener();
        setSaveClickListener();
    }

    private void setSaveClickListener() {

       btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
                String name = groupNameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Intent intent = new Intent(NewGroupActivity.this, AlertDialog.class);
                    intent.putExtra("msg", str6);
                    startActivity(intent);
                } else {
                    // 进通讯录选人
                    startActivityForResult(new Intent(NewGroupActivity.this, GroupPickContactsActivity.class).putExtra("groupName", name), Action_Create_Group);
                }
            }
        });
    }

    private void setCheckBoxChangeListener() {
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openInviteContainer.setVisibility(View.INVISIBLE);
                } else {
                    openInviteContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setGroupAvatarListener() {
        findViewById(R.id.Layout_groupAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetAvatarListener = new OnSetAvatarListener(NewGroupActivity.this, R.id.layout_groupCreate, getGroupName(), "group_icon");
            }
        });
    }

    /**
     * @param
     */
//	public void save(View v) {
//		String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
//		String name = groupNameEditText.getText().toString();
//		if (TextUtils.isEmpty(name)) {
//			Intent intent = new Intent(this, AlertDialog.class);
//			intent.putExtra("msg", str6);
//			startActivity(intent);
//		} else {
//			// 进通讯录选人
//			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), 0);
//		}
//	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == Action_Create_Group) {
                //新建群组
                createGroup(data);
            } else {
                mSetAvatarListener.setAvatar(requestCode, data, groupAvatar);
            }
        } else {
            return;
        }
    }

    private void createGroup(Intent data) {
        groupCreateSaveDialog();
        String groupName = groupNameEditText.getText().toString().trim();
        try {
            String path = new ApiParams()
                    .with(I.Group.NAME, groupName)
                    .getRequestUrl(I.REQUEST_FIND_GROUP);
            executeRequest(new GsonRequest<GroupBean>(path,GroupBean.class,
                    responseFindGroup(data.getStringArrayExtra("newmembers")),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<GroupBean> responseFindGroup(final String[] members) {
        return new Response.Listener<GroupBean>() {
            @Override
            public void onResponse(GroupBean groupBean) {
                if (groupBean != null) {
                    progressDialog.dismiss();
                    groupNameEditText.findFocus();
                    groupNameEditText.setError(getResources().getString(R.string.Group_name_existed));
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            // 调用sdk创建群组方法
                            final String st2 = getResources().getString(R.string.Failed_to_create_groups);
                            String groupName = groupNameEditText.getText().toString().trim();
                            String desc = introductionEditText.getText().toString();


                            ArrayList<GroupBean> grouplist = FuLiCenterApplication.getInstance().getGrouplist();
                            try {
                                EMGroup group;
                                if (checkBox.isChecked()) {
                                    //创建公开群，此种方式创建的群，可以自由加入
                                    //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                                    group = EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                                } else {
                                    //创建不公开群
                                    group = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                                }
                                String userName = FuLiCenterApplication.getInstance().getUserName();
                                String groupId = group.getGroupId();
                                boolean isPublic = checkBox.isChecked();
                                boolean isExam = !memberCheckbox.isChecked();
                                StringBuffer buffer = new StringBuffer();
                                for (String member : members) {
                                    buffer.append(member).append(",");
                                }
                                buffer.append(userName);
                                GroupBean toCreateGroup = new GroupBean(groupId, groupName, desc, userName, isPublic, isExam, buffer.toString());

                                boolean isSuccess = NetUtil.createGroup(toCreateGroup);
                                if (isSuccess) {
                                    try {
                                        isSuccess = NetUtil.uploadAvatar(NewGroupActivity.this, "group_icon", groupName);
                                        if (isSuccess) {
                                            toCreateGroup.setAvatar("group_icon/" + groupName + ".jpg");
                                            Intent intent = new Intent("update_GroupList").putExtra("group", toCreateGroup);
                                            setResult(RESULT_OK,intent);
                                        } else {
                                            Toast.makeText(NewGroupActivity.this, R.string.upload_avatar_failed, Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(NewGroupActivity.this,R.string.Failed_to_create_groups,Toast.LENGTH_SHORT).show();

                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            } catch (final EaseMobException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        };
    }

//	private void checkGroupName(String groupName) {
//		try {
//			String path = new ApiParams()
//                    .with(I.Group.NAME, groupName)
//                    .getRequestUrl(I.REQUEST_FIND_GROUP);
//			executeRequest(new GsonRequest<GroupBean>(path,GroupBean.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

    private void groupCreateSaveDialog() {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void back(View view) {
        finish();
    }

    public String getGroupName() {
        String groupName = groupNameEditText.getText().toString();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "请先输入群组名", Toast.LENGTH_SHORT).show();
        }
        return groupName;
    }
}
