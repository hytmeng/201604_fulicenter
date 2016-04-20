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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.listener.OnSetAvatarListener;
import cn.ucai.fulicenter.utils.NetUtil;
import cn.ucai.fulicenter.utils.Utils;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {
	private static final String TAG = RegisterActivity.class.getName();
	RegisterActivity registerActivity;
	ProgressDialog pd;
    OnSetAvatarListener mSetAvatarListener;
	String username,  usernick,  pwd;

	private EditText userNameEditText;
	private EditText userNickEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	private ImageView userAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		registerActivity=this;
		initView();
		setListener();
	}

	private void setListener() {
		setRegisterOnClickListener();
//		setLoginOnClickListener();
        setAvatarClickListener();
    }

    private void setAvatarClickListener() {
        findViewById(R.id.Layout_userAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetAvatarListener = new OnSetAvatarListener(registerActivity, R.id.layout_Resiger, getUserName(), "user_avatar");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        mSetAvatarListener.setAvatar(requestCode,data,userAvatar);
    }

//    private void setLoginOnClickListener() {
//		findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(registerActivity,LoginActivity.class));
//			}
//		});
//	}

	private void initView() {
		userNameEditText = (EditText) findViewById(R.id.username);
		userNickEditText = (EditText) findViewById(R.id.nick);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
		userAvatar = (ImageView) findViewById(R.id.iv_avatar);
	}

	/**
	 * 注册
	 * 
	 * @param
	 */
	public void setRegisterOnClickListener() {
		findViewById(R.id.BtnRegister).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username = userNameEditText.getText().toString().trim();
                usernick = userNickEditText.getText().toString().trim();
				pwd = passwordEditText.getText().toString().trim();
				String confirm_pwd = confirmPwdEditText.getText().toString().trim();
				if (TextUtils.isEmpty(username)) {
					userNameEditText.requestFocus();
                    userNameEditText.setError(getResources().getString(R.string.User_name_cannot_be_empty));
                    return;
				}	if (!username.matches("[\\w][\\w\\d_]+")) {
                    userNameEditText.requestFocus();
                    userNameEditText.setError(getResources().getString(R.string.User_name_cannot_be_empty));
                    return;
                }  else if (TextUtils.isEmpty(usernick)) {
                   userNickEditText.requestFocus();
                    userNickEditText.setError(getResources().getString(R.string.User_name_cannot_be_empty));
                    return;
                }else if (TextUtils.isEmpty(pwd)) {
					passwordEditText.requestFocus();
                    passwordEditText.setError(getResources().getString(R.string.Password_cannot_be_empty));
					return;
				} else if (TextUtils.isEmpty(confirm_pwd)) {
					confirmPwdEditText.requestFocus();
                    confirmPwdEditText.setError(getResources().getString(R.string.Confirm_password_cannot_be_empty));
					return;
				} else if (!pwd.equals(confirm_pwd)) {

                    confirmPwdEditText.requestFocus();
                    confirmPwdEditText.setError(getResources().getString(R.string.Two_input_password));
					return;
				}

				if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
					pd = new ProgressDialog(registerActivity);
					pd.setMessage(getResources().getString(R.string.Is_the_registered));
					pd.show();
				}
				registerApp();
            }
		});
	}

    private void registerApp() {

		try {
			String path = new ApiParams()
                    .with(I.User.USER_NAME, username)
                    .with(I.User.NICK, usernick)
                    .with(I.User.PASSWORD, pwd)
                    .getRequestUrl(I.REQUEST_REGISTER);
			executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseRegisterListener(), errorListener()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Response.Listener<MessageBean> responseRegisterListener() {
		return new Response.Listener<MessageBean>() {
			@Override
			public void onResponse(MessageBean messageBean) {
				if (messageBean != null && messageBean.isSuccess()) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								boolean isSuccess= NetUtil.uploadAvatar(registerActivity,"user_avatar",username);
								if (isSuccess) {
									registerEMServer();
								}else {
									pd.dismiss();
									Utils.showToast(registerActivity, R.string.upload_avatar_failed, Toast.LENGTH_SHORT);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}).start();
				}else{
					try {
						String path = new ApiParams()
                                .with(I.User.USER_NAME, username)
                                .getRequestUrl(I.REQUEST_UNREGISTER);
						executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseUnRegisterListener(), errorListener()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					pd.dismiss();
					Utils.showToast(registerActivity, R.string.Registration_failed, Toast.LENGTH_SHORT);
				}
			}
		};
	}

	private Response.Listener<MessageBean> responseUnRegisterListener() {
		return new Response.Listener<MessageBean>() {
			@Override
			public void onResponse(MessageBean messageBean) {
				if (!messageBean.isSuccess()) {
					Utils.showToast(registerActivity, R.string.cancel_register_failed, Toast.LENGTH_SHORT);
				}
			}
		};
	}

	public void back(View view) {
		finish();
	}

	public void registerEMServer() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(username, pwd);
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							// 保存用户名
							FuLiCenterApplication.getInstance().setUserName(username);
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							int errorCode=e.getErrorCode();
							if(errorCode==EMError.NONETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXISTS){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.UNAUTHORIZED){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.ILLEGAL_USER_NAME){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();
	}

    private String getUserName() {
        String userName = userNameEditText.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(registerActivity,"请先输入用户名",Toast.LENGTH_SHORT).show();
        }
        return userName;
    }

}
