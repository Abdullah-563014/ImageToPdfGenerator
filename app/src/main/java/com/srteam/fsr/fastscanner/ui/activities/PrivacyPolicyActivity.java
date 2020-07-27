package com.srteam.fsr.fastscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.srteam.fsr.fastscanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.readPrivacyPolicyReadFromOnlineButtonId)
    Button button;
    private String url="https://bd-super.blogspot.com/p/document-scanner.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        context=getApplicationContext();
        ButterKnife.bind(this);


    }


    @OnClick(R.id.readPrivacyPolicyReadFromOnlineButtonId)
    void openUrl() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(Intent.createChooser(i,"Please select a browser"));
    }


}