package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.services.notify.NotifyService;

public class NotifyServiceMock implements NotifyService {

    @Override
    public String publish(Object o) {
        return "1253do335k";
    }

    @Override
    public void setTopicArn(String s) {

    }
}
