package com.xia.adgis.Main.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class HistorySqliteHelpter extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "history.db";

    private final static String CREATE_TABLE = "create table history(name text primary key)";
    public HistorySqliteHelpter(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
