/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;
import com.easemob.chat.EMContactManager;

import java.util.ArrayList;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.UserUtils;

public class AddContactActivity extends BaseActivity{
	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText,mTextView;
	private Button searchBtn;
	private NetworkImageView avatar;
	private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;

	private Button msearch;
	private Button mIndicator;
	private TextView mtvNothing;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		mTextView = (TextView) findViewById(R.id.add_list_friends);
		
		editText = (EditText) findViewById(R.id.edit_note);
		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		String strUserName = getResources().getString(R.string.user_name);
		editText.setHint(strUserName);
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
		avatar = (NetworkImageView) findViewById(R.id.avatar);
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		msearch = (Button) findViewById(R.id.search);
		mIndicator = (Button) findViewById(R.id.indicator);
		mtvNothing = (TextView) findViewById(R.id.tvNothing);
		mContext=this;
		setListener();
	}

	private void setListener() {
		setSearchListener();
		setAddContactListener();
	}

	/**
	 *  添加contact
	 * @param
	 */
	String addFriendContext;
	private void setAddContactListener() {
		mIndicator.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = new ProgressDialog(mContext);
				String stri = getResources().getString(R.string.Is_sending_a_request);
				progressDialog.setMessage(stri);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();

                View layout = View.inflate(mContext, R.layout.edit_add_friend, null);
                final EditText requestAdd = (EditText) layout.findViewById(R.id.et_Add_friend);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                Log.i("main","AlertDialog");
                builder.setTitle("打个招呼吧")
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addFriendContext = requestAdd.getText().toString();
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            EMContactManager.getInstance().addContact(toAddUsername, addFriendContext);
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    String s1 = getResources().getString(R.string.send_successful);
                                                    Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } catch (final Exception e) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    Log.i("main","Request_add_buddy_failure");
                                                    String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                                                    Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
			}
		});
	}
	/**
	 * 查找contact
	 * @param
	 */
	private void setSearchListener() {
		msearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = editText.getText().toString();
				toAddUsername=name;


				if(TextUtils.isEmpty(name)) {
					String st = getResources().getString(R.string.Please_enter_a_username);
					startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", st));
					return;
				}
				if(FuLiCenterApplication.getInstance().getUserName().equals(name)){
					String str = getString(R.string.not_add_myself);
					startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", str));
					return;
				}
				try {
					String path = new ApiParams()
                            .with(I.User.USER_NAME, name)
                            .getRequestUrl(I.REQUEST_FIND_USER);
					executeRequest(new GsonRequest<UserBean>(path,UserBean.class,
							responseListener(),errorListener()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private Response.Listener<UserBean> responseListener() {
		return new Response.Listener<UserBean>() {
			@Override
			public void onResponse(UserBean userBean) {
				if (userBean != null) {
					mtvNothing.setVisibility(View.GONE);
					ArrayList<UserBean> users = FuLiCenterApplication.getInstance().getContactList();
					if (users.contains(userBean)) {
						Intent intent = new Intent();
						intent.setClass(mContext, UserProfileActivity.class);
						intent.putExtra("username", userBean.getUserName());
						mContext.startActivity(intent);

					}else {
						searchedUserLayout.setVisibility(View.VISIBLE);
//						nameText.setText(toAddUsername);
						UserUtils.setUserBeanAvatarNF(userBean, avatar);
						UserUtils.setUserBeanNick(userBean, nameText);
					}
				}else {
					mtvNothing.setVisibility(View.VISIBLE);
				}
			}
		};
	}


	/**
	 * 查找contact
	 * @param v
	 */
//	public void searchContact(View v) {
//		final String name = editText.getText().toString();
//		String saveText = searchBtn.getText().toString();
//
//		if (getString(R.string.button_search).equals(saveText)) {
//			toAddUsername = name;
//			if(TextUtils.isEmpty(name)) {
//				String st = getResources().getString(R.string.Please_enter_a_username);
//				startActivity(new Intent(this, AlertDialog.class).putExtra("msg", st));
//				return;
//			}
//			// TODO 从服务器获取此contact,如果不存在提示不存在此用户
//			//服务器存在此用户，显示此用户和添加按钮
//			searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);
//
//		}
//	}
	
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(View view){
		if(FuLiCenterApplication.getInstance().getUserName().equals(nameText.getText().toString())){
			String str = getString(R.string.not_add_myself);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", str));
			return;
		}
		
		if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().containsKey(nameText.getText().toString())){
		    //提示已在好友列表中，无需添加
		    if(EMContactManager.getInstance().getBlackListUsernames().contains(nameText.getText().toString())){
		        startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
		        return;
		    }
			String strin = getString(R.string.This_user_is_already_your_friend);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", strin));
			return;
		}
		
		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo写死了个reason，实际应该让用户手动填入
					String s = getResources().getString(R.string.Add_a_friend);
					EMContactManager.getInstance().addContact(toAddUsername, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}
	
	public void back(View v) {
		finish();
	}
}
