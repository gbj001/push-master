package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gengbeijun
 */
@Mapper
public interface PurchaseOrderMapper {
    /**
     * @return list
     */
    List<PurchaseOrder> getPurchaseOrders();
}
