package com.android_aes.administrator.androidaes;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Objects;

@SuppressLint("NewApi")
public class UriToPathUtil {
  public static String[] getFileFromUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        String[] fileinfo;
        switch (Objects.requireNonNull ( uri.getScheme () )) {
            case "content":
                fileinfo = getFileFromContentUri(uri, context);
                return fileinfo;
            case "file":
                fileinfo = new String[]{uri.getPath(),uri.getQuery ()};
                return  fileinfo;
            default:
                return null;
        }


    }
    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param contentUri The content:// URI to find the file path from
     * @param context    Context
     * @return the file path as a string
     */

    private static String[] getFileFromContentUri(Uri contentUri, Context context) {
        if (contentUri == null) {
            return null;
        }
        String[] filePathColumn = {MediaStore.MediaColumns.DATA,MediaStore.MediaColumns.TITLE};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(contentUri, filePathColumn, null, null, null);
        String[] filePath;
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = new String[]{cursor.getString(cursor.getColumnIndex(filePathColumn[0])), cursor.getString(cursor.getColumnIndex(filePathColumn[1]))};
            System.out.println ("文件名："+cursor.getString(cursor.getColumnIndex ( filePathColumn[1])));
            cursor.close();
            return filePath;
        }
        return null;
    }
}