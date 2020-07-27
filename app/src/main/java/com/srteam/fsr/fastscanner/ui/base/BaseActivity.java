package com.srteam.fsr.fastscanner.ui.base;

import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.srteam.fsr.fastscanner.R;

import butterknife.ButterKnife;

/**
 * Created by  ripo on 12/21/2017.
 */

public class BaseActivity extends AppCompatActivity {

    /*
    * State variable, to determine if activity is visible
    * */
    private volatile boolean isOn = false;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        ButterKnife.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOn = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOn = true;
    }

    public void showDialog(String title, String message) {

        /*
        *
        * Do not attempt to show dialog if activity is not visible
        * */
        if(!isOn) return;

        new AlertDialog.Builder(this, R.style.AppTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    public void toast(String message) {

        if(!isOn) return;

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    public void snack(String message) {

        if(!isOn) return;

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void show(View... views) {

        try {

            if(views == null) return;

            if (!isOn) return;

            for (View view : views)
                view.setVisibility(View.VISIBLE);

        }catch (Exception e) {}
    }

    public void hide(View... views) {

        try {

            if(views == null) return;

            if (!isOn) return;

            for (View view : views)
                view.setVisibility(View.GONE);

        }catch (Exception e) {}

    }
}
