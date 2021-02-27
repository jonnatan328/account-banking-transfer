package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.resources.*;
import com.banistmo.itf.account.banking.transfer.dts.rq.*;

public class TransferReverseBodyRequestBuilder extends BodyRequestBuilder<TransferReverseBodyRequestBuilder>{

    protected String revRqUID;

    private TransferReverseBodyRequestBuilder(){}

    public static TransferReverseBodyRequestBuilder create() {
        return new TransferReverseBodyRequestBuilder();
    }


    public TransferReverseBodyRequestBuilder withRevRqUID(String revRqUID){
        this.revRqUID = revRqUID;
        return this;
    }
    public XferRevRQ build() {

        RevReasonCode revReasonCode = new RevReasonCode();
        revReasonCode.setRevReasonCodeSource("o79");
        revReasonCode.setRevReasonCodeValue("Reverso cualquiera");

        CredentialsRsHdr credentialsRsHdr = new CredentialsRsHdr();
        credentialsRsHdr.setSeqNum("fjdjfd");

        msgRqHdr.setCredentialsRsHdr(credentialsRsHdr);

        XferRqMsg xferRqMsg = new XferRqMsg();
        xferRqMsg.setRevRqUID(revRqUID);
        xferRqMsg.setXferInfo(xferInfo);

        XferRevRQ rq = new XferRevRQ();
        rq.setRqUID(rqUID);
        rq.setMsgRqHdr(msgRqHdr);
        rq.setRevReasonCode(revReasonCode);
        rq.setXferRqMsg(xferRqMsg);
        rq.setDueDate("10-20-2098");

        return rq;
    }
}
