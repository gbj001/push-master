package com.xinliangjishipin.pushwms.utils;


import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;


public class WebServiceClient {




    public String remoteClient(String remoteUrl, String xml) {
        String returnValue = "";
        //try {
        //    //调用webservice地址
        //    String endpoint = remoteUrl;
        //    String targetNamespace = "http://pub.xlj.itf.nc/IARAPService";
        //    //调用方法名
        //    String method = "importBill";
        //    Service service = new Service();
        //    //通过service创建call对象
        //    Call call = (Call) service.createCall();
        //    //设置服务地址
        //    call.setTargetEndpointAddress(new java.net.URL(endpoint));
        //    call.setOperationName(method);
        //    call.setUseSOAPAction(true);
        //    //设置调用方法
        //    call.addParameter("xml",  XMLType.SOAP_STRING, javax.xml.rpc.ParameterMode.IN);
        //    //设置返回类型
        //    call.setReturnType(XMLType.SOAP_STRING);
        //    //call.setUseSOAPAction(true);
        //    //call.setSOAPActionURI(targetNamespace + method);
        //    Object ret = null;
        //    try {
        //        //使用invoke调用方法，Object数据放传入的参数值
        //        returnValue = (String)call.invoke(new Object[]{xml});
        //    } catch (Exception e) {
        //
        //        e.printStackTrace();
        //    }
        //    //输出SOAP返回报文
        //    System.out.println("--SOAP Response: " + call.getResponseMessage().getSOAPPartAsString());
        //    //输出返回信息
        //    System.out.println("result===" + returnValue);
        //    returnValue = ret.toString();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        return returnValue;
    }
}
