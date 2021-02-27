package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.resources.*;
import com.banistmo.itf.account.banking.transfer.dts.rq.*;

public class TransferBodyRequestBuilder extends BodyRequestBuilder<TransferBodyRequestBuilder> {

    private TransferBodyRequestBuilder(){}

    public static TransferBodyRequestBuilder create() {
        return new TransferBodyRequestBuilder();
    }

    public XferAddRQ build() {

        CredentialsRsHdr credentialsRsHdr = new CredentialsRsHdr();
        credentialsRsHdr.setSeqNum("fjdjfd");

        msgRqHdr.setCredentialsRsHdr(credentialsRsHdr);

        XferAddRQ rq = new XferAddRQ();
        rq.setRqUID(rqUID);
        rq.setMsgRqHdr(msgRqHdr);
        rq.setXferInfo(xferInfo);
        rq.setDueDate("10-20-2098");
        rq.setTransactionLabel(transactionLabel);
        return rq;
    }
}