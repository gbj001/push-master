package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.SaleOrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SaleOrderDetailMapper {
    /**
     * @param pkOrder
     * @return list
     */
    List<SaleOrderDetail> getSaleOrderDetail(String pkOrder);
}
