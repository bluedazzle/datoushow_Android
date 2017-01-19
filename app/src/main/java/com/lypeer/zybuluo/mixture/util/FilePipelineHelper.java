package com.lypeer.zybuluo.mixture.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 游小光 on 2017/1/1.
 */

public class FilePipelineHelper {

    public static void writeInt(OutputStream outputStream, int data) throws IOException {
        byte[] bytes = int2bytes(data);
        outputStream.write(bytes, 0, bytes.length);
    }

    public static int readInt(InputStream inputStream) throws Exception {
        if (inputStream.available() < 4) {
            return -1;
        }
        byte[] bytes = new byte[4];
        inputStream.read(bytes, 0, bytes.length);
        return bytes2int(bytes);
    }

    public static void writeLong(OutputStream outputStream, long data) throws IOException {
        byte[] bytes = long2bytes(data);
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();
    }

    public static long readLong(InputStream inputStream) throws Exception {
        if (inputStream.available() < 8) {
            return -1;
        }
        byte[] bytes = new byte[8];
        inputStream.read(bytes, 0, bytes.length);
        return bytes2long(bytes);
    }

    public static void writeBytes(OutputStream outputStream, byte[] bytes, int length) throws IOException {
        outputStream.write(bytes, 0, length);
        outputStream.flush();
    }

    public static boolean readBytes(InputStream inputStream, byte[] bytes, int length) throws Exception {
        if (inputStream.available() < length) {
            return false;
        }
        inputStream.read(bytes, 0, length);
        return true;
    }

    public static int bytes2int(byte[] b) {
        int temp;
        int res = 0;
        for (int i=0;i<4;i++) {
            res <<= 8;
            temp = b[i] & 0xff;
            res |= temp;
        }
        return res;
    }

    public static byte[] int2bytes(int num) {
        byte[] b = new byte[4];
        for (int i=0;i<4;i++) {
            b[i] = (byte)(num>>>(24-(i*8)));
        }
        return b;
    }
    public static byte[] long2bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }
}
