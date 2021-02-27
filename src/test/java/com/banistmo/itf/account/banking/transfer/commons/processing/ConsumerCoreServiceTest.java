package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.services.core.CoreService;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ConsumerCoreServiceTest {
    private static OutputCore outputCore = new OutputCore();
    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);

    private CoreServiceProvider coreServiceProvider;

    @BeforeEach
    void init() {
        outputCore.setCoreCode("00");
        outputCore.setCoreDescription("una descripcion");

        coreServiceProvider = Mockito.mock(CoreServiceProvider.class);
        CoreService coreService = PowerMockito.mock(CoreService.class);

        when(coreService.applyTransfer(any())).thenReturn(outputCore);
        when(coreServiceProvider.getCoreService(any())).thenReturn(coreService);
    }

    @Test
    @DisplayName("process con message valido, retorno la misma instancia")
    void process_ConMessageValido_Ok() {
        //Config
        ServiceConfig sc = new ServiceConfig();
        sc.setChannelId("svp");

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setServiceConfig(sc);

        ConsumerCoreService<TransferMessage> ccs = new ConsumerCoreService<>(coreServiceProvider);

        //Execute
        TransferMessage responseMessage = ccs.process(message);

        //Validate
        assertSame(message, responseMessage);
        assertNotNull(responseMessage.getOutputCore());
    }
}
