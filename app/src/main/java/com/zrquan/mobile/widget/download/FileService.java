package com.zrquan.mobile.widget.download;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务bean
 */
public class FileService {
    private SQLiteOpenHelper openHelper;

    public FileService(SQLiteOpenHelper sqLiteOpenHelper) {
        openHelper = sqLiteOpenHelper;
    }

    /**
     * 获取每条线程已经下载的文件长度
     *
     * @param path
     * @return
     */
    public Map<Integer, Integer> getData(String path) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db
                .rawQuery(
                        "select thread_id, download_length from file_download_logs where download_path=?",
                        new String[]{path});
        Map<Integer, Integer> data = new HashMap<>();
        while (cursor.moveToNext()) {
            data.put(cursor.getInt(0), cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * 保存每条线程已经下载的文件长度
     *
     * @param path
     * @param map
     */
    public void save(String path, Map<Integer, Integer> map) {

        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                db.execSQL(
                        "insert into file_download_logs(download_path, thread_id, download_length) values(?,?,?)",
                        new Object[]{path, entry.getKey(), entry.getValue()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * 实时更新每条线程已经下载的文件长度
     *
     * @param path
     * @param threadId
     * @param pos
     */
    public void update(String path, int threadId, int pos) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL(
                "update file_download_logs set download_length=? where download_path=? and thread_id=?",
                new Object[]{pos, path, threadId});
        db.close();
    }

    /**
     * 当文件下载完成后，删除对应的下载记录
     *
     * @param path
     */
    public void delete(String path) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from file_download_logs where download_path=?",
                new Object[]{path});
        db.close();
    }

}
