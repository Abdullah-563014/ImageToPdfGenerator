package com.srteam.fsr.fastscanner.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.srteam.fsr.fastscanner.models.LocalPdf;

import java.util.List;

/**
 * Created by  ripo on 2/13/2018.
 */

@Dao
public interface LocalPdfDao {

    @Insert
    long newLocalPdf(LocalPdf localPdf);

    @Query("SELECT * FROM LocalPdf")
    List<LocalPdf> all();

    @Query("DELETE FROM LocalPdf WHERE path = :path")
    void remove(String path);
}
