package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.resources.ContextRqHdr;
import com.banistmo.commons.bso.resources.MsgRqHdr;
import com.banistmo.commons.bso.resources.PointOfServiceData;
import com.banistmo.commons.bso.resources.PosAgent;
import com.banistmo.itf.account.banking.transfer.dts.rq.*;

import java.math.BigDecimal;

public abstract class BodyRequestBuilder<S extends BodyRequestBuilder> {

    protected XferInfo xferInfo;
    protected MsgRqHdr msgRqHdr;
    protected String rqUID;
    protected String transactionLabel;

    protected BodyRequestBuilder() {
        xferInfo = new XferInfo();
        msgRqHdr = new MsgRqHdr();
    }

    public S withRqUID(String rqUID) {
        this.rqUID = rqUID;
        return getSubType();
    }

    public S withCurAmt(BigDecimal amt) {
        CurAmt compositeCurAmt = new CurAmt();
        compositeCurAmt.setAmt(amt);
        compositeCurAmt.setCurCode("USD");

        xferInfo.setCurAmt(compositeCurAmt);
        return getSubType();
    }

    public S withClientTerminalSeqNum(String clientTerminalSeqNum) {
        ContextRqHdr contextRqHdr = getContextRqHdr();
        contextRqHdr.setClientTerminalSeqNum(clientTerminalSeqNum);

        return getSubType();
    }

    public S withServiceType(String serviceType) {
        ContextRqHdr contextRqHdr = getContextRqHdr();
        contextRqHdr.setServiceType(serviceType);

        return getSubType();
    }

    public S withChannelId(String channelId) {
        ContextRqHdr contextRqHdr = getContextRqHdr();
        contextRqHdr.setChannelId(channelId);

        return getSubType();
    }

    public S withPosLocation(String posLocation) {
        PosAgent posAgent = new PosAgent();
        posAgent.setAgentIdent("id");

        PointOfServiceData pointOfServiceData = new PointOfServiceData();
        pointOfServiceData.setPosLocation(posLocation);
        pointOfServiceData.setPosAgent(posAgent);

        ContextRqHdr contextRqHdr = getContextRqHdr();
        contextRqHdr.setPointOfServiceData(pointOfServiceData);

        return getSubType();
    }

    private ContextRqHdr getContextRqHdr() {
        ContextRqHdr contextRqHdr = msgRqHdr.getContextRqHdr();

        if (contextRqHdr == null) {
            contextRqHdr = new ContextRqHdr();
            msgRqHdr.setContextRqHdr(contextRqHdr);
        }

        return contextRqHdr;
    }

    public S withFromAcctId(String acctId) {
        AcctRec acctRec = new AcctRec();
        acctRec.setAcctId(acctId);

        FromAcctRef fromAcctRef = new FromAcctRef();
        fromAcctRef.setAcctRec(acctRec);

        xferInfo.setFromAcctRef(fromAcctRef);
        return getSubType();
    }

    public S withTrxCode(String trxCode) {
        ContextRqHdr contextRqHdr = getContextRqHdr();
        contextRqHdr.setTrxCode(trxCode);

        return getSubType();
    }

    public S withToAcctId(String acctId) {
        AcctRec acctRec = new AcctRec();
        acctRec.setAcctId(acctId);

        ToAcctRef toAcctRef = new ToAcctRef();
        toAcctRef.setAcctRec(acctRec);

        xferInfo.setToAcctRef(toAcctRef);
        return getSubType();
    }

    public S withUserInterface(String userInterface) {
        ContextRqHdr contextRqHdr = getContextRqHdr();
        contextRqHdr.setUserInterface(userInterface);

        return getSubType();
    }


    public S withSourceDefault(String sourceDefault){
        ContextRqHdr contextRqHdr = getContextRqHdr();
        contextRqHdr.setSourceDefault(sourceDefault);

        return getSubType();
    }

    public S withTransactionLabel(String transactionLabel){
        this.transactionLabel = transactionLabel;

        return getSubType();
    }

    @SuppressWarnings("unchecked")
    private S getSubType() {
        return (S) this;
    }
}
