package com.commons.common.utils.snmp;


import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

/**
 * Copyright (C)
 * CommonSnmp
 * Author: jameslinlu
 */
public abstract class CommonSnmp {

    private static Logger logger = LoggerFactory.getLogger(CommonSnmp.class);

    protected static final Integer RETRIES = 1;
    protected static final Integer TIMEOUT = 3000;

    protected Snmp snmp = null;
    protected Target target = null;


    public void initializeListen() {
        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();
        } catch (IOException e) {
            logger.error("initialize Snmp transport Fail ", e);
        }
    }

    public void close() {
        try {
            snmp.close();
        } catch (IOException e) {
            logger.error("destroy Snmp transport Fail ", e);
        }
    }


    public abstract PDU getPUD(String mib);

    public String getValue(String mib) throws ServiceException {
        return this.getValue(this.getPUD(mib));
    }

    public String getValue(PDU requestPDU) throws ServiceException {
        try {
            ResponseEvent response = snmp.get(requestPDU, target);

            PDU responsePDU = response.getResponse();
            if (responsePDU == null || responsePDU.getVariableBindings().size() == 0 || responsePDU.getVariableBindings().get(0).getVariable().toString().equals("noSuchInstance")) {
                throw new ServiceException(ResultCode.ERROR_SEARCH);
            }
            // 错误校验 ，收到pdu类型为report not response
            // 错误类型在 SnmpConstants  printReport中查看
            if (responsePDU.getType() == PDU.REPORT) {
                throw new ServiceException(ResultCode.ERROR_SEARCH);
            }
            return responsePDU.getVariableBindings().get(0).getVariable().toString();
        } catch (IOException e) {
            logger.error("Snmp Get mib Value Fail", e);
            throw new ServiceException(ResultCode.ERROR_SEARCH);
        }
    }


}
