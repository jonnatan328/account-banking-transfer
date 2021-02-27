package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.processing.NotifyTransactions;
import com.banistmo.commons.bso.resources.CommonBodyRequest;
import com.banistmo.commons.bso.resources.Response;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.commons.bso.services.notify.NotifyService;
import com.banistmo.itf.account.banking.transfer.flow.GenericMessage;

public class NotifyTransaction2<T extends GenericMessage<? extends CommonBodyRequest, ? extends Response>> extends NotifyTransactions<T> {

    public NotifyTransaction2(NotifyService notifyService) {
        super(notifyService);
    }

    @Override
    public T process(T message){
        ServiceConfig serviceConfig = message.getServiceConfig();

        if (serviceConfig == null) {
            return message;
        }

        this.topic = System.getenv(serviceConfig.getEnvNameNotifyTrx());

        return super.process(message);
    }
}
