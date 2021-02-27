package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.ServiceConfigServices;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceConfigLoaderTest {

    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);
    private ServiceConfigServices service = new BusinessServicesMock().getServiceConfigServices();

    @Test
    void process_ConDefaultServiceConfig_Ok() {
        //Config
        XferAddRQ bodyRequest = TransferBodyRequestBuilder.create()
                .withChannelId("anyChannel")
                .build();

        request.setBody(bodyRequest);

        TransferMessage message = new TransferMessage(request, loggerHandler);

        ServiceConfigLoader<TransferMessage> serviceConfigLoader = new ServiceConfigLoader<>(service);

        //Execute
        TransferMessage messageResponse = serviceConfigLoader.process(message);

        //Validate
        assertSame(message, messageResponse);
        assertEquals("anyChannel", messageResponse.getServiceConfig().getChannelId());
        assertEquals("NOTIFY_TRANSACTIONS_TOPIC_ARN", messageResponse.getServiceConfig().getEnvNameNotifyTrx());
        assertFalse(messageResponse.getServiceConfig().isNotifyCompliance());
    }

    @Test
    void process_ConChannelCnb_Ok() {
        //Config
        XferAddRQ bodyRequest = TransferBodyRequestBuilder.create()
                .withChannelId("cnb")
                .build();

        request.setBody(bodyRequest);

        TransferMessage message = new TransferMessage(request, loggerHandler);

        ServiceConfigLoader<TransferMessage> serviceConfigLoader = new ServiceConfigLoader<>(service);

        //Execute
        TransferMessage messageResponse = serviceConfigLoader.process(message);

        //Validate
        assertSame(message, messageResponse);
        assertEquals("cnb", messageResponse.getServiceConfig().getChannelId());
        assertEquals("NOTIFY_TRANSACTIONS_TOPIC_ARN_CNB", messageResponse.getServiceConfig().getEnvNameNotifyTrx());
        assertTrue(messageResponse.getServiceConfig().isNotifyCompliance());
    }

}
