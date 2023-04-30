package com.jarvis.instarepost.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Iterator;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "InstaRepost.sqlite";
    private static int DB_VERSION = 1;
    private static final ArrayList<String> TABLE_SCHEMA = new ArrayList<>();

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_SCHEMA.add("CREATE TABLE IF NOT EXISTS \"posts\" (\"id\" TEXT PRIMARY KEY, \"profile_pic_url\" TEXT, \"username\" TEXT, \"caption\" TEXT, \"media\" TEXT, \"time\" INTEGER)");
    }

    private void create(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.beginTransaction();
        Iterator it = TABLE_SCHEMA.iterator();
        while (it.hasNext()) {
            sQLiteDatabase.execSQL((String) it.next());
        }
        sQLiteDatabase.setTransactionSuccessful();
        sQLiteDatabase.endTransaction();
    }

    private void drop(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS \"posts\"");
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        create(sQLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        drop(sQLiteDatabase);
        create(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        drop(sQLiteDatabase);
        create(sQLiteDatabase);
    }
}
