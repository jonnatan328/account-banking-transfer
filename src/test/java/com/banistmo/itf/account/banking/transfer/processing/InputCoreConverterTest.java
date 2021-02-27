package com.banistmo.itf.account.banking.transfer.processing;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.services.core.InputCore;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class InputCoreConverterTest {

     private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
     private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);
     
    @Test
     void process_InputCoreOk_True() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withFromAcctId("105266606")
                .withToAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.12"))
                .withRqUID("1234532342543")
                .withUserInterface("HF1P3SVP")
                .withSourceDefault("F")
                .withTransactionLabel("Label canal")
                .withTrxCode("6012")
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

        TransferMessage message = new TransferMessage(request, loggerHandler);
        message.setCoreTransaction(coreTransaction);

        InputCoreConverter inputCoreConverter = new InputCoreConverter();

        //Execute
        TransferMessage responseMessage = inputCoreConverter.process(message);
        InputCore inputCore = responseMessage.getInputCore();

        //Validation
        assertSame(message, responseMessage);
        assertEquals("1234532342543", inputCore.getClientId());
        assertEquals("TR01", inputCore.getTxn());
        assertEquals("SVP", inputCore.getCanal());
        assertEquals("0105266606", inputCore.getCuenta1());
        assertEquals("0105266607", inputCore.getCuenta2());
        assertEquals("888888", inputCore.getCajero());
        assertEquals("88888", inputCore.getSucursal());
        assertEquals("1001", inputCore.getTran01());
        assertEquals("1003", inputCore.getTran02());
        assertEquals(new BigDecimal("10.12"), inputCore.getMonto());
    }

    @Test
    void process_ConChannelId_CNB_Ok() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withToAcctId("105266606")
                .withFromAcctId("105266607")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withTrxCode("6015")
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
        coreTransaction.setServiceType("TX1");
        coreTransaction.setChannelId("cnb");
        coreTransaction.setCashierOperation("102526");
        coreTransaction.setCashOperation("88888");
        coreTransaction.setTransactionConfig(transactionConfigList);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputCoreConverter inputCoreConverter = new InputCoreConverter();

        message.setCoreTransaction(coreTransaction);
        //Execute
        TransferMessage responseMessage =  inputCoreConverter.process(message);
        InputCore inputCore = responseMessage.getInputCore();

        //Validation
        assertSame(message, responseMessage);
        assertEquals("0041A", inputCore.getTxn());
        assertEquals("CNB", inputCore.getCanal());
        assertNull(inputCore.getUsuario());
        assertNull( inputCore.getFuente());
        assertEquals(new BigDecimal("10.06"), inputCore.getMonto());
        assertEquals("081402", inputCore.getCajero());
        assertEquals("00814", inputCore.getSucursal());
        assertEquals("0105266607", inputCore.getCuenta1());
        assertNull(inputCore.getDescripcion());
    }

    @Test
     void process_ConLabelCanal_Ok() {
        //Config

        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withChannelId("cnb")
                .withFromAcctId("105266607")
                .withToAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .withTransactionLabel("Label canal")
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        InputCoreConverter InputCoreConverter = new InputCoreConverter();

        //Execute
        TransferMessage responseMessage =  InputCoreConverter.process(message);
        InputCore inputCore = responseMessage.getInputCore();

        //Validation
        assertSame(message, responseMessage);
        assertEquals("AIBT", inputCore.getIndicador());
        assertEquals("0041A", inputCore.getTxn());
        assertEquals("CNB", inputCore.getCanal());
        assertNull( inputCore.getUsuario());
        assertNull(inputCore.getFuente());
        assertEquals(new BigDecimal("10.06"), inputCore.getMonto());
        assertEquals("081402", inputCore.getCajero());
        assertEquals("00814", inputCore.getSucursal());
        assertEquals("0105266607", inputCore.getCuenta1());
        assertEquals("0105266606", inputCore.getCuenta2());
        assertEquals("Label canal", inputCore.getDescripcion());
    }
}
