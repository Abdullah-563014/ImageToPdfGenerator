package com.srteam.fsr.fastscanner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.srteam.fsr.fastscanner.R;
import com.srteam.fsr.fastscanner.models.CroppedModel;
import com.srteam.fsr.fastscanner.ui.base.BaseActivity;
import com.srteam.fsr.fastscanner.utils.Logs;
import com.srteam.fsr.fastscanner.utils.PermissionsDelegate;
import com.srteam.fsr.fastscanner.utils.Utility;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;

import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.result.transformer.ResolutionTransformersKt.scaled;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.FlashSelectorsKt.autoFlash;
import static io.fotoapparat.selector.FlashSelectorsKt.autoRedEye;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.FocusModeSelectorsKt.autoFocus;
import static io.fotoapparat.selector.FocusModeSelectorsKt.continuousFocusPicture;
import static io.fotoapparat.selector.FocusModeSelectorsKt.fixed;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SelectorsKt.firstAvailable;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.highestSensorSensitivity;

/**
 * Created by ripo on 1/28/2018.
 */

public class CameraActivity extends BaseActivity {
    @BindView(R.id.camera_view_main)
    CameraView mCameraView;
    @BindView(R.id.iv_image_camera_activity)
    ImageView imageView;
    @BindView(R.id.loading_layout_camera_activity)
    RelativeLayout loadingLayout;
    @BindView(R.id.tv_photo_taken_count_camera_activity)
    TextView photoTakenCountTextView;

    private Fotoapparat fotoapparat;
    private List<File> photoTakens = new ArrayList<>();

    private CameraConfiguration cameraConfiguration = CameraConfiguration
            .builder()
            .photoResolution(standardRatio(
                    highestResolution()
            ))
            .focusMode(firstAvailable(
                    continuousFocusPicture(),
                    autoFocus(),
                    fixed()
            ))
            .flash(firstAvailable(
                    autoRedEye(),
                    autoFlash(),
                    torch(),
                    off()
            ))
            .previewFpsRange(highestFps())
            .sensorSensitivity(highestSensorSensitivity())
            .build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_camera_activity);
        mCameraView.setVisibility(View.VISIBLE);
        photoTakenCountTextView.setText("0");

        fotoapparat = createFotoapparat();
        fotoapparat.start();
    }

    private Fotoapparat createFotoapparat() {

        return Fotoapparat
                .with(this)
                .into(mCameraView)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(back())
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException e) {

                        Logs.wtf("Error " + e);
                    }
                })
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fotoapparat.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fotoapparat.start();
    }

    @OnClick(R.id.fab_take_photo_main) public void onTakePhotoClick() {

        loadingLayout.setVisibility(View.VISIBLE);
        PhotoResult photoResult = fotoapparat.takePicture();
        Logs.fine("Picture Taken " + photoResult.toString());

        File newPhoto = Utility.randomFile();
        photoResult.saveToFile(newPhoto);
        Logs.fine("Photo Saved " + newPhoto.getAbsolutePath());
        photoTakens.add(newPhoto);

        photoResult
                .toBitmap(scaled(0.25f))
                .whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(BitmapPhoto bitmapPhoto) {
                        if (bitmapPhoto == null) {
                            return;
                        }

                        loadingLayout.setVisibility(View.GONE);

                        if (photoTakens.size() > 0) {
                            photoTakenCountTextView.setVisibility(View.VISIBLE);
                            photoTakenCountTextView.setText(String.valueOf(photoTakens.size()));
                        }
                        imageView.setImageBitmap(bitmapPhoto.bitmap);
                        imageView.setRotation(-bitmapPhoto.rotationDegrees);
                    }
                });
    }

    @OnClick(R.id.btn_continue_camera_activity) public void onContinueClick() {

        if (photoTakens.size() <= 0) {
            toast("No Photo has been taken!");
            return;
        }

        List<CroppedModel> models = new ArrayList<>();

        for (File file : photoTakens) {
            if (file.exists()) {
                CroppedModel model = new CroppedModel();
                model.path = file.getAbsolutePath();
                model.selected = false;
                models.add(model);
            }
        }
        Intent intent = new Intent(this, ImageCropperActivity.class);
        intent.putExtra(CroppedModel.KEY, Parcels.wrap(models));
        startActivity(intent);
    }
}
