package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.entity.PurchaseOrder;
import com.xinliangjishipin.pushwms.mapper.PurchaseOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gengbeijun
 */
@Service
public class POrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrderMapper.getPurchaseOrders();
    }
}
