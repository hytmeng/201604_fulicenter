package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.FragmentCart;
import cn.ucai.fulicenter.fragment.NewGoodFragment;
import cn.ucai.fulicenter.fragment.PersionalCenterFragment;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by clawpo on 16/4/16.
 */
public class FuLiCenterMainActivity extends BaseActivity {

    // 菜单项按钮
    TextView mtvCartHint;
    RadioButton mLayoutCart;
    RadioButton mLayoutNewGood;
    RadioButton mLayoutBoutique;
    RadioButton mLayoutCategory;
    RadioButton mLayoutPersonalCenter;

    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersionalCenterFragment mPersionalCenterFragment;
    FragmentCart mCartFragment;
    Fragment[] mFragments = new Fragment[5];

    int index;
    int currentIndex = 0;
    RadioButton[] mRadios = new RadioButton[5];

    String mCurrentUserName;
    private String action;

    UpdataCartReceiver mUpdataCartReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        updataCartReceiverRegister();
        initFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container, mBoutiqueFragment)
                .add(R.id.fragment_container, mCategoryFragment)
                .add(R.id.fragment_container, mPersionalCenterFragment)
                .add(R.id.fragment_container, mCartFragment)
                .hide(mCartFragment)
                .hide(mPersionalCenterFragment)
                .hide(mBoutiqueFragment)
                .hide(mCategoryFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment=new CategoryFragment();
        mPersionalCenterFragment = new PersionalCenterFragment();
        mCartFragment = new FragmentCart();
        mFragments[0] = mNewGoodFragment;
        mFragments[1] = mBoutiqueFragment;
        mFragments[2] = mCategoryFragment;
        mFragments[4] = mPersionalCenterFragment;
        mFragments[3] = mCartFragment;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setRadioDefaultChecked(currentIndex);
        setFragment();
    }

    private void setFragment() {
        mCurrentUserName = FuLiCenterApplication.getInstance().getUserName();
        action = getIntent().getStringExtra("action");
        if (action != null && mCurrentUserName != null && action.equals("persion")) {
            index=4;
            getIntent().removeExtra("action");
        } else if (action != null && mCurrentUserName != null && action.equals("cart")) {
            index=3;
            getIntent().removeExtra("action");
        } else if (action != null && mCurrentUserName == null && index == 4) {
            index=0;
        }
        if (currentIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
        }
        setRadioDefaultChecked(index);
        currentIndex = index;

    }

    private void setRadioDefaultChecked(int index) {

        for(int i = 0; i< mRadios.length; i++){
            if(i==index){
                mRadios[i].setChecked(true);
            }else{
                mRadios[i].setChecked(false);
            }
        }
    }

    private void initView() {
        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mLayoutNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        mLayoutBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mLayoutCategory = (RadioButton) findViewById(R.id.layout_category);
        mLayoutCart = (RadioButton) findViewById(R.id.layout_cart);
        mLayoutPersonalCenter = (RadioButton) findViewById(R.id.layout_personal_center);
        mRadios[0] = mLayoutNewGood;
        mRadios[1] = mLayoutBoutique;
        mRadios[2] = mLayoutCategory;
        mRadios[3] = mLayoutCart;
        mRadios[4] = mLayoutPersonalCenter;
    }

    public void onCheckedChange(View view){
        switch (view.getId()){
            case R.id.layout_new_good:
                index = 0;
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:
                index = 2;
                break;
            case R.id.layout_cart:
              String userName=  FuLiCenterApplication.getInstance().getUserName();
                if (userName != null) {
                    index = 3;
                } else {
                    gotoLogin("cart");
                }
                break;
            case R.id.layout_personal_center:
                mCurrentUserName = FuLiCenterApplication.getInstance().getUserName();
                if (mCurrentUserName != null) {
                    index = 4;
                } else {
                    gotoLogin("persion");
                }
                break;
        }

        if (currentIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
        }
        setRadioDefaultChecked(index);
        currentIndex = index;
    }



    private void gotoLogin(String action) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("action", action);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        action = getIntent().getStringExtra("action");
    }

    @Override
    protected void onStart() {
        super.onStart();
        action = getIntent().getStringExtra("action");
    }

    private void updataCartReceiverRegister() {
        mUpdataCartReceiver = new UpdataCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        registerReceiver(mUpdataCartReceiver, filter);
    }
    class UpdataCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
            int count = Utils.sumCartCount(cartList);

            if (count > 0) {
                mtvCartHint.setVisibility(View.VISIBLE);
                mtvCartHint.setText(count + "");
            } else {
                mtvCartHint.setVisibility(View.GONE);
            }
        }
    }
}
