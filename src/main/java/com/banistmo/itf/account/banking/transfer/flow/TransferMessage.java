package com.banistmo.itf.account.banking.transfer.flow;


import com.banistmo.commons.bso.resources.Request;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferAddRS;
import com.banistmo.logging.handler.LoggerHandler;

public class TransferMessage extends GenericMessage<XferAddRQ, XferAddRS>{
    public TransferMessage(Request<XferAddRQ> request, LoggerHandler loggerHandler) {
        super(request, loggerHandler);
    }
}
