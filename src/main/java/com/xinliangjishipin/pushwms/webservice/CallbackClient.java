package com.xinliangjishipin.pushwms.webservice;

import org.apache.axis2.AxisFault;

import java.rmi.RemoteException;

public class CallbackClient {
    public static String callBackToNC(String xml) {
        String returnValue = "";
        IARAPServiceStub stub;
        try {
            stub = new IARAPServiceStub();
            IARAPServiceStub.ImportBill sh = new IARAPServiceStub.ImportBill();
            sh.setString(xml);
            returnValue = stub.importBill(sh).get_return();
        } catch (AxisFault e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return returnValue;
    }


}
