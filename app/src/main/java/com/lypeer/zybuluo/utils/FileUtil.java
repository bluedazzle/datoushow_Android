package com.lypeer.zybuluo.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.lypeer.zybuluo.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lypeer on 2017/1/13.
 */

public class FileUtil {

    public static String getStorageDir() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = App.getAppContext().getExternalFilesDir(null).getPath();
        } else {
            cachePath = App.getAppContext().getFilesDir().getPath();
        }
        return cachePath;
    }

    public static boolean saveToGallery(String path) {
        if (!checkFile(path)) {
            return false;
        }

        FileInputStream inputStream = null;
        try {
            File originFile = new File(path);
            inputStream = new FileInputStream(originFile);

            byte[] data = new byte[1024];
            //输出流

            File pathDirs = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Camera");
            File resultFile = new File(pathDirs, originFile.getName());
            if (resultFile.exists()) {
                return true;
            } else {
                pathDirs.mkdirs();

                if (resultFile.createNewFile()) {
                    FileOutputStream outputStream = new FileOutputStream(resultFile);
                    //开始处理流
                    while (inputStream.read(data) != -1) {
                        outputStream.write(data);
                    }

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(resultFile));
                    App.getAppContext().sendBroadcast(intent);
                    inputStream.close();
                    outputStream.close();
                } else {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkFile(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }
}
