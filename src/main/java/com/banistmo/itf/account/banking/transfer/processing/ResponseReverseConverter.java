package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.commons.bso.resources.Status;
import com.banistmo.commons.bso.util.StatusUtil;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferRevRS;
import com.banistmo.itf.account.banking.transfer.flow.TransferReverseMessage;
import com.banistmo.itf.account.banking.transfer.util.TransferAppUtil;

public class ResponseReverseConverter implements Pipe<TransferReverseMessage> {

    @Override
    public TransferReverseMessage process(TransferReverseMessage message) {
        String rqUID = message.getRequest().getBody().getRqUID();
        Status status = StatusUtil.buildStatusOk();

        String txn = message.getInputCore().getTxn();
        String channelId = message.getServiceConfig().getChannelId();
        String txnReverse = TransferAppUtil.getTxnReverse(channelId, txn);

        XferRevRS rs = new XferRevRS();
        rs.setRqUID(rqUID);
        rs.setStatus(status);
        rs.setSvcIdent(txnReverse);

        message.setResponse(rs);

        return message;
    }
}
