package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.services.BusinessServices;
import com.banistmo.commons.bso.services.db.CoreErrorTransactionsService;
import com.banistmo.commons.bso.services.db.CoreTransactionDetailsService;
import com.banistmo.commons.bso.services.notify.NotifyService;

public class BusinessServicesMock implements BusinessServices {

    @Override
    public CoreTransactionDetailsService getCoreTransactionDetailsService() {
        return new CoreTransactionDetailsServiceMock();
    }

    @Override
    public CoreErrorTransactionsService getCoreErrorTransactionsService() {
        return new CoreErrorTransactionsServiceMock();
    }

    @Override
    public NotifyService getNotifyService() {
        return new NotifyServiceMock();
    }

}
