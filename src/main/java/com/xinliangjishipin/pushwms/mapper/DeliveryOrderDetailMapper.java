package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.DeliveryOrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeliveryOrderDetailMapper {
    /**
     * @param pkOrder
     * @return list
     */
    List<DeliveryOrderDetail> getDeliveryOrderDetail(String pkOrder);
}
