package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.resources.Request;
import com.banistmo.commons.bso.util.AppUtil;

public final class TestingAppUtil {

    @SuppressWarnings("unchecked")
    public static <T> Request<T> createBaseRequest() {
        return AppUtil.getAsObject(Request.class, "request.json");
    }
}
