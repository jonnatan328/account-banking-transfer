package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.exceptions.ApplicationException;
import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.services.db.CoreTransactionDetailsService;
import com.banistmo.itf.account.banking.transfer.LoggerHandlerFactory;
import com.banistmo.itf.account.banking.transfer.dts.rq.XferAddRQ;
import com.banistmo.itf.account.banking.transfer.flow.TransferMessage;
import com.banistmo.itf.account.banking.transfer.util.TestingAppUtil;
import com.banistmo.itf.account.banking.transfer.util.TransferBodyRequestBuilder;
import com.banistmo.itf.account.banking.transfer.util.BusinessServicesMock;
import com.banistmo.logging.handler.LoggerHandler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CoreTransactionConfigLoaderTest {

    private Request<XferAddRQ> request = TestingAppUtil.createBaseRequest();
    private LoggerHandler loggerHandler = LoggerHandlerFactory.create(request);
    private CoreTransactionDetailsService service = new BusinessServicesMock().getCoreTransactionDetailsService();

    @Test
    public void process_ConServiceTypeNotNull_Ok() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR01")
                .withChannelId("svp")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        CoreTransactionConfigLoader<TransferMessage> coreTransactionConfigLoader = new CoreTransactionConfigLoader<>(service);

        //Execute
        TransferMessage responseMessage = coreTransactionConfigLoader.process(message);

        //Validate
        assertSame(message, responseMessage);
        assertNotNull(responseMessage.getCoreTransaction());
    }

    @Test
    public void process_ConServiceTypeNoExistente_ThrowException() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("PS89EDJ")
                .withChannelId("SVP")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        CoreTransactionConfigLoader<TransferMessage> coreTransactionConfigLoader = new CoreTransactionConfigLoader<>(service);

        //Execute and Validate
        assertThrows(ApplicationException.class, () -> coreTransactionConfigLoader.process(message));
    }

    @Test
    public void process_ConServiceTypeExistenteSinTransactionConfig_ThrowException() {
        //Config
        XferAddRQ body = TransferBodyRequestBuilder.create()
                .withRqUID("1234")
                .withServiceType("TR02")
                .withChannelId("svp")
                .withToAcctId("105266607")
                .withFromAcctId("105266606")
                .withClientTerminalSeqNum("81402")
                .withPosLocation("814")
                .withCurAmt(new BigDecimal("10.06"))
                .build();

        request.setBody(body);

        TransferMessage message = new TransferMessage(request, loggerHandler);
        CoreTransactionConfigLoader<TransferMessage> coreTransactionConfigLoader = new CoreTransactionConfigLoader<>(service);

        //Execute and Validate
        assertThrows(ApplicationException.class, () -> coreTransactionConfigLoader.process(message));
    }
}
