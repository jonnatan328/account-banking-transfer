package com.banistmo.itf.account.banking.transfer;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.logging.handler.LoggerHandler;
import com.banistmo.logging.util.Constants;

public final class LoggerHandlerFactory {
    private LoggerHandlerFactory(){}

    public static LoggerHandler create(Request<?> request){
        return LoggerHandler.getInstance(Constants.TIER_COMPOSITION, request, new ImplementContext());
    }
}
