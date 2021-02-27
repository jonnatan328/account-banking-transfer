package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferRevRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferReverseMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferReverseBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class InputCoreReverseConverterTest {

    private Request<XferRevRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);

    @Test
    void process_InputCoreOk_True() {
        //Config

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("102526")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withChannelId("svp")
                .build();

        request.setBody(body);

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
        coreTransaction.setServiceType("TR01");
        coreTransaction.setChannelId("svp");
        coreTransaction.setCashierOperation("888888");
        coreTransaction.setCashOperation("88888");
        coreTransaction.setTransactionConfig(transactionConfigList);

        TransferReverseMessage message = new TransferReverseMessage(request, loggerHandler);
        InputCoreReverseConverter inputCoreConverter = new InputCoreReverseConverter();

        message.setCoreTransaction(coreTransaction);

        //Execute
        TransferReverseMessage responseMessage = inputCoreConverter.process(message);
        InputCore inputCore = responseMessage.getInputCore();

        //Validation
        assertSame(message, responseMessage);
        assertEquals("TR01", inputCore.getTxn());
        assertEquals("SVP", inputCore.getCanal());
        assertEquals("0105266606", inputCore.getCuenta1());
        assertEquals("0105266607", inputCore.getCuenta2());
        assertEquals("888888", inputCore.getCajero());
        assertEquals("88888", inputCore.getSucursal());
        assertEquals("1001", inputCore.getTran01());
        assertEquals("1003", inputCore.getTran02());
        assertEquals(1, inputCore.getReverso().intValue());
        assertEquals(new BigDecimal("10.06"), inputCore.getMonto());
    }

    @Test
    void process_ConChannelId_CNB_Ok() {
        //Config

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withToAcctId("105266606")
                .withFromAcctId("105266607")
                .withRevRqUID("99883")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferReverseMessage message = new TransferReverseMessage(request, loggerHandler);
        InputCoreReverseConverter inputCoreConverter = new InputCoreReverseConverter();

        //Execute
        TransferReverseMessage responseMessage = inputCoreConverter.process(message);
        InputCore inputCore = responseMessage.getInputCore();

        //Validation
        assertSame(message, responseMessage);
        assertEquals("0042A", inputCore.getTxn());
        assertEquals("CNB", inputCore.getCanal());
        assertNull(inputCore.getUsuario());
        assertNull(inputCore.getFuente());
        assertEquals(new BigDecimal("10.06"), inputCore.getMonto());
        assertEquals("081402", inputCore.getCajero());
        assertEquals("00814", inputCore.getSucursal());
        assertEquals("0105266607", inputCore.getCuenta1());
        assertEquals("0105266606", inputCore.getCuenta2());
        assertEquals("99883", inputCore.getConsecutivo());
        assertNull(inputCore.getDescripcion());
    }

    @Test
    void process_ConLabelCanal_Ok() {
        //Config

        XferRevRQ body = TransferReverseBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("svp")
                .withServiceType("TR01")
                .withToAcctId("105266606")
                .withFromAcctId("105266607")
                .withCurAmt(new BigDecimal("10.06"))
                .withTransactionLabel("Label canal")
                .withRevRqUID("190029")
                .build();

        request.setBody(body);

        TransferReverseMessage message = new TransferReverseMessage(request, loggerHandler);
        InputCoreReverseConverter inputCoreReverseConverter = new InputCoreReverseConverter();

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
        coreTransaction.setServiceType("TR01");
        coreTransaction.setChannelId("svp");
        coreTransaction.setCashierOperation("888888");
        coreTransaction.setCashOperation("88888");
        coreTransaction.setTransactionConfig(transactionConfigList);

        message.setCoreTransaction(coreTransaction);

        //Execute
        TransferReverseMessage responseMessage = inputCoreReverseConverter.process(message);
        InputCore inputCore = responseMessage.getInputCore();

        //Validation
        assertSame(message, responseMessage);
        assertEquals("1234", inputCore.getClientId());
        assertEquals("AIBT", inputCore.getIndicador());
        assertEquals("TR01", inputCore.getTxn());
        assertEquals("SVP", inputCore.getCanal());
        assertNull(inputCore.getUsuario());
        assertNull(inputCore.getFuente());
        assertEquals(new BigDecimal("10.06"), inputCore.getMonto());
        assertEquals("888888", inputCore.getCajero());
        assertEquals("88888", inputCore.getSucursal());
        assertEquals("0105266607", inputCore.getCuenta1());
        assertEquals("0105266606", inputCore.getCuenta2());
        assertEquals(1, inputCore.getReverso().intValue());
        assertEquals("190029", inputCore.getConsecutivo());
    }
}
