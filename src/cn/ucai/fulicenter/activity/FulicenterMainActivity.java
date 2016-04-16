package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.NewGoodFragment;

/**
 * Created by ucai on 2016/4/16.
 */
public class FulicenterMainActivity extends  BaseActivity {
    TextView mtvCartHint;
    RadioButton mLayoutNewGood;
    RadioButton mLayoutBoutique;
    RadioButton mLayoutCategory;
    RadioButton mLayoutCat;
    RadioButton mLayoutPersonalCenter;
    RadioButton[] mRadio;
    int index;
    int currentIndex = -1;

    NewGoodFragment mNewGoodFragment;
    Fragment[] mFragment = new Fragment[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mNewGoodFragment)
//                .add(R.id.fragment_container, contactListFragment)
//                .hide(contactListFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void initView() {
        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mLayoutNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        mLayoutBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mLayoutCategory = (RadioButton) findViewById(R.id.layout_category);
        mLayoutCat = (RadioButton) findViewById(R.id.layout_cart);
        mLayoutPersonalCenter = (RadioButton) findViewById(R.id.layout_personal_center);
        mRadio=new RadioButton[]{
                mLayoutNewGood,mLayoutBoutique,mLayoutCategory,mLayoutCat,mLayoutPersonalCenter
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRadioDefaultcheked(currentIndex);

    }



    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mFragment[0] = mNewGoodFragment;
    }

    private void setRadioDefaultcheked(int index) {
        if (index == -1) {
            index=0;
        }
        for (int i=0;i<mRadio.length;i++) {
            if (i == index) {
                mRadio[i].setChecked(true);
            } else {
                mRadio[i].setChecked(false);
            }
        }
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                index=0;
                break;
            case R.id.layout_boutique:
                index=1;
                break;
            case R.id.layout_category:
                index=2;
                break;
            case R.id.layout_cart:
                index=3;
                break;
            case R.id.layout_personal_center:
                index=4;
                break;
        }
        currentIndex = index;
        setRadioDefaultcheked(currentIndex);
    }


}
