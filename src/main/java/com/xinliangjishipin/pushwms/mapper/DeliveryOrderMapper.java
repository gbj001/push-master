package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.DeliveryOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gengbeijun
 */
@Mapper
public interface DeliveryOrderMapper {
    /**
     * @return list
     */
    List<DeliveryOrder> getDeliveryOrders();
}
