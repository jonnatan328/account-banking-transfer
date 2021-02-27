package com.banistmo.itf.account.banking.transfer.services;


import com.banistmo.itf.account.banking.transfer.services.core.CoreService;

public interface CoreServiceProvider {

    CoreService getCoreService(String channelId);
}
