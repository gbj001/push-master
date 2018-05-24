package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.OrderExchangeInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gengbeijun
 */
@Mapper
public interface OrderExchangeInfoMapper {

    /**
     * @param outOrderId
     * @return
     */
    OrderExchangeInfo getByOutOrderId(String outOrderId);

    /**
     * @param orderExchangeInfo
     */
    void updateOrderExchangeInfo(OrderExchangeInfo orderExchangeInfo);

    /**
     * @param orderExchangeInfo
     */
    void insertOrderExchangeInfo(OrderExchangeInfo orderExchangeInfo);

    /***
     * @param
     */

    List<OrderExchangeInfo> failPushOrderList();
}
