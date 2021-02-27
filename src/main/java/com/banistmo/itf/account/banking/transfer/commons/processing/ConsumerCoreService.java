package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.commons.bso.resources.CommonBodyRequest;
import com.banistmo.itf.account.banking.transfer.flow.GenericMessage;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.services.core.CoreService;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;

public class ConsumerCoreService<T extends GenericMessage<? extends CommonBodyRequest, ?>>
        implements Pipe<T> {

    private CoreServiceProvider coreServiceProvider;

    public ConsumerCoreService(CoreServiceProvider coreServiceProvider) {
        this.coreServiceProvider = coreServiceProvider;
    }

    @Override
    public T process(T pipeMessage) {
        String channelId = pipeMessage.getServiceConfig().getChannelId();
        CoreService coreService = coreServiceProvider.getCoreService(channelId);

        InputCore inputCore = pipeMessage.getInputCore();
        OutputCore outputCore = coreService.applyTransfer(inputCore);

        pipeMessage.setOutputCore(outputCore);

        return pipeMessage;
    }
}
