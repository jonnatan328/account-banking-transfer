package com.banistmo.itf.account.banking.transfer.flow;

import com.banistmo.commons.bso.flow.RequestProcessor;
import com.banistmo.commons.bso.pipe.PipeLine;
import com.banistmo.commons.bso.processing.LogInput;
import com.banistmo.commons.bso.processing.LogOutput;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.BusinessServices;
import com.banistmo.commons.bso.validators.POSTRequestValidator;
import com.banistmo.itf.account.banking.transfer.commons.processing.*;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferRevRS;
import com.banistmo.itf.account.banking.transfer.processing.InputCoreReverseConverter;
import com.banistmo.itf.account.banking.transfer.processing.NotifySentinel;
import com.banistmo.itf.account.banking.transfer.processing.ResponseReverseConverter;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.logging.handler.LoggerHandler;
import com.banistmo.itf.account.banking.transfer.util.TransferAppUtil;

import javax.inject.Inject;

public class TransferReverseRequestProcessor extends RequestProcessor<XferRevRQ, XferRevRS> {

    private static final String SERVICE_NAME = "transferReverse";
    private static final String SCHEMA_FILE_NAME = "/schemas/transfer-reverse.json";
    private static final POSTRequestValidator POST_REQUEST_VALIDATOR = new POSTRequestValidator(SCHEMA_FILE_NAME);

    private final PipeLine<TransferReverseMessage> pipeLine = new PipeLine<>();
    private final NotifyTransaction2<TransferReverseMessage> notifyTransactions;
    private TransferReverseMessage message;

    @Inject
    public TransferReverseRequestProcessor(CoreServiceProvider coreServiceProvider, BusinessServices businessServices) {
        super();

        notifyTransactions = new NotifyTransaction2<>(businessServices.getNotifyService());

        pipeLine.add(new LogInput<>());
        pipeLine.add(new InputRequestValidator<>(POST_REQUEST_VALIDATOR));
        pipeLine.add(new CoreTransactionConfigLoader<>(businessServices.getCoreTransactionDetailsService()));
        pipeLine.add(new InputCoreReverseConverter());
        pipeLine.add(new ServiceConfigLoader<>(businessServices.getServiceConfigServices()));
        pipeLine.add(new NotifySentinel<>(businessServices.getNotifyService(), createMapping()));
        pipeLine.add(new ConsumerCoreService<>(coreServiceProvider));
        pipeLine.add(new VerifyCoreResponse<>(businessServices.getCoreErrorTransactionsService()));
        pipeLine.add(new ResponseReverseConverter());
        pipeLine.add(notifyTransactions);
        pipeLine.add(new LogOutput<>());

        setErrorTransactionHandler(this::notifyErrorTransaction);
    }

    private NotifySentinel.RequestNotifyMapping<TransferReverseMessage> createMapping() {
        return new NotifySentinel.RequestNotifyMapping<TransferReverseMessage>()
                .mapCreditInfo(m -> m.getRequest().getBody().getXferRqMsg().getXferInfo())
                .mapMemo(m -> SERVICE_NAME)
                .mapTxn(m -> {
                    String txn = m.getInputCore().getTxn();
                    String channelId = m.getServiceConfig().getChannelId();

                    return TransferAppUtil.getTxnReverse(channelId, txn);
                });
    }

    @Override
    public XferRevRS process(Request<XferRevRQ> request, LoggerHandler loggerHandler) {
        message = new TransferReverseMessage(request, loggerHandler);

        pipeLine.process(message);

        return message.getResponse();
    }

    @Override
    public String getSvcIdent() {
        return TransferAppUtil.getSvcIdentFrom(message);
    }

    private void notifyErrorTransaction(XferRevRS responseError) {
        message.setResponse(responseError);
        notifyTransactions.process(message);
    }
}
