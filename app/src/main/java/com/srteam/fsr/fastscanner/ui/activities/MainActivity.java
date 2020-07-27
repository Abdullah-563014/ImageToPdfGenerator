package com.srteam.fsr.fastscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.srteam.fsr.fastscanner.FastScannerApplication;
import com.srteam.fsr.fastscanner.R;
import com.srteam.fsr.fastscanner.async.FileLoadCallback;
import com.srteam.fsr.fastscanner.models.CroppedModel;
import com.srteam.fsr.fastscanner.models.LocalPdf;
import com.srteam.fsr.fastscanner.persistence.RepoManager;
import com.srteam.fsr.fastscanner.ui.adapters.PdfListAdapter;
import com.srteam.fsr.fastscanner.ui.base.BaseActivity;
import com.srteam.fsr.fastscanner.utils.Logs;
import com.srteam.fsr.fastscanner.utils.PermissionsDelegate;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout cameraLayout;
    LinearLayout galleryLayout;
    Context mContext;
    private Toolbar mToolbar;
    private Activity mActivity;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;



    public static final int PHOTO_PICKER = 12;

    @BindView(R.id.pw_main)
    ProgressWheel progressWheel;
    @BindView(R.id.rv_pdfs_main)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_file_layout_main)
    LinearLayout noFileLayout;

    private List<LocalPdf> localPdfs = new ArrayList<>();
    private PdfListAdapter pdfListAdapter;

    private PermissionsDelegate permissionsDelegate;
    private boolean hasCameraPermission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        initToolbar();
        initDrawer();

        permissionsDelegate = new PermissionsDelegate(this);
        hasCameraPermission = permissionsDelegate.hasCameraPermission();

        if (hasCameraPermission) {
            Logs.fine("Permission Granted");
            //  startImagePicker();
        }else {
            Logs.fine("No Permission!");
            permissionsDelegate.requestCameraPermission();
        }

        /*banner ad*/

        AdView mAdView = findViewById(R.id.adView);
        MobileAds.initialize(mActivity, getResources().getString(R.string.banner_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        pdfListAdapter = new PdfListAdapter(this, localPdfs);
        pdfListAdapter.setiInteractionListener(iInteractionListener);
        mRecyclerView.setAdapter(pdfListAdapter);
        loadPdfs();
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }


    public void initDrawer() {

        mDrawerLayout = findViewById(R.id.mDrawer);
        mNavigationView = findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,R.string.open, R.string.close);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void loadPdfs() {

        progressWheel.setVisibility(View.VISIBLE);

        FastScannerApplication
                .getApplication()
                .getExecutorService()
                .execute(new LoadPdfsTask(loadCallback));
    }


    private FileLoadCallback loadCallback = new FileLoadCallback() {
        @Override
        public void done(List<LocalPdf> list) {

            localPdfs.clear();
            localPdfs.addAll(list);
            progressWheel.setVisibility(View.GONE);

            if (pdfListAdapter != null)
                pdfListAdapter.notifyDataSetChanged();

            if (localPdfs.size() <= 0) {
                noFileLayout.setVisibility(View.VISIBLE);
            }else {
                noFileLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void error(Throwable throwable) {

        }
    };

    public static void start(Context photoPreviewActivity) {

        Intent intent = new Intent(photoPreviewActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        photoPreviewActivity.startActivity(intent);
    }

    private class LoadPdfsTask implements Runnable {

        Handler handler = new Handler(Looper.getMainLooper());
        List<LocalPdf> result = new ArrayList<>();
        FileLoadCallback fileLoadCallback;


        public LoadPdfsTask(FileLoadCallback callback) {
            fileLoadCallback = callback;
        }


        @Override
        public void run() {

            try {

                result = RepoManager.manager().getDatabaseManager().localPdfDao().all();
            }catch (Exception e) {
                Logs.wtf(e);
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (fileLoadCallback != null)
                        fileLoadCallback.done(result);
                }
            });
        }

    }


    @OnClick(R.id.fab_create_pdf_main) public void onCreatePDFClick() {

        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_option_dialog, null);

        cameraLayout = view.findViewById(R.id.layout_camera_option_dialog);
        galleryLayout = view.findViewById(R.id.layout_gallery_option_dialog);

        cameraLayout.setOnClickListener(clickListener);
        galleryLayout.setOnClickListener(clickListener);

        dialog.setContentView(view);
        dialog.show();

    }

    private void startImagePicker() {

        ImagePicker.create(this)
                .folderMode(true)
                .toolbarImageTitle("Select Images")
                .toolbarFolderTitle("Select Images")
                .multi()
                .theme(R.style.ImagePickerTheme)
                .start(PHOTO_PICKER);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.layout_camera_option_dialog:
                    startActivity(new Intent(MainActivity.this, CameraActivity.class));
                    break;
                case R.id.layout_gallery_option_dialog:
                    startImagePicker();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            List<CroppedModel> croppedModels = new ArrayList<>();
            switch (requestCode) {

                case PHOTO_PICKER : {
                    List<Image> images = ImagePicker.getImages(data);
                    for (Image image : images) {
                        CroppedModel model = new CroppedModel();
                        model.path = image.getPath();
                        model.selected = false;
                        croppedModels.add(model);
                    }

                    Intent intent = new Intent(this, ImageCropperActivity.class);
                    intent.putExtra(CroppedModel.KEY, Parcels.wrap(croppedModels));
                    startActivity(intent);
                }
            }
        }
    }

    private PdfListAdapter.IInteractionListener iInteractionListener = new PdfListAdapter.IInteractionListener() {

        @Override
        public void onDelete(final LocalPdf toDelete) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete " + toDelete.name)
                    .setMessage("Are you sure you want to delete this PDF file?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            File file = new File(toDelete.path);
                            boolean deleted = file.delete();
                            if (!deleted) {
                                toast("Failed to delete file. Please retry");
                                return;
                            }

                            int idx = localPdfs.indexOf(toDelete);
                            localPdfs.remove(idx);
                            if (pdfListAdapter != null)
                                pdfListAdapter.notifyDataSetChanged();

                            RepoManager
                                    .manager()
                                    .getDatabaseManager()
                                    .localPdfDao()
                                    .remove(toDelete.path);

                            toast("File Deleted!");
                        }
                    })
                    .setNegativeButton("NO", null)
                    .create()
                    .show();
        }

        @Override
        public void onShare(LocalPdf toShare) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            if (mActivity == null)
                return;
            File pdf = new File(toShare.path);
            if (pdf.exists()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdf));
                intent.setType("application/pdf");
                openIntent(Intent.createChooser(intent, "Share file"));
            } else {
                Log.i("DEBUG", "File doesn't exist");
            }

        }

        @Override
        public void onView(LocalPdf onView) {
            viewPdf(onView);
        }
    };

    private void viewPdf(LocalPdf localPdf){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File(localPdf.path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            if(mActivity ==null){
                Log.d("fileViewError","mActivity: is null");
            }

            Uri uri = Uri.fromFile(file);
            target.setDataAndType(uri, mActivity.getString(R.string.pdf_type));
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openIntent(Intent.createChooser(target, "Open file"));

        } catch (Exception e) {
            Log.d("fileViewError",e.getMessage()+", "+file);
            Toast.makeText(mActivity, "error occurred", Toast.LENGTH_SHORT).show();
        }

    }


    private void openIntent(Intent intent) {
        try {
            mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mActivity, "No PDf App", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        new OoOAlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setTitleColor(R.color.colorPrimary)
                .setAnimation(Animation.ZOOM)
                .setMessage("Are You Sure,want exit From App?")
                .setMessageColor(R.color.colorPrimary)
                .setPositiveButtonColor(R.color.colorPrimary)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonTextColor(R.color.white)
                .setNegativeButtonColor(R.color.colorPrimary)
                .setPositiveButton("Yes",  new OnClickListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                })
                .setNegativeButton("No",  null)
                .build();

    }

    public static void rateThisApp(Context activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void shareApp(Activity activity) {
        try {
            final String appPackageName = activity.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.share_text) + " https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            activity.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoPrivacyPolicyActivity() {
        startActivity(new Intent(MainActivity.this,PrivacyPolicyActivity.class));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            hasCameraPermission = true;
        }
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            // Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            isStoragePermissionGranted();
        }

    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //  Log.v(TAG,"Permission is granted");
                return true;
            } else {

                // Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            // Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_camera_list) {
            startActivity(new Intent(mActivity, CameraActivity.class));
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_gallary) {
            startImagePicker();
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_privacy_policy) {
            gotoPrivacyPolicyActivity();
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_share) {
            rateThisApp(mActivity);
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_rate) {
            shareApp(mActivity);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(mActivity,AboutUsActivity.class));
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

}
