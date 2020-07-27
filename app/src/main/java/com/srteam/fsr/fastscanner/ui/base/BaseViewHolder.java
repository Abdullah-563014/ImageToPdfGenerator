package com.srteam.fsr.fastscanner.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by  ripo on 12/31/2017.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    /*
    * ButterKnife enabled viewholder
    * */
    public BaseViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
