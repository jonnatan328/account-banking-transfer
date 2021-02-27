package com.banistmo.itf.account.banking.transfer.flow;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferRevRS;
import com.banistmo.logging.handler.LoggerHandler;

public class TransferReverseMessage extends GenericMessage<XferRevRQ, XferRevRS> {
    public TransferReverseMessage(Request<XferRevRQ> request, LoggerHandler loggerHandler) {
        super(request, loggerHandler);
    }
}
