package com.commons.common.utils.snmp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

/**
 * Copyright (C)
 * CommonSnmp
 * Author: jameslinlu
 */
public class CommonSnmpV2 extends CommonSnmp {

    private static Logger logger = LoggerFactory.getLogger(CommonSnmpV2.class);

//    public static void main(String[] args) throws Exception {
//        String ip = "10.255.114.17";//苏州
//        Integer port = 161;
//        String securityName = "snmpv3root";
//        String authPass = "CtcClouds@123";
//        String privacyPass = "CtcClouds@1234";
//        CommonSnmpV3 v3 = new CommonSnmpV3(ip, port, securityName, authPass, privacyPass,
//                SnmpAuthenticationProtocolEnums.SHA, SnmpPrivacyProtocolEnums.AES128, SecurityLevel.authPriv);
//        v3.getValue("");

        /*
        ip = "10.255.114.18";//苏州
        port = 161;
        securityName = "snmpv3root";
        authPass = "CtcClouds@123";
        privacyPass = "CtcClouds@1234";
        v3 = new CommonSnmpV3(ip, port, securityName, authPass, privacyPass,
                SnmpAuthenticationProtocolEnums.SHA, SnmpPrivacyProtocolEnums.AES128, SecurityLevel.authPriv);

        ip = "42.123.66.2";//42.123.66.6  内蒙 2个ip  CtcClouds@1234
        port = 161;
        securityName = "nmnms";
        authPass = "%$%$fy'fG,Z2h(Lnx$VF2iNA@cZQ%$%$";
        privacyPass = "%$%$GtXqNn]B%EN$!h#l}bD)@g^U%$%$";
        v3 = new CommonSnmpV3(ip, port, securityName, authPass, privacyPass,
                SnmpAuthenticationProtocolEnums.MD5, SnmpPrivacyProtocolEnums.AES128, SecurityLevel.authPriv);

        ip = "42.123.66.10";//42.123.66.14 内蒙 2个ip
        port = 161;
        securityName = "nmnms";
        authPass = "%$%$wZ~rW\";*SQE'Xs/mjv/7{KB9%$%$";
        privacyPass = "%$%$P:av7!b>5H/YWdSKQ|`0{OF=%$%$";
        v3 = new CommonSnmpV3(ip, port, securityName, authPass, privacyPass,
                SnmpAuthenticationProtocolEnums.MD5, SnmpPrivacyProtocolEnums.AES128, SecurityLevel.authPriv);
        */

//    }

    public CommonSnmpV2(String ip, String community) {
        this(ip, 161, community);
    }

    public CommonSnmpV2(String ip, Integer port, String community) {
        this.initializeListen();
        this.initialize(ip, port, community);
    }

    private void initialize(String ip, Integer port, String community) {
        target = new CommunityTarget();
        target.setSecurityName(new OctetString(community));
        target.setAddress(GenericAddress.parse("udp:" + ip + "/" + port));
        target.setRetries(RETRIES);
        target.setTimeout(TIMEOUT);
        target.setVersion(SnmpConstants.version2c);
    }

    public PDU getPUD(String mib) {
        PDU requestPDU = new PDU();
        requestPDU.setType(PDU.GET);
        requestPDU.add(new VariableBinding(new OID(mib)));
        return requestPDU;
    }


}
