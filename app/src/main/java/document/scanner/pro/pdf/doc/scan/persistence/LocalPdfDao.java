package document.scanner.pro.pdf.doc.scan.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import document.scanner.pro.pdf.doc.scan.models.LocalPdf;

import java.util.List;

@Dao
public interface LocalPdfDao {

    @Insert
    long newLocalPdf(LocalPdf localPdf);

    @Query("SELECT * FROM LocalPdf")
    List<LocalPdf> all();

    @Query("DELETE FROM LocalPdf WHERE path = :path")
    void remove(String path);
}
