package document.scanner.pro.pdf.doc.scan.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bumptech.glide.request.RequestOptions;
import document.scanner.pro.pdf.doc.scan.FastScannerApplication;
import document.scanner.pro.pdf.doc.scan.async.LoadCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;


public final class Utility {

    /*
    * Handy utils
    * */

    public static final SimpleDateFormat SDF = new SimpleDateFormat("dd:mm:yy", Locale.getDefault());

    public static String textOf(EditText editText) {

        if(editText == null) return "";

        return editText.getText().toString().trim();
    }

    public static String randString() {
        return String.valueOf("a" + new Random().nextInt(99999999));
    }


    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e) {
            //if there is NPE
        }
    }

    public static boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                FastScannerApplication.getApplication().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static File randomFile() {

        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "LightScanner";

        File folder = new File(fileName);
        if (!folder.exists()) {
            boolean result = folder.mkdirs();
            Logs.fine("Folder created " + result);
        }
        return new File(folder, String.valueOf(System.currentTimeMillis()) + randString() + ".png");
    }

    public static File pdf() {

        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "LightScanner" + File.separator + "PDFs";

        File folder = new File(fileName);
        if (!folder.exists()) {
            boolean result = folder.mkdirs();
            Logs.fine("Folder created " + result);
        }

        return new File(folder, randString() + System.currentTimeMillis() + ".pdf");
    }

    public static void bitmapToFile(final Bitmap bitmap, final File file, final LoadCallback loadCallback) {

        final Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final File copied = file;
                long startTime = System.currentTimeMillis();
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                    FileOutputStream fileOutputStream = new FileOutputStream(copied);
                    fileOutputStream.write(outputStream.toByteArray());
                    outputStream.flush();

                }catch (Exception e) {
                    Logs.wtf("Error while converting bitmap to file " + e);
                }

                long endTime = System.currentTimeMillis();

                Logs.fine("Time taken " + ( (startTime - endTime) / 1000) + " Secs");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadCallback != null)
                            loadCallback.onLoad(copied);
                    }
                });
            }
        };

        FastScannerApplication.getApplication().getExecutorService()
                .execute(runnable);
    }

    public static Bitmap decodeImageFromFiles(String path, int width, int height) {
        BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
        scaleOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, scaleOptions);
        int scale = 1;
        while (scaleOptions.outWidth / scale / 2 >= width
                && scaleOptions.outHeight / scale / 2 >= height) {
            scale *= 2;
        }
        // decode with the sample size
        BitmapFactory.Options outOptions = new BitmapFactory.Options();
        outOptions.inSampleSize = scale;
        return BitmapFactory.decodeFile(path, outOptions);
    }

    public static File root() {

        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "FastScanner" + File.separator + "PDFs";

        return new File(fileName);
    }

    public static RequestOptions options() {

        return new RequestOptions().centerCrop()
                .dontAnimate();
    }
}
