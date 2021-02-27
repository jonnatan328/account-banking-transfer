package com.banistmo.itf.account.banking.transfer.services;


import com.banistmo.itf.account.banking.transfer.services.core.AIBTransferCoreService;
import com.banistmo.itf.account.banking.transfer.services.core.CNBTransferCoreService;
import com.banistmo.itf.account.banking.transfer.services.core.CoreService;

public class CoreServiceProviderImpl implements CoreServiceProvider {
    private final CoreService defaultCoreService;
    private final CoreService cnbCoreService;

    public CoreServiceProviderImpl() {
        cnbCoreService = new CNBTransferCoreService();
        defaultCoreService = new AIBTransferCoreService();
    }

    @Override
    public CoreService getCoreService(String channelId) {
        if ("cnb".equals(channelId)) {
            return cnbCoreService;
        }

        return defaultCoreService;
    }
}
