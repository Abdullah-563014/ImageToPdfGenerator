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
import com.srteam.fsr.fastscanner.models.FilterModel;
import com.srteam.fsr.fastscanner.ui.base.BaseViewHolder;

import net.alhazmy13.imagefilter.ImageFilter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by ripo  on 1/30/2018.
 */

public class PhotoFilterAdapter extends RecyclerView.Adapter<PhotoFilterAdapter.PhotoFilterViewHolder> {

    private List<FilterModel> filterModels;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Callback callback;

    public PhotoFilterAdapter(Context context, List<FilterModel> models) {
        filterModels = models;
        mContext = context;
        if (mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public PhotoFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(parent.getContext());

        return new PhotoFilterViewHolder(mLayoutInflater.inflate(R.layout.layout_cropped_model, parent, false));
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(PhotoFilterViewHolder holder, int position) {

        final FilterModel model = filterModels.get(position);
        Bitmap bitmap = ImageFilter.applyFilter(model.bitmap, model.filter);
        Glide.with(mContext)
                .load(bitmap)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null)
                    callback.onSelect(model);
            }
        });
    }

    public interface Callback {

        void onSelect(FilterModel model);

    }

    @Override
    public int getItemCount() {
        return filterModels.size();
    }

    class PhotoFilterViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_cropped_model)
        ImageView imageView;

        public PhotoFilterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
