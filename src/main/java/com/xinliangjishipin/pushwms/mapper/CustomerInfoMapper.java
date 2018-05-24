package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.CustomerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gengbeijun
 */
@Mapper
public interface CustomerInfoMapper {

    /**
     * @param accountPrefix alipayAccountprefix, alipayAccountSuffix
     * @return CustomerInfo
     */
    List<CustomerInfo> getByEmailDefAccount(@Param(value = "alipayAccountPrefix") String alipayAccountPrefix,
                                            @Param(value = "alipayAccountSuffix") String alipayAccountSuffix);

    /**
     * @param accountPrefix accountSuffix
     * @return CustomerInfo
     */
    List<CustomerInfo> getByDefAccount(@Param(value = "alipayAccountPrefix") String alipayAccountPrefix,
                                       @Param(value = "alipayAccountSuffix") String alipayAccountSuffix);

    /**
     * @param defName
     * @return CustomerInfo
     */
    List<CustomerInfo> getByFullAlipayAccount(@Param(value = "fullAlipayAccount") String fullAlipayAccount);


    CustomerInfo getByCustomerCode(@Param(value = "customerCode") String customerCode);
}