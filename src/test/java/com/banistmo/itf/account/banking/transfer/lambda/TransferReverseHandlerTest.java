package com.banistmo.itf.account.banking.transfer.lambda;

import com.banistmo.commons.bso.exceptions.LambdaApplicationException;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;
import com.banistmo.itf.account.banking.transfer.ImplementContext;
import com.banistmo.itf.account.banking.transfer.commons.processing.VerifyCoreResponse;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.dts.rs.XferRevRS;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.services.core.CoreService;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferReverseBodyRequestBuilder;
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
public class TransferReverseHandlerTest {

    private static OutputCore outputCore = new OutputCore();
    private Request<XferRevRQ> request = TestingAppUtil.createBaseRequest();

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
        var handler = new TransferReverseHandler();

        //Execute
        XferRevRS response = handler.handleRequest(null, new ImplementContext());

        //Validate
        assertNull(response);
    }

    @Test
    @DisplayName("handleRequest con request awakeFunction, responde null")
    public void handleRequest_ConRequestAwakeFunction_Null() {
        //Config
        request.setAwakenFunction(true);

        var handler = new TransferReverseHandler();

        //Execute
        XferRevRS response = handler.handleRequest(request, new ImplementContext());

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
        transactionConfigDR.setTrxType("tr");
        transactionConfigDR.setTrx("1001");

        transactionConfigCR.setTrxType("tr");
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

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
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

        var handler = new TransferReverseHandler();

        //Execute
        XferRevRS response = handler.handleRequest(request, new ImplementContext());

        //Validate
        assertNotNull(response);
    }


    @Test
    @DisplayName("handleRequest request con body vacio, catch BadRequestException, Lanza LambdaApplicationException.")
    public void handleRequest_ConRequestNoValido_catchBRE_ThrowException() {
        //Config
        request.setBody(new XferRevRQ());

        TransferReverseHandler handler = new TransferReverseHandler();

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

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR01")
                .withChannelId("svp")
                .withTrxCode("6015")
                .withSourceDefault("F")
                .withUserInterface("HF1P3CNB")
                .withRevRqUID("83292")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferReverseHandler handler = new TransferReverseHandler();

        //Execute
        Throwable throwable = assertThrows(LambdaApplicationException.class, () -> handler.handleRequest(request, new ImplementContext()));

        //Validate
        assertTrue(throwable.getMessage().matches(".*code\":502.*"));
    }

    @Test
    public void handleRequest_ConChannelId_CNB_ConRsDelCoreNoValido_catchBGE_ThrowException() {
        //Config
        outputCore.setCoreCode("99");

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withRevRqUID("0172921")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("25.06"))
                .build();

        request.setBody(body);

        var handler = new TransferReverseHandler();

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

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR01")
                .withChannelId("svp")
                .withTrxCode("6015")
                .withSourceDefault("F")
                .withUserInterface("HF1P3CNB")
                .withRevRqUID("83292")
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        Request<XferRevRQ> request = TestingAppUtil.createBaseRequest();
        request.setBody(body);

        TransferReverseHandler handler = new TransferReverseHandler();

        //Execute
        Throwable throwable = assertThrows(LambdaApplicationException.class, () -> handler.handleRequest(request, new ImplementContext()));

        //Validate
        assertTrue(throwable.getMessage().matches(".*code\":500.*"));
    }
}
