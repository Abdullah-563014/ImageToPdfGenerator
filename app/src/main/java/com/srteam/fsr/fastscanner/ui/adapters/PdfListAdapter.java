package com.srteam.fsr.fastscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.srteam.fsr.fastscanner.R;
import com.srteam.fsr.fastscanner.models.LocalPdf;
import com.srteam.fsr.fastscanner.ui.base.BaseViewHolder;
import com.srteam.fsr.fastscanner.utils.Utility;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ripo  on 2/8/2018.
 */

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.PdfListViewHolder> {

    private List<LocalPdf> localPdfs = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private IInteractionListener iInteractionListener;

    public PdfListAdapter(Context context, List<LocalPdf> list) {
        localPdfs = list;
        mContext = context;

        if (mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setiInteractionListener(IInteractionListener iInteractionListener) {
        this.iInteractionListener = iInteractionListener;
    }

    @Override
    public PdfListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(parent.getContext());

        return new PdfListViewHolder(mLayoutInflater.inflate(R.layout.layout_pdf, parent, false));
    }

    @Override
    public void onBindViewHolder(final PdfListViewHolder holder, int position) {

        final int idx = position;
        LocalPdf localPdf = localPdfs.get(idx);
        holder.fileNameTextView.setText(localPdf.name);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        holder.timeCreatedTextView.setText(currentDateTimeString);

        Glide.with(mContext)
                .load(new File(localPdf.thumbPath))
                .apply(Utility.options())
                .into(holder.imageView);
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(idx, holder.more);
            }
        });

    }

    private void showMenu(int idx, View view) {

        final LocalPdf selected = localPdfs.get(idx);
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.pdf_option_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_option_delete:
                        if (iInteractionListener != null)
                            iInteractionListener.onDelete(selected);
                        return true;
                    case R.id.menu_option_view:
                        if (iInteractionListener != null)
                            iInteractionListener.onView(selected);
                        return true;
                    case R.id.menu_option_share:
                        if (iInteractionListener != null)
                            iInteractionListener.onShare(selected);
                        return true;
                }
                return true;
            }
        });

        popupMenu.show();
    }

    public interface IInteractionListener {

        void onDelete(LocalPdf toDelete);
        void onShare(LocalPdf toShare);
        void onView(LocalPdf onView);
    }

    @Override
    public int getItemCount() {
        return localPdfs.size();
    }

    class PdfListViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_pdf)
        ImageView imageView;
        @BindView(R.id.tv_file_name_pdf)
        TextView fileNameTextView;
        @BindView(R.id.tv_time_pdf)
        TextView timeCreatedTextView;
        @BindView(R.id.iv_more)
        ImageView more;

        public PdfListViewHolder(View itemView) {
            super(itemView);
        }
    }
}
