package com.example.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ByteUtil {

    // 输入流转字节数组
    public static byte[] inputStreamToByteArray(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024*4];
            int n;
            while ((n = in.read(buffer))>0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
