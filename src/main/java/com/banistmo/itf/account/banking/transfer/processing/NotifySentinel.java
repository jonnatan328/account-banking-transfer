package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.builders.NotifySentinelBuilder;
import com.banistmo.commons.bso.processing.AbstractNotifyPipe;
import com.banistmo.commons.bso.resources.CommonBodyRequest;
import com.banistmo.commons.bso.resources.Response;
import com.banistmo.commons.bso.services.notify.NotifyService;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferInfo;
import com.banistmo.itf.account.banking.transfer.flow.GenericMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;


import java.math.BigDecimal;
import java.util.function.Function;

public class NotifySentinel <T extends GenericMessage<? extends CommonBodyRequest, ? extends Response>>
        extends AbstractNotifyPipe<T> {


    private static final String SERVICE_IDENTIFY = "notifyBamSNS";
    private static final String TOPIC_NAME = System.getenv("NOTIFY_BAM_TOPIC_ARN");
    private final RequestNotifyMapping<T> requestNotifyMapping;

    public NotifySentinel(NotifyService notifyService, RequestNotifyMapping<T> requestNotifyMapping) {
        super(notifyService, TOPIC_NAME, SERVICE_IDENTIFY);

        this.requestNotifyMapping = requestNotifyMapping;
    }

    @Override
    public T process(T message) {

        if (message.getServiceConfig().isNotifyCompliance()) {
            return super.process(message);
        }

        return message;
    }


    @Override
    protected Object createNotify(T message) {
        CommonBodyRequest body = message.getRequest().getBody();
        InputCore inputCore = message.getInputCore();

        XferInfo xferInfo = requestNotifyMapping.xfertInfoExtractor.apply(message);
        String txn = requestNotifyMapping.txnExtractor.apply(message);
        String memo = requestNotifyMapping.memoExtractor.apply(message);

        String acctId = xferInfo.getFromAcctRef().getAcctRec().getAcctId();
        BigDecimal curAmt = xferInfo.getCurAmt().getAmt();
        String curCode = xferInfo.getCurAmt().getCurCode();

        return NotifySentinelBuilder.standard()
                .withRqUID(body.getRqUID())
                .withDueDate(body.getDueDate())
                .withFromAcctId(acctId)
                .withCurAmtAndCode(curAmt, curCode)
                .withPosLocation(inputCore.getSucursal())
                .withSeqNum(txn)
                .withMemo(memo)
                .build();
    }

    public static class RequestNotifyMapping<T extends GenericMessage<? extends CommonBodyRequest, ? extends Response>> {
        private Function<T, XferInfo> xfertInfoExtractor;
        private Function<T, String> txnExtractor;
        private Function<T, String> memoExtractor;

        public RequestNotifyMapping() {
            this.txnExtractor = m -> m.getInputCore().getTxn();
            this.memoExtractor = m -> "";
        }

        public RequestNotifyMapping<T> mapCreditInfo(Function<T, XferInfo> debitInfoExtractor){
            this.xfertInfoExtractor = debitInfoExtractor;
            return this;
        }

        public RequestNotifyMapping<T> mapTxn(Function<T, String> txnExtractor){
            this.txnExtractor = txnExtractor;
            return this;
        }

        public RequestNotifyMapping<T> mapMemo(Function<T, String> memoExtractor){
            this.memoExtractor = memoExtractor;
            return this;
        }
    }
}
