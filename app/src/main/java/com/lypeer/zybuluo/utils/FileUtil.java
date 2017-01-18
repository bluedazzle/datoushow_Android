package com.lypeer.zybuluo.utils;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;

import com.lypeer.zybuluo.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lypeer on 2017/1/13.
 */

public class FileUtil {
    private static final String ASSET_LIST_FILENAME = "assets.lst";

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

    public static boolean copy() throws IOException {
        AssetManager manager = App.getAppContext().getAssets();

        List<String> srcFiles = new ArrayList<String>();

        //读取assets/$(subDirectory)目录下的assets.lst文件，得到需要copy的文件列表
        List<String> assets = getAssetsList(manager);
        for (String asset : assets) {
            //如果不存在，则添加到copy列表
            if (!new File(getStorageDir(), asset).exists()) {
                srcFiles.add(asset);
            }
        }

        //依次拷贝到App的安装目录下
        for (String file : srcFiles) {
            copy(file, manager);
        }

        return true;
    }

    private static List<String> getAssetsList(AssetManager manager) throws IOException {
        List<String> files = new ArrayList<>();

        InputStream listFile = manager.open(new File(ASSET_LIST_FILENAME).getPath());
        BufferedReader br = new BufferedReader(new InputStreamReader(listFile));
        String path;
        while (null != (path = br.readLine())) {
            files.add(path);
        }

        return files;
    }

    private static File copy(String asset, AssetManager manager) throws IOException {

        InputStream source = manager.open(new File(asset).getPath());
        File destinationFile = new File(getStorageDir(), asset);
        destinationFile.getParentFile().mkdirs();
        OutputStream destination = new FileOutputStream(destinationFile);
        byte[] buffer = new byte[1024];
        int nread;

        while ((nread = source.read(buffer)) != -1) {
            if (nread == 0) {
                nread = source.read();
                if (nread < 0)
                    break;
                destination.write(nread);
                continue;
            }
            destination.write(buffer, 0, nread);
        }
        destination.close();

        return destinationFile;
    }
}
