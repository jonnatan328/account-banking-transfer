package com.banistmo.itf.account.banking.transfer.flow;

import com.banistmo.commons.bso.pipe.PipeMessage;
import com.banistmo.commons.bso.resources.CommonBodyRequest;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.logging.handler.LoggerHandler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericMessage <B extends CommonBodyRequest, R>  extends PipeMessage<Request<B>, InputCore, OutputCore, R> {
    private ServiceConfig serviceConfig;

    public GenericMessage(Request<B> request, LoggerHandler loggerHandler) {
        super(request, loggerHandler);
    }
}
