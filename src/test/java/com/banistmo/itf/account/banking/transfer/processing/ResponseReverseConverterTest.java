package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferReverseMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferReverseBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

 class ResponseReverseConverterTest {

    private Request<XferRevRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);

    @Test
    @DisplayName("process con message valido, retorno la misma instancia")
     void process_ConMessageValido_Ok() {
        //Config
        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withCurAmt(new BigDecimal("10.06"))
                .withRqUID("1234")
                .build();

        request.setBody(body);

        InputCore inputCore = new InputCore();
        inputCore.setTxn("TR01");

        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setChannelId("svp");

        TransferReverseMessage message = new TransferReverseMessage(request, loggerHandler);
        message.setInputCore(inputCore);
        message.setServiceConfig(serviceConfig);

        ResponseReverseConverter ccs = new ResponseReverseConverter();

        //Execute
        TransferReverseMessage response = ccs.process(message);

        //Validate
        assertSame(message, response);
        assertEquals("1234", response.getResponse().getRqUID());
        assertEquals(200, response.getResponse().getStatus().getCode());
        assertEquals("TR012", response.getResponse().getSvcIdent());
    }

    @Test
    @DisplayName("process con message valido, retorno la misma instancia CNB")
     void process_ConMessageValido_Ok_CNB() {
        //Config
        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withRqUID("1234")
                .withChannelId("cnb")
                .build();

        request.setBody(body);

        InputCore inputCore = new InputCore();
        inputCore.setTxn("0042A");

        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setChannelId("cnb");

        TransferReverseMessage message = new TransferReverseMessage(request, loggerHandler);
        message.setInputCore(inputCore);
        message.setServiceConfig(serviceConfig);

        ResponseReverseConverter ccs = new ResponseReverseConverter();

        //Execute
        TransferReverseMessage response = ccs.process(message);

        //Validate
        assertSame(message, response);
        assertEquals("1234", response.getResponse().getRqUID());
        assertEquals(200, response.getResponse().getStatus().getCode());
        assertEquals("0042A", response.getResponse().getSvcIdent());
    }
}
