package com.banistmo.itf.account.banking.transfer.services.core;

public class MockCoreService implements CoreService {

    private OutputCore outputCore;

    public MockCoreService(OutputCore outputCore) {
        this.outputCore = outputCore;
    }

    @Override
    public OutputCore applyTransfer(InputCore inputCore) {
        return outputCore;
    }
}
