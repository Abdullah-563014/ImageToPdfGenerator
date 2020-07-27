package document.scanner.pro.pdf.doc.scan.persistence;

import android.arch.persistence.room.Room;
import android.content.Context;

import document.scanner.pro.pdf.doc.scan.FastScannerApplication;


public class RepoManager {

    private static RepoManager manager;
    private DatabaseManager databaseManager;

    private RepoManager() {

        Context context =
                FastScannerApplication.getApplication().getApplicationContext();
        databaseManager =
                Room.databaseBuilder(context, DatabaseManager.class, "LightScanner.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized RepoManager manager() {
        if (manager == null)
            manager = new RepoManager();

        return manager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
