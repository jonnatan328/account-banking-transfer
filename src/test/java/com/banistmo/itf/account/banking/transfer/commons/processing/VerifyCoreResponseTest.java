package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.exceptions.BadGatewayException;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.ServiceConfig;
import com.banistmo.commons.bso.services.db.CoreErrorTransactionsService;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VerifyCoreResponseTest {

    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);
    private CoreErrorTransactionsService service = new BusinessServicesMock().getCoreErrorTransactionsService();
    
    @Test
    @DisplayName("process codigo respuesta core ok, retorna el mensaje")
    public void process_ConCoreCodeOk_Ok() {
        //Config
        OutputCore outputCore = new OutputCore();
        outputCore.setCoreCode(VerifyCoreResponse.SUCCESS_CODE_RESPONSE);


        ServiceConfig cf = new ServiceConfig();
        cf.setChannelId("svp");

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setOutputCore(outputCore);
        message.setServiceConfig(cf);

        VerifyCoreResponse<TransferMessage> verifyCoreResponse = new VerifyCoreResponse<>(service);

        //Execute
        TransferMessage responseMessage = verifyCoreResponse.process(message);

        //Validate
        assertSame(message, responseMessage);
    }

    @Test
    @DisplayName("process, codigo respuesta CNB core ok, retorna el mensaje")
    void process_ConCoreCodeCNBOk_Ok() {
        //Config
        OutputCore outputCore = new OutputCore();
        outputCore.setCoreCode(VerifyCoreResponse.SUCCESS_CODE_RESPONSE_ODPA);


        ServiceConfig cf = new ServiceConfig();
        cf.setChannelId("cnb");

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setOutputCore(outputCore);
        message.setServiceConfig(cf);

        VerifyCoreResponse<TransferMessage> verifyCoreResponse = new VerifyCoreResponse<>(service);

        //Execute
        TransferMessage responseMessage = verifyCoreResponse.process(message);

        //Validate
        assertSame(message, responseMessage);
    }

    @Test
    @DisplayName("process codigo respuesta core 99, lanza Exception")
    public void process_ConCoreCodeNueveNueve_ThrowException() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withChannelId("svp")
                .build();

        request.setBody(body);

        OutputCore outputCore = new OutputCore();
        outputCore.setCoreCode(VerifyCoreResponse.GENERIC_ERROR.toString());
        outputCore.setAibReas(16);

        ServiceConfig cf = new ServiceConfig();
        cf.setChannelId("svp");

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setOutputCore(outputCore);
        message.setServiceConfig(cf);

        VerifyCoreResponse<TransferMessage> verifyCoreResponse = new VerifyCoreResponse<>(service);

        //Execute and Validate
        assertThrows(BadGatewayException.class, () -> verifyCoreResponse.process(message));


    }

    @Test
    @DisplayName("process codigo respuesta core no valido, lanza Exception")
    public void process_ConCoreCodeNoValido_ThrowException() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withChannelId("svp")
                .build();

        request.setBody(body);

        OutputCore outputCore = new OutputCore();
        outputCore.setCoreCode("99");
        outputCore.setAibReas(VerifyCoreResponse.GENERIC_ERROR);
        outputCore.setAibErrGen(5820);

        ServiceConfig cf = new ServiceConfig();
        cf.setChannelId("svp");
        
        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setOutputCore(outputCore);
        message.setServiceConfig(cf);

        VerifyCoreResponse<TransferMessage> verifyCoreResponse = new VerifyCoreResponse<>(service);

        //Execute and Validate
        assertThrows(BadGatewayException.class, () -> verifyCoreResponse.process(message));
    }

    @Test
    @DisplayName("process, codigo respuesta CNB core 9, lanza Exception")
    void process_ConCoreCodeCNBNoValido_Ok() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withChannelId("cnb")
                .build();

        request.setBody(body);

        InputCore inputCore = new InputCore();
        inputCore.setTxn("0041A");

        OutputCore outputCore = new OutputCore();
        outputCore.setCoreCode("99");
        outputCore.setAibReas(VerifyCoreResponse.GENERIC_ERROR);
        outputCore.setAibErrGen(5820);

        ServiceConfig cf = new ServiceConfig();
        cf.setChannelId("cnb");

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setInputCore(inputCore);
        message.setOutputCore(outputCore);
        message.setServiceConfig(cf);

        VerifyCoreResponse<TransferMessage> verifyCoreResponse = new VerifyCoreResponse<>(service);

        //Execute and Validate
        assertThrows(BadGatewayException.class, () -> verifyCoreResponse.process(message));
    }
}
