package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.exceptions.BadRequestException;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.flow.TransferReverseMessage;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.itf.account.banking.transfer.util.TransferReverseBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class InputRequestValidatorTest {
    private static String schemaFileName = "/schemas/transfer.json";
    private static String schemaFileNameReverse = "/schemas/transfer-reverse.json";

    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);

    /* TEST AIB */
    @Test
    public void process_ConBodyOk_Ok() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("1")
                .withChannelId("svp")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .withTrxCode("6012")
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute
        TransferMessage responseMessage = requestValidator.process(message);

        //Validate
        assertSame(message, responseMessage);
    }

    @Test
    public void process_ConBodySinToAcctId_ThrowException() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute and Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }

    @Test
    public void process_ConBodySinFromAcctId_ThrowException() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute and Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }

    @Test
    public void process_ConBodySinClientTerminalSeqNum_ThrowException() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute and Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }

    @Test
    public void process_ConBodySinPosLocation_ThrowException() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute and Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }

    @Test
    public void process_ConBodySinCurAmt_ThrowException() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute and Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }

    @Test
    public void process_ConBodyOk_SinTrxCode_ThrowBRE() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("svp")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("564.06"))
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute and Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }


    /* TEST REVERSO AIB*/

    @Test
    public void process_Rev_ConDefTrxCodeEnBody_Ok() {
        //Config
        Request<XferRevRQ> request = TestingAppUtil.createBaseRequest();

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("1")
                .withChannelId("svp")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withRevRqUID("435345")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .withTrxCode("6012")
                .build();

        request.setBody(body);

        TransferReverseMessage message = new TransferReverseMessage(request, loggerHandler);
        InputRequestValidator<TransferReverseMessage> requestValidator = new InputRequestValidator<>(schemaFileNameReverse);

        //Execute
        TransferReverseMessage responseMessage = requestValidator.process(message);

        //Validate
        assertSame(message, responseMessage);

    }

    /* TEST CNB*/
    @Test
    public void process_CNB_ConBodyOk_Ok() {
        //Config

        Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .withTrxCode("6012")
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute
        TransferMessage responseMessage = requestValidator.process(message);

        //Validate
        assertSame(message, responseMessage);
    }

    @Test
    public void process_CNB_ConBodyOk_SIN_ClientTerminalSeqNum_ThrowBRE() {
        //Config

        Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .withTrxCode("6012")
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute & Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }

    @Test
    public void process_CNB_ConBodyOk_SIN_PosLocation_ThrowBRE() {
        //Config
        Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withCurAmt(new BigDecimal("10.06"))
                .withUserInterface("HF1P3SVP")
                .withClientTerminalSeqNum("81402")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .withTrxCode("6012")
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputRequestValidator<TransferMessage> requestValidator = new InputRequestValidator<>(schemaFileName);

        //Execute & Validate
        assertThrows(BadRequestException.class, () -> requestValidator.process(message));
    }

    /* TEST REVERSO CNB*/
}
