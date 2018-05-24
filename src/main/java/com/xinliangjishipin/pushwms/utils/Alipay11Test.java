package com.xinliangjishipin.pushwms.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import org.dom4j.Element;

import java.io.File;
import java.net.URLDecoder;
import java.time.LocalDate;

public class Alipay11Test {

    public static void testAlipay(String downloadDate) {
        String appId = "2018031502382723";
        //String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCUexAA9XtqfU9s24YXCZrrbY3RVZwUT4GEYJK6ETcbZp2Dz1R3TzzWJjYXAQMY0rN+ZDFvXKMD3nzltY5tIXFKhUn6ykCU98ilRvBtxgWASeUpOL3JC4Rs0wSS9eV/ti89UnkJ0ZGpAPGjKk6Zup7VK5PbCqPeQfwRVpS9VxfdV2deQFOSF053EwSXLHgrNXIP+3d0Zy5DynUAgSch6VDuvYsdfQTUjThgW2/rejPHDm1EFfN2ybGBy15hI3TLA3Rh8KR7mijTMnTZsk8jblI+jght5CVUBjHFGrk7a5nOj9HCloSAsLIJq7gAUPNXKlKpkY/yLOzHGj3PrjRxOwOLAgMBAAECggEAY/Xpk0uw98M/KoEr5+yOgHrz4/9noYDZKB7ACUY3vFm8J5X4Po543Y9CEQCcbiTDtI6NfXR3Rs6NdTRim3PNSqcrZAyvp9qdGTAGA1EyOOkGv1a05lm7oGv8A1hKVk13xif01rhhAM9i3j9IRVSPQ+Ifm3KxWZtAsQeCAWpV5au7gWHDKz8X7C6AiBUID1Dm09Z0aj5F51+uC/PgSF/QDAmqHs9jtHIeYFrzpim2QSF4CZkK7eFeFuw7hi9Joj/qboQgUe46UMiPa21raev9wAk39/BN4TXjIZI4tA+0a2eBUQueYqRbood+E6h/PIFL7HoayNOgEKNSBMhksv/d8QKBgQDtlmuPOlB5aL90NFllVp6UGQlSWWIwUW/QsMzuDoodh2iRKomRtkWOCPR+81ozKx57WW4KlyFi2Lc4t/z/v3TOotRUgJ8719yxujhk2q63FU96MtQV0ODJ48jvJg8s3riscb5U4rX6Y19rQTJNldZP3LkJOraIkj+T3sT/zeok4wKBgQCf/NJAXA6lOgPEhX1SVu5FXJ9YgN+i6umH42xd6CCZH5JZ9KjgYYXgvYZyhUqDSyeNc2ELRQs3zcah8SawusIOmJYdGyxtyocEIBeaaiBWP3eRd6pXw7EBXpxXMSJI0UBjXg54GNTVQk67s59HEw+fmFRm+RtBqg6PmS3NcuqPOQKBgQCTIWjAjONTHEwb4WNO/2I64npEVfgZZgUxnpt5/OUJPlbCNy50XwUZ3W/Twk4ki9pXlt0vj8HsHbrxU/dIRb9HS8zj+7cgbyBLq7/KrSYvWPIcAagXToA2ZmqDtvUE23RPziyJEtCRG8L2f6xwIY9Ta6PnFEX/s1nN79HdfB448wKBgCEVNqT4ZE7mYEETGYcdUsglDw5OF/CogwIGlTIV/ierz6eqYAGGKRkAF/02cuITeGpXoYmjDV7MvnZeV5HUDKzYALKkG9vYNXM076yOpYEwPplmFWNwo/mUht/A2UYVfysNGBDdkaVHwOAvlJAt8N0fstRYTrqVX81x73a8fKSBAoGBAMJw/Lakdmbz5hZK0BW867vWHsEPtZflkoRSjzfZ5b7iNn19Jz+zFELaniyE3ktwgWYOA6pJL59JVEJBNLBJ+QBF9aTSTFDtx8MlGHA36ZjblCXlcgONzvOjxfGFABdMFZBHPeg5tnhrADBqCcUob2/95uV7iZYZb+czXsNcInCF";
        String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDRCEzs8I1y6xML+VcnP4Sxqj/+Fi98y1knuLsNwKTXahMEBLVH4ZHrxypIdYU9BMQYEg20jeq7eMDN27udPp6yy1f+FwJyqHbaSrgp1tOtdLxEptsBYOrmIm48Vb3dND8GnBmVtFbSSRs9kjXvrlLo70EXM2ECh7B8dTZkdKlrtuyixHCV4r7LvcvzQUwdFDAfrk7SXKTLiBKXNlNk5hPfY4QSI4BKx5CUMBnJV7yT8eDOHQ/42XOK+mA829JyfEbZDNWVE+7LJvuGSvKWiJfH38LyDVUs7XWzEurbWeV57S0B+TBHbhYs8n5lC3zvcjotwi6kPWHL/GGNc1ZE4VCjAgMBAAECgf8E5iznYLvv9ZJ8tVtVkCeMAnF9gweZ5SoxxCKeL+WtJs9LSND23lKNNB3JlPeKdpIh/kpT8P+PZUUfjgbXqKAPkLR/QArOpSQj+ZxXXrGXXyUKdt6rKZpXynbK8zd/97mCbiYrXA6s3wnEZ6jb9Tn8O59K6+laJdP9FsIvmIxf+d62xDQXSXwNKX2Zn5FERowTB+/kL70cIbzJy2y+j3nkpKJO4wm2kqBc2d+z+QcrC+88opwR4GC3L+cLJBM2G8abSJVb9knnU+zK+UIvKE1GgTS8BiUMmm7ZavsJWt+ruGv9JIyQo3zQya36dYEybVqxGypCIyVtdgPyO2N/k7ECgYEA8sw0qhxqpMD7EVFcAsmsN+qu5BIFBQb/SAQfUFOKv9tYl+B1G+TC7/ObBCaNXM8jgZEopckkJ0TnPv1rPliejrxt4RC9XvjBDuSD1mCGl1IN/gz2xecjJYK3HNr+l9Di6gptzueOSophRte5jRGWEwq7vHN1jGwWiGeQXwVKQP8CgYEA3GYTTI804O8rmh0IzgTFPpxUQ2W8vcoPTfxjQWdR61RJw5t09Xagzgw1qKlV2cGfUySloXBM3a34whLrhRII7u9sLm0QQDgX53tvlXmX7f4SkAXxDCXgZ4wNupy0dzpbpLTJwy0y4uwmwYdn/otvTLoMjU6L5/CoR8X/WBZkTF0CgYEA5w6uj7S7SE7en3xts7l4yz3rceA0JrSh/EbwBeF7CVoU1XQl/oWVfgS2YRzPKqN1hrg9lof0f+pypt9ET/DF89AeQTFNBjR0hVeynzD85MSRzFNKBMwSBoB/AlbmMlP3ngKOvXhfUVCfgmuJiVZTG4v7XTwDgb7bMrRcEBFYuMECgYAIRrkAzfvhWnnbrq1u9xTlFf/ADCj7Q4QxnIGpqhSUbrTaaJMKyKqfMiwtqjY0GCZAfAbQOiRYyrilqQo5FAVL26fHUocisjiLk8IL7eX7pOfTirWTjDSSDttRH6NTOtjIJZXSA3d+t/l4mPoZv+itC+t+vu1YB1x6XYAKMcUQbQKBgQCPJMNyTnEiVnamFkDCMXp8FVDCOncIb5B06oHkmcZwVktF1MZOxQ3Tahr6cUeiioAK+vt3ZcYbqCV/RlIAmAogj2hIKy18LhlP/sKAlXQj9E3V09Fd0cVNwAHTKtTbYQ3ygSlWjnFQhdFUwLCyel6nwsOLklO/nKXnxBumXQBcag==";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgFcJT51rfdU9re90MF4+8d/AGNzwu5/DXIrUnviUuEyENMRpYYoMFVc1HsjiHW9/72gkoq1X19KnE6nqqR4ESEr7rpO78OUSADkhOZ+P2If2dK7/ar1rN73CL6pRnlc4ShqCoq01RD8Bya6ncjjs2Q5DSNbostW6tXnAqoaCEO14GIVrbQXUhIdSQA/Hk9G0RNu0uVlbXQC8kQAUf34z4GCXCd8tdx2z29vv5lxuTqM7hbZHDGNOBqAzrMegQMWasRP7T9JXTVr5yMQbW4Yc8xyrj/VTkRZ5sASJbGnbshuL/V48E1+0xCeVLlD0HuvflnRKiaI7U8Cm4Yehb0Bt6QIDAQAB";


        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "gbk", publicKey, "RSA2");
        //AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        request.setBizContent("{" +
                "\"bill_type\":\"signcustomer\"," +
                "\"bill_date\":\""+downloadDate+"\"" +
                "  }");
        AlipayDataDataserviceBillDownloadurlQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }


        if(response.isSuccess()){

            //将接口返回的对账单下载地址传入urlStr
            String urlStr=response.getBillDownloadUrl();
            ////指定希望保存的文件路径
            //Zip2String zip2String = new Zip2String();
            //String fileName = "/Users/gengbeijun/Downloads/fund_bill_"+downloadDate+".zip";
            //boolean isSuccess =  zip2String.downLoadZip(urlStr, fileName);
            //if(isSuccess){
            //    File save_down_url = new File(fileName);
            //    try {
            //        zip2String.readZipFile(fileName);
            //        // System.out.println(connetall);
            //    } catch (Exception e) {
            //        e.printStackTrace();
            //    }
            //
            //}

            //URL url;
            //HttpURLConnection httpUrlConnection = null;
            //InputStream fis = null;
            //FileOutputStream fos = null;
            //try {
            //    url = new URL(urlStr);
            //    httpUrlConnection = (HttpURLConnection) url.openConnection();
            //    httpUrlConnection.setConnectTimeout(5 * 1000);
            //    httpUrlConnection.setDoInput(true);
            //    httpUrlConnection.setDoOutput(true);
            //    httpUrlConnection.setUseCaches(false);
            //    httpUrlConnection.setRequestMethod("GET");
            //    httpUrlConnection.setRequestProperty("CHARSET", "UTF-8");
            //    httpUrlConnection.connect();
            //    fis = httpUrlConnection.getInputStream();
            //    byte[] temp = new byte[1024];
            //    int b;
            //    fos = new FileOutputStream(new File(filePath));
            //    while ((b = fis.read(temp)) != -1) {
            //        fos.write(temp, 0, b);
            //        fos.flush();
            //    }
            //
            //    FileUtil.unZipFiles(filePath,filePath.replaceAll(".csv.zip",""));
            //
            //
            //    //// 插入成功， 删除csv 文件
            //    //for (File file : fs) {
            //    //    file.delete();
            //    //}
            //
            //
            //} catch (MalformedURLException e) {
            //    e.printStackTrace();
            //} catch (IOException e) {
            //    e.printStackTrace();
            //} finally {
            //    try {
            //        if(fis!=null) {
            //            fis.close();
            //        }
            //        if(fos!=null) {
            //            fos.close();
            //        }
            //        if(httpUrlConnection!=null) {
            //            httpUrlConnection.disconnect();
            //        }
            //    } catch (IOException e) {
            //        e.printStackTrace();
            //    }
            //}


            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }

    }

    public static void main(String args[]) {
        //
        //String ss = URLDecoder.decode("%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3CResponse%3E%3Creturn%3E%3CreturnCode%3E0000%3C%2FreturnCode%3E%3CreturnDesc%3E%E6%88%90%E5%8A%9F%3C%2FreturnDesc%3E%3CreturnFlag%3E1%3C%2FreturnFlag%3E%3CresultInfo%2F%3E%3C%2Freturn%3E%3C%2FResponse%3E");
        //
        //Element element = XmlUtil.StringToFLPurchaseXml(ss);
        //String ss = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Response><return><returnCode>0000</returnCode><returnDesc>成功</returnDesc><returnFlag>1</returnFlag><resultInfo/></return></Response>";
        //
        //
        //
        //Element element = XmlUtil.StringToFLResultXml(ss);
        //
        //
        //System.out.println(element);
        //System.out.println(element.element("returnDesc").getText());

        testAlipay("2018-05-15");
        //Zip2String.readZipFile("/Users/gengbeijun/Downloads/20180330_2088031572300210.zip");
    }


}
