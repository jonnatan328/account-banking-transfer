package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ResponseConverterTest {

    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);

    @Test
    @DisplayName("process con message valido, retorno la misma instancia")
    void process_ConMessageValido_Ok() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withCurAmt(new BigDecimal("10.06"))
                .withRqUID("1234")
                .build();

        request.setBody(body);

        ServiceConfig sc = new ServiceConfig();
        sc.setChannelId("svp");

        InputCore inputCore = new InputCore();
        inputCore.setTxn("TR01");

        OutputCore outputCore = new OutputCore();

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setOutputCore(outputCore);
        message.setInputCore(inputCore);
        message.setServiceConfig(sc);

        ResponseConverter ccs = new ResponseConverter();

        //Execute
        TransferMessage response = ccs.process(message);

        //Validate
        assertSame(message, response);
        assertEquals("1234", response.getResponse().getRqUID());
        assertEquals(200, response.getResponse().getStatus().getCode());
        assertEquals("TR01", response.getResponse().getSvcIdent());
        assertNotNull(response.getResponse().getXferRec().getXferId());
    }

    @Test
    @DisplayName("process con message valido, retorno la misma instancia CNB")
    void process_ConMessageValido_Ok_CNB() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withChannelId("cnb")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withRqUID("1234")
                .build();

        request.setBody(body);


        ServiceConfig sc = new ServiceConfig();
        sc.setChannelId("cnb");

        InputCore inputCore = new InputCore();
        inputCore.setTxn("0041A");

        OutputCore outputCore = new OutputCore();

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setOutputCore(outputCore);
        message.setInputCore(inputCore);
        message.setServiceConfig(sc);

        ResponseConverter ccs = new ResponseConverter();

        //Execute
        TransferMessage response = ccs.process(message);

        //Validate
        assertSame(message, response);
        assertEquals("1234", response.getResponse().getRqUID());
        assertEquals(200, response.getResponse().getStatus().getCode());
        assertEquals("0041A", response.getResponse().getSvcIdent());
    }
}
