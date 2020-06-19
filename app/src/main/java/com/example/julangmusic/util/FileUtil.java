package com.example.julangmusic.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * created by 马宏彪
 * 创建日期：2020-5-22
 * 功能：文件操作的工具类
 */
public class FileUtil {

    //将一个输入流转换为一个字符串
    public static String formatStreamToString(InputStream stream){
        if(stream!=null){
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            byte[] bytes=new byte[1024];
            int len=0;
            try {
                while((len=stream.read(bytes))!=-1){
                    out.write(bytes,0,len);
                }
                String str=out.toString();
                out.flush();
                out.close();
                stream.close();
                return str;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean existsFile(String path){
        if(path!=null&&path.length()>0) {
            File file = new File(path);
            if(file.exists())
                return true;
        }
        return false;
    }
}
