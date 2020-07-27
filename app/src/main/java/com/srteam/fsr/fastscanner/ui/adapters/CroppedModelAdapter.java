package com.srteam.fsr.fastscanner.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.srteam.fsr.fastscanner.R;
import com.srteam.fsr.fastscanner.models.CroppedModel;
import com.srteam.fsr.fastscanner.ui.base.BaseViewHolder;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * Created by  ripo on 1/28/2018.
 */

public class CroppedModelAdapter extends RecyclerView.Adapter<CroppedModelAdapter.CroppedModelViewHolder> {

    private List<CroppedModel> croppedModels;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Callback callback;

    public CroppedModelAdapter(Context context, List<CroppedModel> croppedModels) {
        this.croppedModels = croppedModels;
        mContext = context;
        if (mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public CroppedModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(parent.getContext());

        return new CroppedModelViewHolder(mLayoutInflater.inflate(R.layout.layout_cropped_model, parent, false));
    }

    public interface Callback {

        void onSelect(CroppedModel croppedModel);

    }

    @Override
    public void onBindViewHolder(CroppedModelViewHolder holder, int position) {

        final int idx = position;
        final CroppedModel croppedModel = croppedModels.get(idx);
        Glide.with(mContext)
                .load(new File(croppedModel.path))
                .into(holder.imageView);

        if (croppedModel.selected) {
            holder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_badge));
        }else {
            holder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.color.black));
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectAll();
                croppedModel.selected = true;
                notifyItemChanged(idx);

                if (callback != null)
                    callback.onSelect(croppedModel);
            }
        });
    }

    private void deselectAll() {

        for (CroppedModel model : croppedModels) {
            model.selected = false;
        }
    }

    @Override
    public int getItemCount() {
        return croppedModels.size();
    }

    class CroppedModelViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_cropped_model)
        ImageView imageView;

        public CroppedModelViewHolder(View itemView) {
            super(itemView);
        }
    }
}
