package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.commons.bso.resources.CommonBodyRequest;
import com.banistmo.commons.bso.resources.MsgRqHdr;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.commons.bso.services.ServiceConfigServices;
import com.banistmo.itf.account.banking.transfer.flow.GenericMessage;

public class ServiceConfigLoader<T extends GenericMessage<? extends CommonBodyRequest, ?>>
        implements Pipe<T> {

    private ServiceConfigServices services;

    public ServiceConfigLoader(ServiceConfigServices services) {
        this.services = services;
    }

    @Override
    public T process(T message) {
        MsgRqHdr msgRqHdr = message.getRequest().getBody().getMsgRqHdr();
        String channelId = msgRqHdr.getContextRqHdr().getChannelId();

        ServiceConfig serviceConfig = services.get(channelId);

        if (serviceConfig == null) {
            serviceConfig = createDefaultServiceConfig(channelId);
        }

        message.setServiceConfig(serviceConfig);
        return message;
    }

    private ServiceConfig createDefaultServiceConfig(String channelId) {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setChannelId(channelId);
        serviceConfig.setNotifyCompliance(false);
        serviceConfig.setEnvNameNotifyTrx("NOTIFY_TRANSACTIONS_TOPIC_ARN");

        return serviceConfig;
    }
}
