package com.banistmo.itf.account.banking.transfer.services.core;


import com.banistmo.commons.bso.exceptions.BadGatewayException;
import com.banistmo.commons.bso.exceptions.GatewayTimeoutException;
import com.banistmo.cookie.faster.core.context.CookieApplication;
import com.banistmo.cookie.faster.core.context.CookieApplicationContext;
import com.banistmo.cookie.faster.core.exceptions.ConsumerException;
import com.banistmo.cookie.faster.core.exceptions.TimeoutConsumerException;
import com.banistmo.itf.account.banking.transfer.services.cl.LocalTransferenceService;
import com.banistmo.itf.account.banking.transfer.services.cl.LocalTransferenceService.LocalTransferenceInput;
import com.banistmo.itf.account.banking.transfer.services.cl.LocalTransferenceService.LocalTransferenceOutput;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({CookieApplication.class, TransferCoreService.class})
public class CNBTransferCoreServiceTest {

    private LocalTransferenceInput inputOk;
    private LocalTransferenceInput inputTimeOut;
    private LocalTransferenceInput inputBadGatewayOut;

    @Before
    public void setUpStaticMethods() {
        CookieApplicationContext context = PowerMockito.mock(CookieApplicationContext.class);
        LocalTransferenceService cnbService = PowerMockito.mock(LocalTransferenceService.class);

        inputOk = new LocalTransferenceInput();
        inputOk.setWsLnkBusCnbTxn("0902");
        inputOk.setWsLnkBusCnbCuenta("1000012");
        inputOk.setWsLnkBusCnbMonto("1000");

        inputTimeOut = new LocalTransferenceInput();
        inputTimeOut.setWsLnkBusCnbTxn("00");
        inputTimeOut.setWsLnkBusCnbCuenta("1000070");
        inputTimeOut.setWsLnkBusCnbMonto("100");

        inputBadGatewayOut = new LocalTransferenceInput();
        inputBadGatewayOut.setWsLnkBusCnbTxn("556");
        inputBadGatewayOut.setWsLnkBusCnbCuenta("1000087");
        inputBadGatewayOut.setWsLnkBusCnbMonto("500");

        doAnswer((Answer<LocalTransferenceOutput>) invocation -> {
            LocalTransferenceInput input = invocation.getArgument(0);

            if (requestEquals(inputOk, input)) {
                var output = new LocalTransferenceOutput();
                output.setWsResultadoHogan("0");
                output.setWsConsecutivoTx("6577");
                output.setWsRespMensaje("todo ok");

                return output;
            } else if (requestEquals(inputTimeOut, input)) {
                throw new TimeoutConsumerException("Time out calling raptor. ", new Exception("Time out"));
            } else if (requestEquals(inputBadGatewayOut, input)) {
                throw new ConsumerException("Time out calling raptor. ", new Exception("Time out"));
            }

            return null;
        }).when(cnbService).applyTransfer(any(LocalTransferenceInput.class));

        when(context.getService(LocalTransferenceService.class)).thenReturn(cnbService);

        //Config call static method start().

        PowerMockito.mockStatic(CookieApplication.class);
        PowerMockito.when(CookieApplication.start()).thenReturn(context);
    }

    private static boolean requestEquals(LocalTransferenceInput input1, LocalTransferenceInput input2) {

        return Objects.equals(input1.getWsLnkBusCnbTxn(), input2.getWsLnkBusCnbTxn())
                && Objects.equals(input1.getWsLnkBusCnbCuenta(), input2.getWsLnkBusCnbCuenta())
                && Objects.equals(input1.getWsLnkBusCnbMonto(), input2.getWsLnkBusCnbMonto());
    }

    @Test
    public void apply_ResponseOk() {
        //Config
        var inputCore = new InputCore();
        inputCore.setTxn("0902");
        inputCore.setCuenta1("1000012");
        inputCore.setMonto(new BigDecimal("10"));

        TransferCoreService coreService = new CNBTransferCoreService();

        //Execute
        OutputCore outputCore = coreService.applyTransfer(inputCore);

        //validate
        assertNotNull(outputCore);
        assertEquals("0", outputCore.getCoreCode());
        assertEquals("todo ok", outputCore.getCoreDescription());
        assertEquals("6577", outputCore.getConsecutivoTrx());
    }

    @Test(expected = GatewayTimeoutException.class)
    public void apply_CatchTimeoutConsumerException_ThrowGTE() {
        //Config
        var inputCore = new InputCore();
        inputCore.setTxn("00");
        inputCore.setCuenta1("1000070");
        inputCore.setMonto(new BigDecimal("1"));

        TransferCoreService coreService = new CNBTransferCoreService();

        //Execute
        coreService.applyTransfer(inputCore);
    }

    @Test(expected = BadGatewayException.class)
    public void apply_CatchConsumerException_ThrowBGE() {
        //Config
        var inputCore = new InputCore();
        inputCore.setTxn("556");
        inputCore.setCuenta1("1000087");
        inputCore.setMonto(new BigDecimal("5"));

        TransferCoreService coreService = new CNBTransferCoreService();

        //Execute
        coreService.applyTransfer(inputCore);
    }
}
