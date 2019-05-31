package com.commons.common.utils;

import com.commons.common.utils.snmp.CommonSnmp;
import com.commons.common.utils.snmp.CommonSnmpV2;
import com.commons.common.utils.snmp.CommonSnmpV3;
import com.commons.metadata.exception.ServiceException;

/**
 * Copyright (C)
 * SnmpUtils
 * Author: jameslinlu
 */
public class SnmpUtils {


    public static void main(String[] args) throws ServiceException{
//        CommonSnmp snmpv2 = new CommonSnmp("192.168.130.5", "public");
//        System.out.println(snmpv2.getValue("1.3.6.1.2.1.1.1.0"));
//        snmpv2.close();

        CommonSnmp snmpv3 = new CommonSnmpV3("192.168.130.8", "public", "zyuc", "authzyuc");
        System.out.println(snmpv3.getValue("1.3.6.1.4.1.2021.11.57.0"));
        snmpv3.close();
    }

    public static String getMibValue(String mib, String ip, String community) throws ServiceException {
        CommonSnmp snmpv2 = null;
        try {
            snmpv2 = new CommonSnmpV2(ip, community);
            return snmpv2.getValue(mib);
        } catch (ServiceException e) {
            throw e;
        } finally {
            snmpv2.close();
        }
    }

    public static String getMibValue(String mib, String ip, String securityName, String authPass, String privacyPass) throws ServiceException {
        CommonSnmp snmpv3 = null;
        try {
            snmpv3 = new CommonSnmpV3(ip, securityName, authPass, privacyPass);
            return snmpv3.getValue(mib);
        } catch (ServiceException e) {
            throw e;
        } finally {
            snmpv3.close();
        }
    }


}
