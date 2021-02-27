package com.banistmo.itf.account.banking.transfer.services.core;

import com.banistmo.commons.bso.exceptions.BadGatewayException;
import com.banistmo.commons.bso.exceptions.BsoCauseException;
import com.banistmo.commons.bso.exceptions.GatewayTimeoutException;
import com.banistmo.cookie.faster.core.context.CookieApplication;
import com.banistmo.cookie.faster.core.context.CookieApplicationContext;
import com.banistmo.cookie.faster.core.exceptions.ConsumerException;
import com.banistmo.cookie.faster.core.exceptions.TimeoutConsumerException;

public abstract class TransferCoreService implements CoreService{
    private static final String BAD_GATEWAY_MSG = System.getenv("EXCEPTION502");
    private static final String GATEWAY_TIMEOUT_MSG = System.getenv("EXCEPTION504");
    private static final String BAD_GATEWAY_MSG_DT = System.getenv("EXCEPTION_DETAIL_502");
    private static final String GATEWAY_TIMEOUT_MSG_DT = System.getenv("EXCEPTION_DETAIL_504");
    static final CookieApplicationContext context = CookieApplication.start();
    static CoreIOMapper coreIOMapper = new CoreIOMapper();

    @Override
    public  OutputCore applyTransfer(InputCore inputCore){
        try {
            return apply(inputCore);
        } catch (TimeoutConsumerException e) {
            throw new GatewayTimeoutException(GATEWAY_TIMEOUT_MSG, GATEWAY_TIMEOUT_MSG_DT, new BsoCauseException(e.getMessage()));
        } catch (ConsumerException e) {
            throw new BadGatewayException(BAD_GATEWAY_MSG, BAD_GATEWAY_MSG_DT, new BsoCauseException(e.getMessage()));
        }
    }

    protected abstract OutputCore apply(InputCore inputCore);
}
