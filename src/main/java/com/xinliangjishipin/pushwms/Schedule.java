package com.xinliangjishipin.pushwms;


import com.xinliangjishipin.pushwms.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Schedule {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderExchangeInfoService orderExchangeInfoService;

    @Autowired
    private DownloadAlipayBillService downloadAlipayBillService;

    @Autowired
    private MatchAlipayAccountService matchAlipayAccountService;

    @Autowired
    private SendBatchAlipayBillService sendBatchAlipayBillService;

    @Autowired
    private VerifyManualAlipayAccountService verifyManualAlipayAccountService;

    /**
     *采购订单推送计划任务，每5分钟执行一次
     */
    @Scheduled(cron="0 0/30 * * * ?")
    public void poOrderPush() throws Exception {
        logger.info("start PoOrderPushTask......");
        long startTime = System.currentTimeMillis();
         orderExchangeInfoService.pushPurchaseOrderProcess();
        long endTime = System.currentTimeMillis();
        logger.info("end PoOrderPushTask....., totalTime:" + (endTime - startTime)/1000 + " seconds");
    }

    /**
     *销售订单推送计划任务，每1分钟执行一次
     */
    @Scheduled(cron="0 0/30 * * * ?")
    public void soSaleOrderPush() {
        logger.info("start SoSaleOrderPushTask......");
        long startTime = System.currentTimeMillis();
        orderExchangeInfoService.pushSaleOrderProcess();
        long endTime = System.currentTimeMillis();
        logger.info("end SoSaleOrderPushTask......, totalTime:" + (endTime - startTime)/1000 + " seconds");
    }

    /**
     *销售发货单推送计划任务，每35分钟执行一次
     */
    @Scheduled(cron="0 0/30 * * * ?")
    public void soDeliveryOrderPush() {
        logger.info("start SalesDeliveryOrderPushTask......");
        long startTime = System.currentTimeMillis();
        orderExchangeInfoService.saleDeliveryProcess();
        long endTime = System.currentTimeMillis();
        logger.info("end SalesDeliveryOrderPushTask......, totalTime:" + (endTime - startTime)/1000 + " seconds");
    }


    /**
     *推送订单监控机制，监控推送失败的订单发送邮件
     */
    @Scheduled(cron="0 0/35 9-20 * * ?")
    public void monitoredPushOrderFail() {
        logger.info("start monitored push order Fail......");
        long startTime = System.currentTimeMillis();
        orderExchangeInfoService.monitoredPushOrder();
        long endTime = System.currentTimeMillis();
        logger.info("end monitored push order Fail......, totalTime:" + (endTime - startTime)/1000 + " seconds");
    }

    /******************************************************************************************************************/

    /**
     * 支付宝对账单下载计划任务，每天上午6:00执行一次
     */
    @Scheduled(cron = "0 00 06 ? * *")
    public void downloadAlipayBill() {
        logger.info("start downloadAlipayBill......");
        long startTime = System.currentTimeMillis();
        LocalDate today = LocalDate.now();
        downloadAlipayBillService.downloadAlipayByDate(today.minusDays(1).toString());
        long endTime = System.currentTimeMillis();
        logger.info("end downloadAlipayBill....., totalTime:" + (endTime - startTime) / 1000 + " seconds");
    }

    /**
     * 支付宝对账单下载计划任务，每天上午7:00执行一次
     */
    @Scheduled(cron = "0 00 07 ? * *")
    public void downloadAlipayBill2() {
        logger.info("start downloadAlipayBill......");
        long startTime = System.currentTimeMillis();
        LocalDate today = LocalDate.now();
        downloadAlipayBillService.downloadAlipayByDate(today.minusDays(1).toString());
        long endTime = System.currentTimeMillis();
        logger.info("end downloadAlipayBill....., totalTime:" + (endTime - startTime) / 1000 + " seconds");
    }

    /**
     * 支付宝对账单下载计划任务，每天上午8:00执行一次
     */
    @Scheduled(cron = "0 00 08 ? * *")
    public void downloadAlipayBill3() {
        logger.info("start downloadAlipayBill......");
        long startTime = System.currentTimeMillis();
        LocalDate today = LocalDate.now();
        downloadAlipayBillService.downloadAlipayByDate(today.minusDays(1).toString());
        long endTime = System.currentTimeMillis();
        logger.info("end downloadAlipayBill....., totalTime:" + (endTime - startTime) / 1000 + " seconds");
    }

    /**
     * 支付宝账单中的账号和NC对应的客户信息匹配任务，每隔10分钟执行一次
     */
    @Scheduled(cron="0 0/10 * * * ?")
    public void matchAlipayAccount() {
        logger.info("start matchAlipayAccount......");
        long startTime = System.currentTimeMillis();
        matchAlipayAccountService.matchAlipayAccount();
        long endTime = System.currentTimeMillis();
        logger.info("end matchAlipayAccount....., totalTime:" + (endTime - startTime) / 1000 + " seconds");
    }

    /**
     * 校验手工录入的信息是否和系统下载的是否一致的任务，每隔15分钟执行一次
     */
    @Scheduled(cron="0 0/15 * * * ?")
    public void verifyManualAlipayAccount() {
        logger.info("start verifyManualAlipayAccount......");
        long startTime = System.currentTimeMillis();
        verifyManualAlipayAccountService.verifyManualAlipayAccount();
        long endTime = System.currentTimeMillis();
        logger.info("end verifyManualAlipayAccount....., totalTime:" + (endTime - startTime) / 1000 + " seconds");
    }

    /**
     * 发送匹配完成的客户推送到NC，每隔10分钟执行一次
     */
    @Scheduled(cron="0 0/10 * * * ?")
    public void sendBatchAlipayAccountToNC() {
        logger.info("start sendAlipayAccountToNC......");
        long startTime = System.currentTimeMillis();
        sendBatchAlipayBillService.sendBatchAlipayBill();
        long endTime = System.currentTimeMillis();
        logger.info("end sendAlipayAccountToNC....., totalTime:" + (endTime - startTime) / 1000 + " seconds");
    }
}
