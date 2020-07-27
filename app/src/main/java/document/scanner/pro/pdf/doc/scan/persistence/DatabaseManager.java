package document.scanner.pro.pdf.doc.scan.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import document.scanner.pro.pdf.doc.scan.models.LocalPdf;

@Database(entities = {LocalPdf.class}, version = 1)
public abstract class DatabaseManager extends RoomDatabase {

    public abstract LocalPdfDao localPdfDao();
}
