/**
 * Exception.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.7  Built on : Nov 20, 2017 (11:41:20 GMT)
 */
package com.xinliangjishipin.pushwms.webservice;

public class Exception extends java.lang.Exception {
    private static final long serialVersionUID = 1524570496741L;
    private com.xinliangjishipin.pushwms.webservice.IARAPServiceStub.ExceptionE faultMessage;

    public Exception() {
        super("Exception");
    }

    public Exception(String s) {
        super(s);
    }

    public Exception(String s, Throwable ex) {
        super(s, ex);
    }

    public Exception(Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
        com.xinliangjishipin.pushwms.webservice.IARAPServiceStub.ExceptionE msg) {
        faultMessage = msg;
    }

    public com.xinliangjishipin.pushwms.webservice.IARAPServiceStub.ExceptionE getFaultMessage() {
        return faultMessage;
    }
}
