package document.scanner.pro.pdf.doc.scan.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import document.scanner.pro.pdf.doc.scan.FastScannerApplication;
import document.scanner.pro.pdf.doc.scan.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.pnikosis.materialishprogress.ProgressWheel;

import document.scanner.pro.pdf.doc.scan.models.CroppedModel;
import document.scanner.pro.pdf.doc.scan.models.LocalPdf;
import document.scanner.pro.pdf.doc.scan.models.Preview;
import document.scanner.pro.pdf.doc.scan.persistence.RepoManager;
import document.scanner.pro.pdf.doc.scan.ui.adapters.PhotoPreviewAdapter;
import document.scanner.pro.pdf.doc.scan.ui.base.BaseActivity;

import net.alhazmy13.imagefilter.ImageFilter;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;
import butterknife.BindView;
import butterknife.OnClick;
import document.scanner.pro.pdf.doc.scan.utils.Logs;
import document.scanner.pro.pdf.doc.scan.utils.Utility;


public class PhotoPreviewActivity extends BaseActivity {


    @BindView(R.id.rv_photo_previews)
    RecyclerView mRecyclerView;
    @BindView(R.id.pw_photo_preview)
    ProgressWheel progressWheel;

    private List<CroppedModel> croppedModels = new ArrayList<>();
    private PhotoPreviewAdapter photoPreviewAdapter;
    private List<Preview> previewList = new ArrayList<>();
    private ImageFilter.Filter filter;
    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_preview_photos);



        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Photo Preview");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        filter = ImageFilter.Filter.valueOf(intent.getStringExtra(PhotoFilterActivity.EXTRA_FILTER_TYPE));
        Logs.fine("Filter " + filter.name());
        croppedModels = Parcels.unwrap(intent.getParcelableExtra(CroppedModel.KEY));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoPreviewAdapter = new PhotoPreviewAdapter(this, previewList);
        photoPreviewAdapter.setFilter(filter);
        mRecyclerView.setAdapter(photoPreviewAdapter);

        progressWheel.setVisibility(View.VISIBLE);
        FastScannerApplication.getApplication().getExecutorService()
                .execute(new ScaleAndDisplayImages());


    }

    private class ScaleAndDisplayImages implements Runnable {

        @Override
        public void run() {
            for (CroppedModel croppedModel : croppedModels) {
                Bitmap bitmap = Utility.decodeImageFromFiles(croppedModel.path, 600, 600);
                Preview preview = new Preview();
                preview.bitmap = bitmap;

                previewList.add(preview);
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressWheel.setVisibility(View.GONE);

                    if (photoPreviewAdapter != null)
                        photoPreviewAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @OnClick(R.id.btn_create_pdf_preview_photos)
    public void onCreatePDFClick() {

        FastScannerApplication
                .getApplication()
                .getExecutorService()
                .execute(new PdfCreatorTask(previewList));
    }


    class PdfCreatorTask implements Runnable {

        List<Preview> datas;
        public PdfCreatorTask(List<Preview> bitmaps) {
            datas = bitmaps;
        }

        @Override
        public void run() {

            String thumbPath = croppedModels.get(0).path;
            LocalPdf localPdf = new LocalPdf();
            localPdf.thumbPath = thumbPath;
            try {


                Logs.fine("PDF creation started!");
                File pdf = Utility.pdf();
                OutputStream outputStream = new FileOutputStream(pdf);

                Document document = new Document(PageSize.A4, 20, 20, 20, 20);
                PdfWriter.getInstance(document, outputStream);

                document.open();

                for (int i = 0; i < previewList.size(); i++) {

                    Bitmap next = datas.get(i).bitmap;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    next.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                    FileOutputStream fileOutputStream = new FileOutputStream(new File(localPdf.thumbPath));
                    fileOutputStream.write(byteArrayOutputStream.toByteArray());
                    fileOutputStream.flush();

                    fileOutputStream.close();

                    Image image = Image.getInstance(byteArrayOutputStream.toByteArray());
                    image.scaleToFit(PageSize.A4);
                    image.setAbsolutePosition(
                            (PageSize.A4.getWidth() - image.getScaledWidth()) / 2,
                            (PageSize.A4.getHeight() - image.getScaledHeight()) / 2
                    );

                    document.add(image);
                    document.newPage();

                }

                localPdf.path = pdf.getAbsolutePath();
                localPdf.name = pdf.getName();
                localPdf.timeCreated = String.valueOf(System.currentTimeMillis());

                document.close();
                RepoManager.manager().getDatabaseManager().localPdfDao().newLocalPdf(localPdf);

                Logs.fine("PDF has been created " + pdf.getAbsolutePath());
                new Handler(Looper.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                new OoOAlertDialog.Builder(PhotoPreviewActivity.this)
                                        .setTitle("Congratulations !!!")
                                        .setTitleColor(R.color.colorPrimary)
                                        .setAnimation(Animation.ZOOM)
                                        .setMessage("PDF File Create Successfully.")
                                        .setMessageColor(R.color.colorPrimary)
                                        .setPositiveButtonColor(R.color.colorPrimary)
                                        .setPositiveButtonTextColor(R.color.white)
                                        .setNegativeButtonTextColor(R.color.white)
                                        .setNegativeButtonColor(R.color.colorPrimary)
                                        .setPositiveButton("Finish",  new OnClickListener() {
                                            @Override
                                            public void onClick() {
                                                MainActivity.start(PhotoPreviewActivity.this);
                                               // finish();
                                            }
                                        })
                                        .build();
                               // toast("PDF file created.");
                            }
                        });
            }catch (Exception e) {

                Logs.wtf(e);

            }
        }
    }
}
