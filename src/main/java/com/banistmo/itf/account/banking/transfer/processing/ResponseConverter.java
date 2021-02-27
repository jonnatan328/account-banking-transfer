package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.commons.bso.resources.Status;
import com.banistmo.commons.bso.util.StatusUtil;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferAddRS;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferRec;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.util.TransferAppUtil;

public class ResponseConverter implements Pipe<TransferMessage> {

    @Override
    public TransferMessage process(TransferMessage message) {
        String rqUID = message.getRequest().getBody().getRqUID();
        String svcIdent = TransferAppUtil.getSvcIdentFrom(message);
        Status status = StatusUtil.buildStatusOk();

        String channelId = message.getServiceConfig().getChannelId();
        String trxId = message.getOutputCore().getConsecutivoTrx();
        trxId = TransferAppUtil.getTrxId(channelId, trxId);

        XferRec xferRec = new XferRec();
        xferRec.setXferId(trxId);

        XferAddRS rs = new XferAddRS();
        rs.setRqUID(rqUID);
        rs.setStatus(status);
        rs.setXferRec(xferRec);
        rs.setSvcIdent(svcIdent);

        message.setResponse(rs);

        return message;
    }
}
