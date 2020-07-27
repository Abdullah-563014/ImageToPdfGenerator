package document.scanner.pro.pdf.doc.scan.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    /*
    * ButterKnife enabled viewholder
    * */
    public BaseViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
