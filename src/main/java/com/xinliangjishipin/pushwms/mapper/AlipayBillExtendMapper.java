package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gengbeijun
 */
@Mapper
public interface AlipayBillExtendMapper {

    /**
     * @param billNo
     * @return AlipayBillExtend
     */
    AlipayBillExtend getByBillNo(String billNo);

    /**
     * @param businessNo
     * @return AlipayBillExtend
     */
    AlipayBillExtend getByBusinessNo(String businessNo);

    /**
     * @param alipayBillExtend
     */
    void updateMatchAlipayBillExtend(AlipayBillExtend alipayBillExtend);

    /**
     * @param alipayBillExtend
     */
    void updateSendAlipayBillExtend(AlipayBillExtend alipayBillExtend);

    /**
     * @param alipayBillExtend
     */
    void insertAlipayBillExtend(AlipayBillExtend alipayBillExtend);


    List<AlipayBillExtend> unMatchList();

    List<AlipayBillExtend> AllAlipayBillList(@Param(value = "businessNo") String businessNo,
                                             @Param(value = "startTime") String startTime,
                                             @Param(value = "endTime") String endTime,
                                             @Param(value = "importType") String importType,
                                             @Param(value = "matchStatus") String matchStatus,
                                             @Param(value = "verifyStatus") String verifyStatus,
                                             @Param(value = "responseStatus") String responseStatus
                                             );

    List<AlipayBillExtend> sendMatchedList();

    List<AlipayBillExtend> unVerifyManualAlipayAccountList();


    void manualUpdateMatchStatus(AlipayBillExtend alipayBillExtend);
}
