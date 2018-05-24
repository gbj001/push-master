package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.PurchaseOrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PurchaseOrderDetailMapper {
    /**
     * @param pkOrder
     * @return list
     */
    List<PurchaseOrderDetail> getPurchaseOrderDetail(String pkOrder);
}
