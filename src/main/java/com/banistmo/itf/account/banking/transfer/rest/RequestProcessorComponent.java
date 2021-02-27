package com.banistmo.itf.account.banking.transfer.rest;


import com.banistmo.itf.account.banking.transfer.flow.TransferRequestProcessor;
import com.banistmo.itf.account.banking.transfer.flow.TransferReverseRequestProcessor;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TransferModule.class})
interface RequestProcessorComponent {
    TransferRequestProcessor makeRequestProcessor();
    TransferReverseRequestProcessor makeReverseRequestProcessor();
}
