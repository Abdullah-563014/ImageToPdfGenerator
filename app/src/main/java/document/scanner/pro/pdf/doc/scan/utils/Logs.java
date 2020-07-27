package document.scanner.pro.pdf.doc.scan.utils;

import android.util.Log;

public final class Logs {

    /*
    *
    * Manager Log.xxx
    * */

    private static final String TAG = "LightScanner";

    public static void fine(String m) {
        Log.d(TAG, m + "");
    }
    public static void wtf(String m) {
        fine(m);
    }
    public static void wtf(String message, Throwable throwable) {
        Log.d(TAG, message, throwable);
    }
    public static void wtf(Throwable throwable) {
        wtf("", throwable);
    }
}
