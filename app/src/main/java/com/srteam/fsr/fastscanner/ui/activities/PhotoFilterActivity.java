package com.srteam.fsr.fastscanner.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.srteam.fsr.fastscanner.R;
import com.srteam.fsr.fastscanner.models.CroppedModel;
import com.srteam.fsr.fastscanner.models.FilterModel;
import com.srteam.fsr.fastscanner.ui.adapters.PhotoFilterAdapter;
import com.srteam.fsr.fastscanner.ui.base.BaseActivity;

import net.alhazmy13.imagefilter.ImageFilter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by  ripo on 1/30/2018.
 */

public class PhotoFilterActivity extends BaseActivity {

    @BindView(R.id.iv_image_filter_photo)
    ImageView imageView;

    private List<FilterModel> modelList = new ArrayList<>();
    private List<CroppedModel> croppedModels = new ArrayList<>();
    private PhotoFilterAdapter photoFilterAdapter;
    private FilterModel selectedFilterModel;

    public static final String EXTRA_FILTER_TYPE = "filter_type";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_filter_photo);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        croppedModels = Parcels.unwrap(intent.getParcelableExtra(CroppedModel.KEY));
        if (croppedModels.size() > 0) {
            modelList = FilterModel.create(croppedModels.get(0).path);
            selectedFilterModel = modelList.get(8);
            Bitmap bitmap = ImageFilter.applyFilter(selectedFilterModel.bitmap, selectedFilterModel.filter);

            Glide.with(PhotoFilterActivity.this)
                    .load(bitmap).into(imageView);
        }

    }

    private PhotoFilterAdapter.Callback callback = new PhotoFilterAdapter.Callback() {
        @Override
        public void onSelect(FilterModel model) {
            selectedFilterModel = model;

            Bitmap bitmap = ImageFilter.applyFilter(selectedFilterModel.bitmap, selectedFilterModel.filter);
            Glide.with(PhotoFilterActivity.this)
                    .load(bitmap).into(imageView);
        }
    };

    @OnClick(R.id.btn_proceed_filter_photos) public void onProceedClick() {

        if (selectedFilterModel == null)
            return;

        Intent intent = new Intent(this, PhotoPreviewActivity.class);
        intent.putExtra(CroppedModel.KEY, Parcels.wrap(croppedModels));
        intent.putExtra(EXTRA_FILTER_TYPE, selectedFilterModel.filter.name());
        startActivity(intent);
    }
}
