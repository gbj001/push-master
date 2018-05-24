package com.xinliangjishipin.pushwms.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Zip2String {

    /**
     * @param alipay_url
     * @param down_url
     * @return success or fail
     */
    public static boolean downLoadZip(String alipay_url, String down_url) {
        boolean downSuccess;
        int byteRead;
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFolder = sf.format(date);
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            URL url = new URL(alipay_url);
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();
            String unzipFilePath = down_url;
            File unzipFileDir = new File(unzipFilePath);
            if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
                unzipFileDir.mkdirs();
            }
            File entryFile = new File(down_url);
            if (entryFile.exists()) {
                entryFile.delete();
            }
            fs = new FileOutputStream(down_url);
            byte[] buffer = new byte[4028];
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            downSuccess = true;
            System.out.println(dateFolder + "is success.....");
        } catch (Exception e) {
            System.out.println(dateFolder + "is fail, exception:" + e);
            return false;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                inStream = null;
            }

            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                fs = null;
            }
        }
        return downSuccess;
    }

    public static String readZipFile(String path) {
        String content = "";
        try {
            ZipFile zip = new ZipFile(path, Charset.forName("gbk"));
            Enumeration<? extends ZipEntry> entrys = zip.entries();
            BufferedWriter bw;
            while (entrys.hasMoreElements()) {
                ZipEntry entry = entrys.nextElement();
                String fileName = entry.getName();
                if (fileName != null && fileName.indexOf(".") != -1 && fileName.contains("_账务明细.csv")) {
                    InputStream is = zip.getInputStream(entry);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "gbk"));
                    String line;
                    StringBuilder file_content = new StringBuilder();
                    bw = new BufferedWriter(new FileWriter(path.replace(".zip",".txt")));
                    while ((line = br.readLine()) != null) {
                        file_content.append("|").append(line);
                    }
                    content += file_content.toString() + ";";
                    bw.write(content);
                    bw.close();
                    br.close();
                    is.close();
                }
            }
            zip.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found, " + e);
        } catch (IOException e) {
            System.out.println("IO EXCEPTION , " + e);
        }
        return content;
    }

}
