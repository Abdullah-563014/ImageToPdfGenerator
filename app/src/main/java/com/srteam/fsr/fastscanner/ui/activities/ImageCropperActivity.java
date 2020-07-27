package com.srteam.fsr.fastscanner.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.srteam.fsr.fastscanner.R;
import com.srteam.fsr.fastscanner.models.CroppedModel;
import com.srteam.fsr.fastscanner.ui.adapters.CroppedModelAdapter;
import com.srteam.fsr.fastscanner.ui.base.BaseActivity;
import com.srteam.fsr.fastscanner.utils.Logs;
import com.srteam.fsr.fastscanner.utils.Utility;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;



public class ImageCropperActivity extends BaseActivity {

    @BindView(R.id.crop_imageview_crop_activity)
    CropImageView mCropImageView;
    @BindView(R.id.rv_photo_list_crop_photo)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_layout_cropping_activity)
    RelativeLayout loadingLayout;
    @BindView(R.id.tv_crop_status_crop_activity)
    TextView cropStatusTextView;

    private List<CroppedModel> croppedModels = new ArrayList<>();
    private CroppedModelAdapter croppedModelAdapter;
    private CroppedModel selectedCroppedModel;
    private volatile int index = 0;
    int angle = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_crop_photo_activity);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        cropStatusTextView.setText("");
        croppedModels = Parcels.unwrap(intent.getParcelableExtra(CroppedModel.KEY));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        croppedModelAdapter = new CroppedModelAdapter(this, croppedModels);
        croppedModelAdapter.setCallback(callback);
        mRecyclerView.setAdapter(croppedModelAdapter);

        if (croppedModels.size() > 0) {
            selectedCroppedModel = croppedModels.get(0);
            load();
        }
    }

    private LoadCallback loadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(Throwable e) {
            Logs.wtf(e);
        }
    };

    private CroppedModelAdapter.Callback callback = new CroppedModelAdapter.Callback() {
        @Override
        public void onSelect(CroppedModel croppedModel) {

            selectedCroppedModel = croppedModel;
            index = croppedModels.indexOf(selectedCroppedModel);
            load();
        }
    };

    private void load() {
        mCropImageView.load(Uri.fromFile(new File(selectedCroppedModel.path)))
                .execute(loadCallback);
    }

    /*
    * Crop the currently selected bitmap and replace it with the original java.io.File object
    * */
    private void crop() {

        final String path = selectedCroppedModel.path;
        Logs.fine("Cropping " + path);
        loadingLayout.setVisibility(View.VISIBLE);
        cropStatusTextView.setText("Cropping photo. Please wait...");
        mCropImageView.crop(Uri.fromFile(new File(path))).execute(new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {

                /*
                * @var index is very important!
                * */
                cropStatusTextView.setText("Wrapping up...");
                final CroppedModel croppedModel = new CroppedModel();
                Utility.bitmapToFile(cropped, new File(selectedCroppedModel.path), new com.srteam.fsr.fastscanner.async.LoadCallback() {
                    @Override
                    public void onLoad(File newFile) {
                        croppedModel.selected = false;
                        croppedModel.path = newFile.getAbsolutePath();
                        croppedModels.remove(index);
                        croppedModels.add(index, croppedModel);

                        loadingLayout.setVisibility(View.GONE);
                        selectedCroppedModel = croppedModel;
                        load();

                        if (croppedModelAdapter != null)
                            croppedModelAdapter.notifyItemChanged(index);
                        Logs.fine("Cropped to " + croppedModel.path + " on Index " + index);
                    }
                });


            }

            @Override
            public void onError(Throwable e) {
                Logs.wtf("Failed to crop photo " + e);
            }
        });

    }

    @OnClick(R.id.layout_btn_crop_current_photo_cropper) public void onCropPhotoClick() {
        crop();
    }

    @OnClick(R.id.layout_done_cropping_photos) public void onDoneClick() {

        Intent intent = new Intent(this, PhotoFilterActivity.class);
        intent.putExtra(CroppedModel.KEY, Parcels.wrap(croppedModels));
        startActivity(intent);
    }

}
