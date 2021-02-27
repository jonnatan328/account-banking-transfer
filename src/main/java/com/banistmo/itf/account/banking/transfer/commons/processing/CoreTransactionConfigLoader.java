package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.exceptions.ApplicationException;
import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.commons.bso.pipe.PipeMessage;
import com.banistmo.commons.bso.resources.CommonBodyRequest;
import com.banistmo.commons.bso.resources.MsgRqHdr;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.db.CoreTransactionDetailsService;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;

public class CoreTransactionConfigLoader<T extends PipeMessage<? extends Request<? extends CommonBodyRequest>, ?, ?, ?>>
        implements Pipe<T> {

    private CoreTransactionDetailsService service;

    public CoreTransactionConfigLoader(CoreTransactionDetailsService service) {
        this.service = service;
    }

    @Override
    public T process(T message) {
        MsgRqHdr msgRqHdr = message.getRequest().getBody().getMsgRqHdr();
        String serviceType = msgRqHdr.getContextRqHdr().getServiceType();

        if (serviceType != null) {
            String channelId = msgRqHdr.getContextRqHdr().getChannelId();

            CoreTransaction coreTransaction = service.get(channelId, serviceType);

            if (coreTransaction == null) {
                String errorMessage = "CoreTransaction configuration not found, serviceType: " + serviceType
                        + ", channelId: " + channelId;
                throw new ApplicationException(errorMessage);
            }

            if (coreTransaction.getTransactionConfig().isEmpty()) {
                String errorMessage = "TransactionConfig not defined, serviceType: " + serviceType
                        + ", channelId: " + channelId;
                throw new ApplicationException(errorMessage);
            }

            message.setCoreTransaction(coreTransaction);
        }

        return message;
    }
}
