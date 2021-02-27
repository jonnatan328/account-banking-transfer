package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.itf.account.banking.transfer.builder.InputCoreBuilder;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import org.apache.commons.lang3.StringUtils;


public class InputCoreConverter implements Pipe<TransferMessage> {

    @Override
    public TransferMessage process(TransferMessage message) {
        String transactionDescriptionEnv = System.getenv("TRANSACTION_DESCRIPTION");

        XferAddRQ body = message.getRequest().getBody();
        String transactionDescription = body.getTransactionLabel();
        String acctId = body.getXferInfo().getFromAcctRef().getAcctRec().getAcctId();

        transactionDescription = StringUtils.defaultIfEmpty(transactionDescription, transactionDescriptionEnv);

        InputCore inputCore = InputCoreBuilder.create()
                .withClientId(body.getRqUID())
                .withTraceId(message.getRequest().getCloudRequestId())
                .withCoreTransaction(message.getCoreTransaction())
                .withXferInfo(body.getXferInfo())
                .withMsgRqHdr(body.getMsgRqHdr())
                .withFromAcctId(acctId)
                .withTransactionDescription(transactionDescription)
                .build();

        message.setInputCore(inputCore);
        return message;
    }
}
