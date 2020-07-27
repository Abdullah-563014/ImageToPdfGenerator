package document.scanner.pro.pdf.doc.scan.async;


import document.scanner.pro.pdf.doc.scan.models.LocalPdf;

import java.util.List;


public interface FileLoadCallback {

    void done(List<LocalPdf> list);
    void error(Throwable throwable);
}
