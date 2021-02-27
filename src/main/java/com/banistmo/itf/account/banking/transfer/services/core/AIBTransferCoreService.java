package com.banistmo.itf.account.banking.transfer.services.core;


import com.banistmo.itf.account.banking.transfer.services.cl.AIBService;
import com.banistmo.itf.account.banking.transfer.services.cl.AIBService.*;

public class AIBTransferCoreService extends TransferCoreService {
    private static AIBService aibService;

    static {
        aibService = context.getService(AIBService.class);
    }

    @Override
    protected OutputCore apply(InputCore inputCore) {

        AIBInput aibInput = coreIOMapper.mapAIBInput(inputCore);
        AIBOutput aibOutput = aibService.applyTransfer(aibInput);

        return coreIOMapper.mapAIBOutput(aibOutput);
    }
}
