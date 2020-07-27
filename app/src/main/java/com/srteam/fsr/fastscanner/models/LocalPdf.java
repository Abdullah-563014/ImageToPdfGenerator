package com.srteam.fsr.fastscanner.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by  ripo on 2/8/2018.
 */

@Entity
public class LocalPdf {

    @PrimaryKey(autoGenerate = true)
    public long pdfId = 0;
    public String name = "";
    public String path = "";
    public String timeCreated = "";
    public String thumbPath = "";
}
