package document.scanner.pro.pdf.doc.scan.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;



@Entity
public class LocalPdf {

    @PrimaryKey(autoGenerate = true)
    public long pdfId = 0;
    public String name = "";
    public String path = "";
    public String timeCreated = "";
    public String thumbPath = "";
}
