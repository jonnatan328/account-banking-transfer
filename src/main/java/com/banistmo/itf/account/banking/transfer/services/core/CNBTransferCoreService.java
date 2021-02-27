package com.banistmo.itf.account.banking.transfer.services.core;


import com.banistmo.itf.account.banking.transfer.services.cl.LocalTransferenceService;
import com.banistmo.itf.account.banking.transfer.services.cl.LocalTransferenceService.*;

public class CNBTransferCoreService extends TransferCoreService {

    private static LocalTransferenceService cnbService;

    static {
        cnbService = context.getService(LocalTransferenceService.class);
    }

    @Override
    protected OutputCore apply(InputCore inputCore) {

        LocalTransferenceInput cnbInput = coreIOMapper.mapCNBInput(inputCore);
        LocalTransferenceOutput cnbOutput = cnbService.applyTransfer(cnbInput);

        return coreIOMapper.mapCNBOutput(cnbOutput);
    }
}
