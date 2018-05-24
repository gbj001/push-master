package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.SaleOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gengbeijun
 */
@Mapper
public interface SaleOrderMapper {
    /**
     * @return list
     */
    List<SaleOrder> getSaleOrders();
}
