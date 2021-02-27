package com.banistmo.itf.account.banking.transfer.flow;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.BusinessServices;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.commons.processing.VerifyCoreResponse;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferAddRS;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.services.core.CoreService;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.logging.handler.LoggerHandler;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class TransferRequestProcessorTest {
    private static OutputCore outputCore = new OutputCore();
    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
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
    @DisplayName("process request valido, retorna XferAddRS")
    void process_ConRequestValido_Ok_() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR01")
                .withChannelId("svp")
                .withTrxCode("6015")
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withToAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withFromAcctId("105266607")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        outputCore.setCoreCode(VerifyCoreResponse.SUCCESS_CODE_RESPONSE);

        var requestProcessor = new TransferRequestProcessor(coreServiceProvider, businessServices);

        TransactionConfig transactionConfigCR = new TransactionConfig();
        TransactionConfig transactionConfigDR = new TransactionConfig();
        transactionConfigDR.setTrxType("dr");
        transactionConfigDR.setTrx("1001");

        transactionConfigCR.setTrxType("cr");
        transactionConfigCR.setTrx("1003");

        List<TransactionConfig> transactionConfigList = new ArrayList<>();
        transactionConfigList.add(transactionConfigCR);
        transactionConfigList.add(transactionConfigDR);

        CoreTransaction coreTransaction = new CoreTransaction();
        coreTransaction.setServiceType("TX1");
        coreTransaction.setChannelId("SVP");
        coreTransaction.setCashierOperation("81402");
        coreTransaction.setCashOperation("88888");
        coreTransaction.setTransactionConfig(transactionConfigList);

        //Execute
        XferAddRS rs = requestProcessor.process(request, loggerHandler);

        //Validate
        assertNotNull(rs);
    }
}
