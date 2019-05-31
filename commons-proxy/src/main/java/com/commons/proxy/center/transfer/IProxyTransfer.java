package com.commons.proxy.center.transfer;

import com.commons.metadata.exception.ServiceException;
import com.commons.proxy.center.transfer.model.TransferRequestMessage;
import com.commons.proxy.center.transfer.model.TransferResponseMessage;

/**
 * Copyright (C)
 * IProxyTransfer
 * Author: jameslinlu
 */
public interface IProxyTransfer {

    TransferResponseMessage send(TransferRequestMessage requestMessage) throws ServiceException;


}
