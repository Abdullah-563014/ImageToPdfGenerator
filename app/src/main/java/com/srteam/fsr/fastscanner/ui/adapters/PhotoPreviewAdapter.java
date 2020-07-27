package com.srteam.fsr.fastscanner.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.srteam.fsr.fastscanner.R;
import com.srteam.fsr.fastscanner.models.Preview;
import com.srteam.fsr.fastscanner.ui.base.BaseViewHolder;

import net.alhazmy13.imagefilter.ImageFilter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by  ripo on 1/30/2018.
 */

public class PhotoPreviewAdapter extends RecyclerView.Adapter<PhotoPreviewAdapter.PhotoPreviewViewHolder> {

    private List<Preview> previewList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ImageFilter.Filter filter;

    public PhotoPreviewAdapter(Context context, List<Preview> models) {
        previewList = models;
        mContext = context;
        if (mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setFilter(ImageFilter.Filter filter) {
        this.filter = filter;
    }

    public ImageFilter.Filter getFilter() {
        return filter;
    }

    @Override
    public PhotoPreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(parent.getContext());

        return new PhotoPreviewViewHolder(mLayoutInflater.inflate(R.layout.layout_preview_model, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotoPreviewViewHolder holder, int position) {

        Preview preview = previewList.get(position);
        Bitmap bitmap = ImageFilter.applyFilter(preview.bitmap, getFilter());

        Glide.with(mContext)
                .load(bitmap)
                .into(holder.view);
    }

    @Override
    public int getItemCount() {
        return previewList.size();
    }

    class PhotoPreviewViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_image_preview_model)
        ImageView view;

        public PhotoPreviewViewHolder(View itemView) {
            super(itemView);
        }
    }
}
