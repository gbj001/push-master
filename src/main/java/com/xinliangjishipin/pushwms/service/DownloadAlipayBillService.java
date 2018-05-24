package com.xinliangjishipin.pushwms.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import com.xinliangjishipin.pushwms.entity.AlipayBillInfo;
import com.xinliangjishipin.pushwms.mapper.AlipayBillExtendMapper;
import com.xinliangjishipin.pushwms.mapper.AlipayBillInfoMapper;
import com.xinliangjishipin.pushwms.utils.Zip2String;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DownloadAlipayBillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${alipay.serverUrl}")
    private String serverUrl;

    @Value("${alipay.appId}")
    private String appId;

    @Value("${alipay.privateKey}")
    private String privateKey;

    @Value("${alipay.publicKey}")
    private String publicKey;

    @Value("${alipay.downloadPath}")
    private String downloadPath;

    @Value("${to.email}")
    private String alipayMailReceiver;

    @Autowired
    private AlipayBillInfoMapper alipayBillInfoMapper;

    @Autowired
    private AlipayBillExtendMapper alipayBillExtendMapper;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private VerifyManualAlipayAccountService verifyManualAlipayAccountService;


    public void downloadAlipayByDate(String downloadDate) {
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "gbk", publicKey, "RSA2");
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        request.setBizContent("{" +
                "\"bill_type\":\"signcustomer\"," +
                "\"bill_date\":\"" + downloadDate + "\"" +
                "  }");
        AlipayDataDataserviceBillDownloadurlQueryResponse response = null;


        try {
            response = alipayClient.execute(request);
            if (response.isSuccess()) {
                //将接口返回的对账单下载地址传入urlStr
                String urlStr = response.getBillDownloadUrl();
                //指定希望保存的文件路径
                String fileName = downloadPath + "bill_" + downloadDate + ".zip";
                boolean isSuccess = Zip2String.downLoadZip(urlStr, fileName);
                if (isSuccess) {
                    int downloadAlipayBillCount = 0;
                    try {
                        String content = Zip2String.readZipFile(fileName);
                        if (!"".equals(content)) {
                            //指保存转账的记录（目前通过businessType类型来区分）
                            List<AlipayBillInfo> alipayBillInfoList = this.convertToAlipayBillInfoList(content);
                            for (AlipayBillInfo alipayBillInfo : alipayBillInfoList) {
                                downloadAlipayBillCount++;
                                AlipayBillInfo currentAlipayBillInfo = alipayBillInfoMapper.getByBusinessNo(alipayBillInfo.getBusinessNo());
                                if (currentAlipayBillInfo != null) {
                                    continue;
                                }
                                processData(alipayBillInfo);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    //发送邮件，提示下载的账单数量
                    try {
                        sendMailService.sendTextMail("支付宝账单下载结果",downloadDate + "支付宝账单数："+downloadAlipayBillCount+" 条", alipayMailReceiver);
                    } catch (Exception e) {
                        logger.info("邮件发送失败：" + e.getMessage());
                    }
                }
            } else {
                try {
                    sendMailService.sendTextMail("支付宝账单下载结果",downloadDate + "没有支付宝账单", alipayMailReceiver);
                } catch (Exception e) {
                    logger.info("邮件发送失败：" + e.getMessage());
                }
                logger.info("下载对账单失败：" + response.getMsg());
            }

        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void processData(AlipayBillInfo alipayBillInfo) {
        alipayBillInfoMapper.insertAlipayBillInfo(alipayBillInfo);
        AlipayBillExtend alipayBillExtend = new AlipayBillExtend(alipayBillInfo);
        alipayBillExtend.setImportType("S");
        alipayBillExtend.setCreatedUser("system");
        alipayBillExtend.setCreatedTime(new Date());
        alipayBillExtend.setUpdatedUser("");
        alipayBillExtend.setUpdatedTime(new Date());
        //检查是否有手工录入的相同的业务流水号
        AlipayBillExtend manualAlipayBillExtend = alipayBillExtendMapper.getByBusinessNo(alipayBillExtend.getBusinessNo());
        if (manualAlipayBillExtend != null) {
            //分别校验支付宝账号名称、支付宝账号和金额是否全部相等
            if (!verifyManualAlipayAccountService.compareManualAndAuto(manualAlipayBillExtend, alipayBillInfo)) {
                verifyManualAlipayAccountService.updateVerifyManualAlipayAccount(manualAlipayBillExtend, alipayBillInfo);
            }
        } else {
            //系统下载的账单默认都为校验状态
            alipayBillExtend.setVerifyStatus("1");
            alipayBillExtend.setVerifyTime(new Date());
            alipayBillExtend.setVerifyText("系统下载时自动校验成功");
            alipayBillExtendMapper.insertAlipayBillExtend(alipayBillExtend);
        }

    }

    private List<AlipayBillInfo> convertToAlipayBillInfoList(String content) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AlipayBillInfo> alipayBillInfoList = new ArrayList<AlipayBillInfo>();
        int totalLength = content.split("\\|").length;
        int startIndex = 6;
        int endIndex = totalLength - 4;
        for (int i = startIndex; i < endIndex; i++) {
            AlipayBillInfo alipayBillInfo = new AlipayBillInfo();
            String[] info = content.split("\\|")[i].replaceAll("\t", "").split(",");
            //过滤掉类型为非其他的类型
            if(!"其它".equals(info[10])){
                continue;
            }
            alipayBillInfo.setBillNo(info[0]);
            alipayBillInfo.setBusinessNo(info[1].trim());
            //alipayBillInfo.setMerchantOrderNo(info[2]);
            //alipayBillInfo.setGoodsName(info[3]);
            alipayBillInfo.setTradeTime(sdf.parse(info[4]));
            alipayBillInfo.setSourceName(info[5].substring(0, info[5].indexOf("(")));
            alipayBillInfo.setSourceAccount(info[5].substring(info[5].indexOf("(") + 1, info[5].length() - 1));
            alipayBillInfo.setIncomeAmount(info[6]);
            //alipayBillInfo.setExpendAmount(Double.parseDouble(info[7]));
            //alipayBillInfo.setTradeChannel(info[9]);
            alipayBillInfo.setBusinessType(info[10]);
            alipayBillInfo.setRemark((info.length > 11 ? info[11] : ""));
            alipayBillInfo.setCreatedTime(new Date());
            alipayBillInfoList.add(alipayBillInfo);
        }
        return alipayBillInfoList;
    }
}
