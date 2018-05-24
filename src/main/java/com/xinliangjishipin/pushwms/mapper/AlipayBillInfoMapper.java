package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.AlipayBillInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author gengbeijun
 */
@Mapper
public interface AlipayBillInfoMapper {

    /**
     * @param businessNo
     * @return AlipayBillExtend
     */
    AlipayBillInfo getByBusinessNo(String businessNo);

    /**
     * @param alipayBillInfo
     */
    void insertAlipayBillInfo(AlipayBillInfo alipayBillInfo);
}
