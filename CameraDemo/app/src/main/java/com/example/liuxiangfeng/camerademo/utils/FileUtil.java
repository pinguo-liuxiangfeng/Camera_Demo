package com.example.liuxiangfeng.camerademo.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liuxiangfeng on 15-1-7.
 */
public class FileUtil {
    private static final  String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static   String storagePath = "";
    private static final String DST_FOLDER_NAME = "CameraDemo";

    /**初始化保存路径
     * @return
     */
    private static String initPath(){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if(!f.exists()){
                f.mkdir();
            }
        }
        return storagePath;
    }
    /**
     * create bitmap file name
     */
    public static String createJpegName(long dataTake){
        String path = initPath();
        String jpegName = path + "/" + dataTake +".jpg";
        Log.i(TAG, "createJpegName:jpegName = " + jpegName);
        return jpegName;
    }
    /**保存Bitmap到sdcard
     * @param data
     */
    public static void saveBitmap(String jpegName, byte[] data){
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fos = new FileOutputStream(jpegName);
            fos.write(data);
            fos.close();
            Log.i(TAG, "saveBitmap成功");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "saveBitmap:失败");
            e.printStackTrace();
        }

    }
}
