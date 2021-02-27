package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.commons.bso.services.notify.NotifyService;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferAddRS;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NotifyTransaction2Test {
    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);

    private NotifyService notifyService = new BusinessServicesMock().getNotifyService();

    @Test
    void process_SinServiceConfig_returnMessage() {
        //Config
        TransferMessage message = new TransferMessage(request, loggerHandler);
        NotifyTransaction2<TransferMessage> notifyTransaction2 = new NotifyTransaction2<>(notifyService);

        //Execute
        TransferMessage messageResponse = notifyTransaction2.process(message);

        //Validate
        assertSame(message, messageResponse);
    }

    @Test
    void process_ConServiceConfig_returnMessage() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("svp")
                .withTrxCode("4589")
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withFromAcctId("100009092")
                .withToAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        XferAddRS bodyResponse = new XferAddRS();
        bodyResponse.setSvcIdent("1004A");

        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setChannelId("cnb");
        serviceConfig.setNotifyCompliance(false);
        serviceConfig.setEnvNameNotifyTrx("ANY_ENV_VAR");

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setServiceConfig(serviceConfig);
        message.setResponse(bodyResponse);

        NotifyTransaction2<TransferMessage> notifyTransaction2 = new NotifyTransaction2<>(notifyService);

        //Execute
        TransferMessage messageResponse = notifyTransaction2.process(message);

        //Validate
        assertSame(message, messageResponse);
    }
}
