package org.tmcrafz.cloudgallery.datahandling;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataHandlerSql extends SQLiteOpenHelper {
    private static final String TAG = DataHandlerSql.class.getSimpleName();

    private final static String DATABASE_NAME = "cloudgallery.db";
    private final static int DATABASE_VERSION = 1;

    private final static String MEDIA_PATHS_TABLE = "media_paths";

    private final static String MEDIA_PATHS_COL_ID = "id";
    private final static String MEDIA_PATHS_COL_REMOTE_PATH = "path";

    private final static String SQL_CREATE_MEDIA_PATH_TABLE = "CREATE TABLE " + MEDIA_PATHS_TABLE
            + " (" + MEDIA_PATHS_COL_ID + " INTEGER PRIMARY KEY, " + MEDIA_PATHS_COL_REMOTE_PATH + " TEXT)";

    private final static String SQL_DELETE_MEDIA_PATH_TABLE =
            "DROP TABLE IF EXISTS " + MEDIA_PATHS_TABLE;

    private Context mContext;

    public DataHandlerSql(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MEDIA_PATH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_MEDIA_PATH_TABLE);
        onCreate(db);

    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    public boolean isMediaPathExisting(String path) {
        SQLiteDatabase db = getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(db, MEDIA_PATHS_TABLE,
                MEDIA_PATHS_COL_REMOTE_PATH + " = ?"  , new String[] { path });
        if (cnt > 0) {
            return true;
        }
        return false;
    }

//    public int getMediaPathCount(String path) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor=db.query(MEDIA_PATHS_TABLE, null, null, null, null, null, null);
//        db.close();
//        return cursor.getCount();
//    }

    public long insertMediaPath(String path) {
        if (isMediaPathExisting(path)) {
            return -1;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MEDIA_PATHS_COL_REMOTE_PATH, path);
        long rowID = db.insert(MEDIA_PATHS_TABLE, null, values);
        db.close();
        return rowID;
    }

    public ArrayList<String> getAllPaths() {
        ArrayList<String> pathList = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + MEDIA_PATHS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(1);
                pathList.add(path);
            } while (cursor.moveToNext());
        }
        db.close();
        return pathList;
    }

    public void deletePath(String path) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MEDIA_PATHS_TABLE, MEDIA_PATHS_COL_REMOTE_PATH + " = ?", new String[] { path });
        db.close();
    }

    public void clearMediaPaths(String path) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(MEDIA_PATHS_TABLE, "1", null);
        db.close();
    }

}
