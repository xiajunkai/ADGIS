package com.xia.adgis.Login.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSqliteHelper extends SQLiteOpenHelper {
    public UserSqliteHelper(Context context) {
        super(context, "user.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //注册用户
        db.execSQL("create table users(name text primary key,tel text,pwd text)");
        //用户历史
        db.execSQL("create table history(name text primary key)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
