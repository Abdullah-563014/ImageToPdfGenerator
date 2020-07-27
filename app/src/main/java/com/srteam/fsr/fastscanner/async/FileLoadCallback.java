package com.srteam.fsr.fastscanner.async;

import com.srteam.fsr.fastscanner.models.LocalPdf;

import java.util.List;

/**
 * Created by  ripo on 2/8/2018.
 */

public interface FileLoadCallback {

    void done(List<LocalPdf> list);
    void error(Throwable throwable);
}
