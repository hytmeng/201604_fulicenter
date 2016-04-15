package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.easemob.util.HanziToPinyin;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.ucai.fulicenter.Constant;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.ChatActivity;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.RequestManager;
import cn.ucai.fulicenter.domain.User;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }

    public static UserBean getUserBeanInfo(String username) {
        UserBean user = FuLiCenterApplication.getInstance().getUserList().get(username);
        return user;
    }
    public static GroupBean getGroupBeanInfo(String groupname) {
        ArrayList<GroupBean> grouplist = FuLiCenterApplication.getInstance().getGrouplist();
        for (GroupBean group : grouplist) {
            if (group.getName().equals(groupname)) {
                return group;
            }
        }
        return null;
    }
    public static GroupBean getPublicGroupBeanInfo(String groupname) {
        ArrayList<GroupBean> grouplist = FuLiCenterApplication.getInstance().getPublicGroupList();
        for (GroupBean group : grouplist) {
            if (group.getName().equals(groupname)) {
                return group;
            }
        }
        return null;
    }
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
    public static void setGroupBeanAvatar(String groupName, NetworkImageView imageView) {
         GroupBean group = getGroupBeanInfo(groupName);
        imageView.setDefaultImageResId(R.drawable.default_avatar);
        if (group != null && group.getAvatar() != null) {
            imageView.setImageUrl(I.DOWNLOAD_AVATAR_URL + group.getAvatar(), RequestManager.getImageLoader());
        }else{
            imageView.setErrorImageResId(R.drawable.default_avatar);
        }
    }

    public static void setPublicGroupBeanAvatar(String groupName, NetworkImageView imageView) {
        GroupBean group = getPublicGroupBeanInfo(groupName);
        imageView.setDefaultImageResId(R.drawable.default_avatar);
        if (group != null && group.getAvatar() != null) {
            imageView.setImageUrl(I.DOWNLOAD_AVATAR_URL + group.getAvatar(), RequestManager.getImageLoader());
        }else{
            imageView.setErrorImageResId(R.drawable.default_avatar);
        }
    }
	public static void setUserBeanAvatar(String username, NetworkImageView imageView) {
        UserBean user = getUserBeanInfo(username);
        if (user != null) {
            setAvatar(imageView, user);
        } else {
            for (UserBean unUser : ChatActivity.currentMemvers) {
                if (unUser.getUserName().equals(username)) {
                    setAvatar(imageView, unUser);
                }
            }
        }
    }

    public static void setUserBeanAvatarNF(UserBean userBean, NetworkImageView imageView) {

        setAvatar(imageView, userBean);
    }
	/**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}

	public static void setCurrentUserBeanAvatar(NetworkImageView imageView) {
		UserBean user = FuLiCenterApplication.getInstance().getUser();
        setAvatar(imageView, user);
	}

    private static void setAvatar(NetworkImageView imageView, UserBean user) {
        imageView.setDefaultImageResId(R.drawable.default_avatar);
        if (user != null && user.getAvatar() != null) {
            imageView.setImageUrl(I.DOWNLOAD_AVATAR_URL + user.getAvatar(), RequestManager.getImageLoader());
        }else{
            imageView.setErrorImageResId(R.drawable.default_avatar);
        }
    }

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }


    public static void setUserBeanNick(String username, TextView textView) {
        UserBean user = getUserBeanInfo(username);

        if (user != null) {
            textView.setText(user.getNick());
        }else{
            for (UserBean unUser : ChatActivity.currentMemvers) {
                if (unUser.getUserName().equals(username)) {
                    textView.setText(unUser.getNick());
                }
            }
//            textView.setText(username);
        }
    }
    public static void setUserBeanNick(UserBean userBean,TextView textView){
        if(userBean != null){
            textView.setText(userBean.getNick());
        }else{
            textView.setText(userBean.getUserName());
        }
    }
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }

    public static void setCurrentUserBeanNick(TextView textView){
        UserBean user= FuLiCenterApplication.getInstance().getUser();
        if(textView != null){
            textView.setText(user.getNick());
        }
    }
    
    /**
     * 保存或更新某个用户
     * @param
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}


    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    public static void setUserBeanHearder(String username, UserBean user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUserName();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)
        ||username.equals(Constant.GROUP_USERNAME)){
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }
    
}
