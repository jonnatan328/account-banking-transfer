package com.banistmo.itf.account.banking.transfer.builder;

import com.banistmo.commons.bso.resources.ContextRqHdr;
import com.banistmo.commons.bso.resources.MsgRqHdr;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferInfo;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public final class InputCoreBuilder {
    private String clientId;
    private String traceId;
    private String indicador;
    private String txn;
    private String channel;
    private String source;
    private String user;
    private String clientTerminalSeqNum;
    private String posLocation;
    private BigDecimal curAmt;
    private String fromAcctId;
    private String toAcctId;
    private String txn1;
    private String txn2;
    private String description;
    private boolean reverse;
    private String revRqUID;
    private String cnbTxn;
    private String cnbTxnRev;
    private MsgRqHdr msgRqHdr;
    private CoreTransaction coreTransaction;

    private InputCoreBuilder() {
        indicador = System.getenv("INDICADOR");
        cnbTxn = System.getenv("CNB_TXN");
        cnbTxnRev = System.getenv("CNB_TXN_REV");
    }

    public static InputCoreBuilder create() {
        return new InputCoreBuilder();
    }

    public InputCoreBuilder withClientId(String clientId){
        this.clientId = clientId;
        return this;
    }

    public InputCoreBuilder withTraceId(String traceId){
        this.traceId = traceId;
        return this;
    }

    public InputCoreBuilder withCoreTransaction(CoreTransaction coreTransaction) {
        this.coreTransaction = coreTransaction;
        return this;
    }

    public InputCoreBuilder withXferInfo(XferInfo xferInfo) {
        curAmt = xferInfo.getCurAmt().getAmt();
        fromAcctId = xferInfo.getFromAcctRef().getAcctRec().getAcctId();
        toAcctId = xferInfo.getToAcctRef().getAcctRec().getAcctId();
        return this;
    }

    public InputCoreBuilder withMsgRqHdr(MsgRqHdr msgRqHdr) {
        this.msgRqHdr = msgRqHdr;
        return this;
    }

    public InputCoreBuilder withFromAcctId(String acctId) {
        this.fromAcctId = acctId;
        return this;
    }

    public InputCoreBuilder withTransactionDescription(String description) {
        this.description = description;
        return this;
    }

    public InputCoreBuilder withReverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    public InputCoreBuilder withRevRqUID(String revRqUID) {
        this.revRqUID = revRqUID;
        return this;
    }

    public InputCore build() {

        loadTransactionConfig();

        InputCore inputCore = new InputCore();
        inputCore.setClientId(clientId);
        inputCore.setTraceId(traceId);
        inputCore.setIndicador(indicador);
        inputCore.setTxn(txn);
        inputCore.setCanal(channel.toUpperCase());
        inputCore.setUsuario(user);
        inputCore.setFuente(source);
        inputCore.setCajero(StringUtils.leftPad(clientTerminalSeqNum, 6, "0"));
        inputCore.setSucursal(StringUtils.leftPad(posLocation, 5, "0"));
        inputCore.setCuenta1(StringUtils.leftPad(fromAcctId, 10, "0"));
        inputCore.setCuenta2(StringUtils.leftPad(toAcctId, 10, "0"));
        inputCore.setMonto(curAmt);
        inputCore.setTran01(txn1);
        inputCore.setTran02(txn2);
        inputCore.setDescripcion(description);
        inputCore.setConsecutivo(revRqUID);
        inputCore.setReverso(reverse ? 1 : 0);

        return inputCore;
    }

    private void loadTransactionConfig() {
        ContextRqHdr contextRqHdr = msgRqHdr.getContextRqHdr();

        channel = contextRqHdr.getChannelId();

        if ("cnb".equalsIgnoreCase(channel)) {
            loadFromContextRqHdr(contextRqHdr);
        } else {
            loadFromCoreTransaction();
        }
    }

    private void loadFromContextRqHdr(ContextRqHdr contextRqHdr) {
        txn = (reverse) ? cnbTxnRev : cnbTxn;
        posLocation = contextRqHdr.getPointOfServiceData().getPosLocation();
        clientTerminalSeqNum = contextRqHdr.getClientTerminalSeqNum();
    }

    private void loadFromCoreTransaction() {
        txn = coreTransaction.getServiceType();
        source = coreTransaction.getSourceDefault();
        user = coreTransaction.getUserOperation();
        clientTerminalSeqNum = coreTransaction.getCashierOperation();
        posLocation = coreTransaction.getCashOperation();

        for (TransactionConfig tc : coreTransaction.getTransactionConfig()) {
            if (!tc.isRev()) {
                if (txn1 == null && "dr".equals(tc.getTrxType())) {
                    txn1 = tc.getTrx();
                } else if (txn2 == null && "cr".equals(tc.getTrxType())) {
                    txn2 = tc.getTrx();
                }

                if (txn1 != null && txn2 != null) {
                    break;
                }
            }
        }
    }
}
