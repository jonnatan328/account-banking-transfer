package com.banistmo.itf.account.banking.transfer.flow;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.BusinessServices;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.commons.processing.VerifyCoreResponse;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferRevRS;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.services.core.CoreService;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferReverseBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class TransferReverseRequestProcessorTest {
    private static OutputCore outputCore = new OutputCore();
    private Request<XferRevRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);

    private CoreServiceProvider coreServiceProvider;
    private BusinessServices businessServices = new BusinessServicesMock();

    @BeforeEach
    void setUpMethods() {
        //Mock
        coreServiceProvider = Mockito.mock(CoreServiceProvider.class);
        CoreService coreServiceDefault = Mockito.mock(CoreService.class);
        CoreService coreServiceCnb = Mockito.mock(CoreService.class);

        when(coreServiceCnb.applyTransfer(any())).thenReturn(outputCore);
        when(coreServiceDefault.applyTransfer(any())).thenReturn(outputCore);

        doAnswer((Answer<CoreService>) invocation -> {
            String channelId = invocation.getArgument(0);

            if ("cnb".equals(channelId)) {
                return coreServiceCnb;
            }

            return coreServiceDefault;

        }).when(coreServiceProvider).getCoreService(any(String.class));
    }

    @Test
    @DisplayName("process request valido, retorna transferencia")
    void process_ConRequestValido_Ok() {
        //Config
        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR01")
                .withChannelId("svp")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        outputCore.setCoreCode(VerifyCoreResponse.SUCCESS_CODE_RESPONSE);

        var requestProcessor = new TransferReverseRequestProcessor(coreServiceProvider, businessServices);

        //Execute
        XferRevRS rs = requestProcessor.process(request, loggerHandler);

        //Validate
        assertNotNull(rs);
    }
}
