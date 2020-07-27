package com.srteam.fsr.fastscanner.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.srteam.fsr.fastscanner.models.LocalPdf;

/**
 * Created by  ripo on 2/13/2018.
 */

@Database(entities = {LocalPdf.class}, version = 1)
public abstract class DatabaseManager extends RoomDatabase {

    public abstract LocalPdfDao localPdfDao();
}
