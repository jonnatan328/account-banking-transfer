package com.banistmo.itf.account.banking.transfer.services.core;


import com.banistmo.commons.bso.exceptions.BadGatewayException;
import com.banistmo.commons.bso.exceptions.GatewayTimeoutException;
import com.banistmo.cookie.faster.core.context.CookieApplication;
import com.banistmo.cookie.faster.core.context.CookieApplicationContext;
import com.banistmo.cookie.faster.core.exceptions.ConsumerException;
import com.banistmo.cookie.faster.core.exceptions.TimeoutConsumerException;
import com.banistmo.itf.account.banking.transfer.services.cl.AIBService;
import com.banistmo.itf.account.banking.transfer.services.cl.AIBService.AIBInput;
import com.banistmo.itf.account.banking.transfer.services.cl.AIBService.AIBOutput;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({CookieApplication.class, TransferCoreService.class})
public class AIBTransferCoreServiceTest {

    private AIBInput inputOk;
    private AIBInput inputTimeOut;
    private AIBInput inputBadGatewayOut;

    @Before
    public void setUpStaticMethods() {
        CookieApplicationContext context = PowerMockito.mock(CookieApplicationContext.class);
        AIBService aibService = PowerMockito.mock(AIBService.class);

        inputOk = new AIBInput();
        inputOk.setWsEsimpTxn("0902");
        inputOk.setWsEsimpCuenta1("1000012");
        inputOk.setWsEsimpMonto(new BigDecimal("10"));

        inputTimeOut = new AIBInput();
        inputTimeOut.setWsEsimpTxn("00");
        inputTimeOut.setWsEsimpCuenta1("1000070");
        inputTimeOut.setWsEsimpMonto(new BigDecimal("1"));

        inputBadGatewayOut = new AIBInput();
        inputBadGatewayOut.setWsEsimpTxn("556");
        inputBadGatewayOut.setWsEsimpCuenta1("1000087");
        inputBadGatewayOut.setWsEsimpMonto(new BigDecimal("5"));

        doAnswer((Answer<AIBOutput>) invocation -> {
            AIBInput input = invocation.getArgument(0);

            if (requestEquals(inputOk, input)) {
                var output = new AIBOutput();
                output.setWsSsimpCod("00");
                output.setWsSsimpDesc("todo ok");
                output.setWsSsimpAibresp(0);
                output.setWsSsimpAibreas(0);
                output.setWsSsimpErrGen(null);

                return output;
            } else if (requestEquals(inputTimeOut, input)) {
                throw new TimeoutConsumerException("Time out calling raptor. ", new Exception("Time out"));
            } else if (requestEquals(inputBadGatewayOut, input)) {
                throw new ConsumerException("Time out calling raptor. ", new Exception("Time out"));
            }

            return null;
        }).when(aibService).applyTransfer(any(AIBInput.class));

        when(context.getService(AIBService.class)).thenReturn(aibService);

        //Config call static method start().

        PowerMockito.mockStatic(CookieApplication.class);
        PowerMockito.when(CookieApplication.start()).thenReturn(context);
    }

    private static boolean requestEquals(AIBInput input1, AIBInput input2) {

        return Objects.equals(input1.getWsEsimpTxn(), input2.getWsEsimpTxn())
                && Objects.equals(input1.getWsEsimpCuenta1(), input2.getWsEsimpCuenta1())
                && Objects.equals(input1.getWsEsimpMonto(), input2.getWsEsimpMonto());
    }

    @Test
    public void apply_ResponseOk() {
        //Config
        var inputCore = new InputCore();
        inputCore.setTxn("0902");
        inputCore.setCuenta1("1000012");
        inputCore.setMonto(new BigDecimal("10"));

        TransferCoreService coreService = new AIBTransferCoreService();

        //Execute
        OutputCore outputCore = coreService.applyTransfer(inputCore);

        //validate
        assertNotNull(outputCore);
        assertEquals("00", outputCore.getCoreCode());
        assertEquals("todo ok", outputCore.getCoreDescription());
        assertEquals(0, outputCore.getAibResp().intValue());
        assertEquals(0, outputCore.getAibReas().intValue());
        assertNull(outputCore.getAibErrGen());
    }

    @Test(expected = GatewayTimeoutException.class)
    public void apply_CatchTimeoutConsumerException_ThrowGTE() {
        //Config
        var inputCore = new InputCore();
        inputCore.setTxn("00");
        inputCore.setCuenta1("1000070");
        inputCore.setMonto(new BigDecimal("1"));

        TransferCoreService coreService = new AIBTransferCoreService();

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

        TransferCoreService coreService = new AIBTransferCoreService();

        //Execute
        coreService.applyTransfer(inputCore);
    }
}
