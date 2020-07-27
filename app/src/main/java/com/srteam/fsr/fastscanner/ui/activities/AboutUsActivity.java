package com.srteam.fsr.fastscanner.ui.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.srteam.fsr.fastscanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends AppCompatActivity {


    @BindView(R.id.txtAppVersion)
    TextView txtAppVersion;
    Context mContext;
    public  static final String EMPTY_STRING ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mContext=getApplicationContext();
        ButterKnife.bind(this);

        initFunctionality();
    }



    private void initFunctionality() {
        String appVersionName = getAppVersionName(mContext);
        if (!appVersionName.isEmpty() || appVersionName.equals("")) {
            txtAppVersion.setText(getString(R.string.version) + appVersionName);
        } else {
            txtAppVersion.setVisibility(View.GONE);
        }
    }



    @OnClick(R.id.backButton) public void backButton() {

        AboutUsActivity.this.finish();
    }

    public String getAppVersionName(Context context) {
        String versionName = EMPTY_STRING;
        try {
            versionName= context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return versionName;
    }

}
