package com.xinliangjishipin.pushwms.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlUtil {

    public static Element StringToAlipayBillXml(String xml) {
        Document dom = null;
        try {
            dom = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = dom.getRootElement();
        return root.element("sendresult");
    }

    public static Element StringToFLPurchaseXml(String xml) {
        Document dom = null;
        try {
            dom = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = dom.getRootElement();
        return root.element("return");
    }

    public static Element StringToFLSalesXml(String xml) {
        Document dom = null;
        try {
            dom = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = dom.getRootElement();
        return root.element("Response");
    }

    public static Element StringToFLResultXml(String xml) {
        Document dom = null;
        try {
            dom = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = dom.getRootElement();
        return root.element("return");
    }
}
