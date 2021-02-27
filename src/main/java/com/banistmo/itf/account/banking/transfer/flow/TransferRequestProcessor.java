package com.banistmo.itf.account.banking.transfer.flow;

import com.banistmo.commons.bso.flow.RequestProcessor;
import com.banistmo.commons.bso.pipe.PipeLine;
import com.banistmo.commons.bso.processing.LogInput;
import com.banistmo.commons.bso.processing.LogOutput;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.BusinessServices;
import com.banistmo.commons.bso.validators.POSTRequestValidator;
import com.banistmo.itf.account.banking.transfer.commons.processing.*;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferAddRS;
import com.banistmo.itf.account.banking.transfer.processing.InputCoreConverter;
import com.banistmo.itf.account.banking.transfer.processing.NotifySentinel;
import com.banistmo.itf.account.banking.transfer.processing.NotifySentinel.*;
import com.banistmo.itf.account.banking.transfer.processing.ResponseConverter;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.util.TransferAppUtil;
import com.banistmo.logging.handler.LoggerHandler;

import javax.inject.Inject;

public class TransferRequestProcessor extends RequestProcessor<XferAddRQ, XferAddRS> {

    private static final String SERVICE_NAME = "transfer";
    private static final String SCHEMA_FILE_NAME = "/schemas/transfer.json";
    private static final POSTRequestValidator POST_REQUEST_VALIDATOR = new POSTRequestValidator(SCHEMA_FILE_NAME);

    private final PipeLine<TransferMessage> pipeLine = new PipeLine<>();
    private final NotifyTransaction2<TransferMessage> notifyTransactions;
    private TransferMessage message;

    @Inject
    public TransferRequestProcessor(CoreServiceProvider coreServiceProvider, BusinessServices businessServices) {
        super();

        notifyTransactions = new NotifyTransaction2<>(businessServices.getNotifyService());

        pipeLine.add(new LogInput<>());
        pipeLine.add(new InputRequestValidator<>(POST_REQUEST_VALIDATOR));
        pipeLine.add(new CoreTransactionConfigLoader<>(businessServices.getCoreTransactionDetailsService()));
        pipeLine.add(new InputCoreConverter());
        pipeLine.add(new ServiceConfigLoader<>(businessServices.getServiceConfigServices()));
        pipeLine.add(new NotifySentinel<>(businessServices.getNotifyService(), createMapping()));
        pipeLine.add(new ConsumerCoreService<>(coreServiceProvider));
        pipeLine.add(new VerifyCoreResponse<>(businessServices.getCoreErrorTransactionsService()));
        pipeLine.add(new ResponseConverter());
        pipeLine.add(notifyTransactions);
        pipeLine.add(new LogOutput<>());

        setErrorTransactionHandler(this::notifyErrorTransaction);
    }

    private RequestNotifyMapping<TransferMessage> createMapping() {
        return new RequestNotifyMapping<TransferMessage>()
                .mapCreditInfo(m -> m.getRequest().getBody().getXferInfo())
                .mapMemo(m -> SERVICE_NAME);
    }

    @Override
    public XferAddRS process(Request<XferAddRQ> request, LoggerHandler loggerHandler) {
        message = new TransferMessage(request, loggerHandler);

        pipeLine.process(message);

        return message.getResponse();
    }

    @Override
    public String getSvcIdent() {
        return TransferAppUtil.getSvcIdentFrom(message);
    }

    private void notifyErrorTransaction(XferAddRS responseError) {
        message.setResponse(responseError);
        notifyTransactions.process(message);
    }
}
