package com.banistmo.itf.account.banking.transfer.lambda;


import com.banistmo.commons.bso.exceptions.LambdaApplicationException;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;
import com.banistmo.itf.account.banking.transfer.ImplementContext;
import com.banistmo.itf.account.banking.transfer.commons.processing.VerifyCoreResponse;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferAddRS;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.services.core.CoreService;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TransferModule.class})
public class TransferHandlerTest {

    private static OutputCore outputCore = new OutputCore();
    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();

    @Before
    public void setUp() {
        //Mock
        var coreServiceProvider = PowerMockito.mock(CoreServiceProvider.class);
        CoreService coreServiceDefault = PowerMockito.mock(CoreService.class);
        CoreService coreServiceCnb = PowerMockito.mock(CoreService.class);

        when(coreServiceCnb.applyTransfer(any())).thenReturn(outputCore);
        when(coreServiceDefault.applyTransfer(any())).thenReturn(outputCore);

        doAnswer((Answer<CoreService>) invocation -> {
            String channelId = invocation.getArgument(0);

            if ("cnb".equals(channelId)) {
                return coreServiceCnb;
            }

            return coreServiceDefault;

        }).when(coreServiceProvider).getCoreService(any(String.class));

        //Config provideCoreService method.
        var provideCoreServiceMethod = PowerMockito.method(TransferModule.class, "provideCoreServiceProvider");
        var provideBusinessServiceMethod = PowerMockito.method(TransferModule.class, "provideBusinessServices");

        PowerMockito.stub(provideCoreServiceMethod).toReturn(coreServiceProvider);
        PowerMockito.stub(provideBusinessServiceMethod).toReturn(new BusinessServicesMock());
    }

    @Test
    @DisplayName("handleRequest con request null, responde null")
    public void handleRequest_ConRequestNull_Null() {
        //Config
        TransferHandler handler = new TransferHandler();
        //Execute
        XferAddRS response = handler.handleRequest(null, new ImplementContext());

        //Validate
        assertNull(response);
    }

    @Test
    @DisplayName("handleRequest con request awakeFunction, responde null")
    public void handleRequest_ConRequestAwakeFunction_Null() {
        //Config
        TransferHandler handler = new TransferHandler();

        request.setAwakenFunction(true);

        //Execute
        XferAddRS response = handler.handleRequest(request, new ImplementContext());

        //Validate
        assertNull(response);
    }

    @Test
    @DisplayName("handleRequest responde no es nulo.")
    public void handleRequest_RespondeNotNull_Ok() {
        //Config

        outputCore.setCoreCode(VerifyCoreResponse.SUCCESS_CODE_RESPONSE);

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

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withChannelId("svp")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withCurAmt(new BigDecimal("12.25"))
                .withPosLocation("814")
                .withTrxCode("6012")
                .withRqUID("1234532342543")
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .withServiceType("TR01")
                .build();

        request.setBody(body);

        TransferHandler handler = new TransferHandler();


        //Execute
        XferAddRS response = handler.handleRequest(request, new ImplementContext());

        //Validate
        assertNotNull(response);
    }



    @Test
    @DisplayName("handleRequest request con body vacio, catch BadRequestException, Lanza LambdaApplicationException.")
    public void handleRequest_ConRequestNoValido_catchBRE_ThrowException() {
        //Config
        request.setBody(new XferAddRQ());

        TransferHandler handler = new TransferHandler();

        //Execute
        Throwable throwable = assertThrows(LambdaApplicationException.class, () -> handler.handleRequest(request, new ImplementContext()));

        //Validate
        assertTrue(throwable.getMessage().matches(".*code\":400.*"));
    }

    @Test
    @DisplayName("handleRequest request, con response no valido del core, catch BadGatewayException, Lanza LambdaApplicationException.")
    public void handleRequest_ConRsDelCoreNoValido_catchBGE_ThrowException() {
        //Config
        outputCore.setCoreCode("99");
        outputCore.setAibReas(16);

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR01")
                .withChannelId("svp")
                .withTrxCode("6015")
                .withSourceDefault("F")
                .withUserInterface("HF1P3CNB")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferHandler handler = new TransferHandler();

        //Execute
        Throwable throwable = assertThrows(LambdaApplicationException.class, () -> handler.handleRequest(request, new ImplementContext()));

        //Validate
        assertTrue(throwable.getMessage().matches(".*code\":502.*"));
    }

    @Test
    public void handleRequest_ConChannelId_CNB_ConRsDelCoreNoValido_catchBGE_ThrowException() {
        //Config
        outputCore.setCoreCode("99");

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("25.06"))
                .build();

        request.setBody(body);

        TransferHandler handler = new TransferHandler();

        //Execute
        Throwable throwable = assertThrows(LambdaApplicationException.class, () -> handler.handleRequest(request, new ImplementContext()));

        //Validate
        assertTrue(throwable.getMessage().matches(".*code\":502.*"));
    }

    @Test
    @DisplayName("handleRequest request, con response no valido del core, catch any Exception, Lanza LambdaApplicationException.")
    public void handleRequest_ConRsDelCoreNoValidoCampoNull_catchBGE_ThrowException() {
        //Config
        outputCore.setCoreCode(null);
        outputCore.setAibReas(VerifyCoreResponse.GENERIC_ERROR);

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR01")
                .withChannelId("svp")
                .withTrxCode("6015")
                .withSourceDefault("F")
                .withUserInterface("HF1P3CNB")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
        request.setBody(body);

        TransferHandler handler = new TransferHandler();

        //Execute
        Throwable throwable = assertThrows(LambdaApplicationException.class, () -> handler.handleRequest(request, new ImplementContext()));

        //Validate
        assertTrue(throwable.getMessage().matches(".*code\":500.*"));
    }
}
