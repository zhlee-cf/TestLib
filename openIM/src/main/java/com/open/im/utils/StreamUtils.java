package com.open.im.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
    /**
     * 将输入流中的内容，转换为字符串
     *
     * @param input
     * @return 如果出错，返回 null
     */
    public static String convertStream2Str(InputStream input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 自带缓存的输出流
        int len = -1;
        byte[] buffer = new byte[2048];
        try {
            while ((len = input.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
