package cn.ucai.fulicenter.view;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by ucai on 2016/4/18.
 */
public class DisplayUtils {
    public static void initBack(final Activity activity) {
        LinearLayout clickArea = (LinearLayout) activity.findViewById(R.id.ll_backCheck);
        clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
    public static void initBackwithTitle(final Activity activity,String title) {
        TextView tvTitle = (TextView) activity.findViewById(R.id.tv_title);

        tvTitle.setText(title);
        initBack(activity);
    }
}
