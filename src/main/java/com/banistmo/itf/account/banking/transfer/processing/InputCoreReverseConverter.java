package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.itf.account.banking.transfer.builder.InputCoreBuilder;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferReverseMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import org.apache.commons.lang3.StringUtils;


public class InputCoreReverseConverter implements Pipe<TransferReverseMessage> {

    @Override
    public TransferReverseMessage process(TransferReverseMessage message) {
        String transactionDescriptionEnv = System.getenv("TRANSACTION_DESCRIPTION");

        XferRevRQ body = message.getRequest().getBody();
        String acctId = body.getXferRqMsg().getXferInfo().getFromAcctRef().getAcctRec().getAcctId();
        String revRqUID = body.getXferRqMsg().getRevRqUID();

        String transactionDescription = body.getTransactionLabel();

        transactionDescription = StringUtils.defaultIfEmpty(transactionDescription, transactionDescriptionEnv);

        InputCore inputCore = InputCoreBuilder.create()
                .withClientId(body.getRqUID())
                .withTraceId(message.getRequest().getCloudRequestId())
                .withCoreTransaction(message.getCoreTransaction())
                .withXferInfo(body.getXferRqMsg().getXferInfo())
                .withMsgRqHdr(body.getMsgRqHdr())
                .withFromAcctId(acctId)
                .withTransactionDescription(transactionDescription)
                .withReverse(true)
                .withRevRqUID(revRqUID)
                .build();

        message.setInputCore(inputCore);
        return message;
    }
}
