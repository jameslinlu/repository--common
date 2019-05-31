package com.commons.common.utils.snmp;


import com.commons.metadata.model.snmp.enums.SnmpAuthenticationProtocolEnums;
import com.commons.metadata.model.snmp.enums.SnmpPrivacyProtocolEnums;
import com.commons.metadata.model.snmp.enums.SnmpSecureTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

/**
 * Copyright (C)
 * CommonSnmp
 * Author: jameslinlu
 */
public class CommonSnmpV3 extends CommonSnmp {

    private static Logger logger = LoggerFactory.getLogger(CommonSnmpV3.class);


    public CommonSnmpV3(String ip, String securityName, String authPass, String privacyPass) {
        this(ip, 161, securityName, authPass, privacyPass, SnmpAuthenticationProtocolEnums.MD5, SnmpPrivacyProtocolEnums.DES, SecurityLevel.get(SnmpSecureTypeEnum.AUTH_PRIV.getCode()));//SecurityLevel.AUTH_PRIV
    }

    public CommonSnmpV3(String ip, Integer port, String securityName, String authPass, String privacyPass, SnmpAuthenticationProtocolEnums authenticationProtocol, SnmpPrivacyProtocolEnums privacyProtocol, SecurityLevel level) {
        this.initializeListen();
        this.initialize(ip, port, securityName, authPass, privacyPass, authenticationProtocol, privacyProtocol, level);//SecurityLevel.get(SecurityLevel.AUTH_PRIV)
    }


    private void initialize(String ip, Integer port, String securityName, String authPass, String privacyPass, SnmpAuthenticationProtocolEnums authenticationProtocol, SnmpPrivacyProtocolEnums privacyProtocol, SecurityLevel securityLevel) {
        AuthGeneric auth = null;
        PrivacyGeneric privacy = null;
        switch (authenticationProtocol) {
            case MD5:
                auth = new AuthMD5();
                break;
            case SHA:
                auth = new AuthSHA();
                break;
            case HMAC128SHA224:
                auth = new AuthHMAC128SHA224();
                break;
            case HMAC256SHA384:
                auth = new AuthHMAC256SHA384();
                break;
            case HMAC384SHA512:
                auth = new AuthHMAC384SHA512();
                break;
        }
        switch (privacyProtocol) {
            case DES:
                privacy = new PrivDES();
                break;
            case DES3:
                privacy = new Priv3DES();
                break;
            case AES128:
                privacy = new PrivAES128();
                break;
            case AES192:
                privacy = new PrivAES192();
                break;
            case AES256:
                privacy = new PrivAES256();
                break;
        }
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        UsmUser usmUser = new UsmUser(
                new OctetString(securityName), auth.getID(),
                new OctetString(authPass), privacy.getID(),
                new OctetString(privacyPass));
        snmp.getUSM().removeAllUsers();
        snmp.getUSM().addUser(new OctetString(securityName), usmUser);
        target = new UserTarget();
        target.setAddress(GenericAddress.parse("udp:" + ip + "/" + port));
        target.setSecurityName(new OctetString(securityName));
        target.setSecurityLevel(securityLevel.getSnmpValue());
        target.setSecurityModel(SecurityModel.SECURITY_MODEL_USM);
        target.setRetries(RETRIES);
        target.setTimeout(TIMEOUT);
        target.setVersion(SnmpConstants.version3);
    }

    public PDU getPUD(String mib) {
        PDU requestPDU = new ScopedPDU();
        requestPDU.setType(PDU.GET);
        requestPDU.setErrorIndex(0);
//        requestPDU.setErrorStatus(SnmpConstants.SNMP_ERROR_BAD_VALUE);
        requestPDU.add(new VariableBinding(new OID(mib)));
        return requestPDU;
    }


}
