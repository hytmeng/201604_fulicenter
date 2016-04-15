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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.task.DownloadPublicGroupListTask;
import cn.ucai.fulicenter.utils.UserUtils;

public class PublicGroupsActivity extends BaseActivity {
	private ProgressBar pb;
	private ListView listView;
	private GroupsAdapter adapter;

//    	private List<EMGroupInfo> groupsList;
    private ArrayList<GroupBean> groupsList;
    private boolean isLoading;
	private boolean isFirstLoading = true;
	private boolean hasMoreData = true;
	private String cursor;

	private final int pagesize = 20;
    private LinearLayout footLoadingLayout;
    private ProgressBar footLoadingPB;
    private TextView footLoadingText;
    private Button searchBtn;

    PublicGroupChangeReceiver mPublicGroupChangeReceiver;
    int pageId=0;
    private Context mContext;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mContext=this;

        registerPublicGroupReceiver();
		setContentView(R.layout.activity_public_groups);

		pb = (ProgressBar) findViewById(R.id.progressBar);
		listView = (ListView) findViewById(R.id.list);
//		groupsList = new ArrayList<EMGroupInfo>();
        groupsList = new ArrayList<GroupBean>();
        searchBtn = (Button) findViewById(R.id.btn_search);
		
		View footView = getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        footLoadingLayout = (LinearLayout) footView.findViewById(R.id.loading_layout);
        footLoadingPB = (ProgressBar)footView.findViewById(R.id.loading_bar);
        footLoadingText = (TextView) footView.findViewById(R.id.loading_text);
        listView.addFooterView(footView, null, false);
        footLoadingLayout.setVisibility(View.GONE);
        

        
        //设置item点击事件
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(PublicGroupsActivity.this, GroupSimpleDetailActivity.class).
                        putExtra("groupinfo", adapter.getItem(position)));
            }
        });


        listView.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    if(listView.getCount() != 0){
                        int lasPos = view.getLastVisiblePosition();
                        if(hasMoreData && !isLoading && lasPos == listView.getCount()-1){
                            pageId+=20;
                            String currentUsername = FuLiCenterApplication.getInstance().getUserName();
                            new DownloadPublicGroupListTask(mContext, currentUsername,pageId).execute();
                        }
                    }
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                
            }
        });
        
	}
	
	/**
	 * 搜索
	 * @param view
	 */
	public void search(View view){
	    startActivity(new Intent(this, PublicGroupsSeachActivity.class));
	}
	
	private void loadAndShowData(){
	    new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
//                    final EMCursorResult<EMGroupInfo> result = EMGroupManager.getInstance().getPublicGroupsFromServer(pagesize, cursor);
//                    //获取group list
//                    final List<EMGroupInfo> returnGroups = result.getData();
                    runOnUiThread(new Runnable() {

                        public void run() {
                            searchBtn.setVisibility(View.VISIBLE);
//                            groupsList.addAll(returnGroups);
                            ArrayList<GroupBean> publicList = FuLiCenterApplication.getInstance().getPublicGroupList();
                            groupsList.clear();
                            groupsList.addAll(publicList);
//                            if(returnGroups.size() != 0){
                            if(groupsList.size() != 0){
                                //获取cursor
//                                cursor = result.getCursor();
//                                if(returnGroups.size() == pagesize)
                                if(groupsList.size() == pagesize)
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                            }
                            if(isFirstLoading){
                                pb.setVisibility(View.INVISIBLE);
                                isFirstLoading = false;
                                //设置adapter
                                adapter = new GroupsAdapter(PublicGroupsActivity.this, 1, groupsList);
                                listView.setAdapter(adapter);
                            }else{
//                                if(returnGroups.size() < pagesize){
                                if(groupsList.size() < pagesize+pageId){
                                    hasMoreData = false;
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                    footLoadingPB.setVisibility(View.GONE);
                                    footLoadingText.setText("No more data");
                                }
                                adapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            isLoading = false;
                            pb.setVisibility(View.INVISIBLE);
                            footLoadingLayout.setVisibility(View.GONE);
                            Toast.makeText(PublicGroupsActivity.this, "加载数据失败，请检查网络或稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
	}
	/**
	 * adapter
	 *
	 */
	private class GroupsAdapter extends BaseAdapter {
//        ArrayAdapter<EMGroupInfo>
		private LayoutInflater inflater;
        ArrayList<GroupBean> publicGroup;
        Context mContext;

		public GroupsAdapter(Context context, int res, ArrayList<GroupBean> groups) {
//			super(context, res, groups);
            this.mContext=context;
            this.publicGroup = groups;
            this.inflater = LayoutInflater.from(context);
		}

        @Override
        public int getCount() {
            return publicGroup.size();
        }

        @Override
        public GroupBean getItem(int position) {
            return publicGroup.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_group, null);
			}

			((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).getName());
           NetworkImageView ivAvatar= (NetworkImageView) convertView.findViewById(R.id.avatar);
            UserUtils.setPublicGroupBeanAvatar(getItem(position).getName(),ivAvatar);
            return convertView;
		}
	}

    /*
    * 公开群变化广播接收者
    * */
    class PublicGroupChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取及显示数据
            loadAndShowData();
        }
    }

    private void registerPublicGroupReceiver() {
        mPublicGroupChangeReceiver = new PublicGroupChangeReceiver();
        IntentFilter filter = new IntentFilter("update_PublicGroupList");
        registerReceiver(mPublicGroupChangeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPublicGroupChangeReceiver != null) {
            unregisterReceiver(mPublicGroupChangeReceiver);

        }
    }

    public void back(View view){
		finish();
	}
}
