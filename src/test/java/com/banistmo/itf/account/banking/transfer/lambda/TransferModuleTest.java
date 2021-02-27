package com.banistmo.itf.account.banking.transfer.lambda;

import lombok.var;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.assertNotNull;


@PowerMockIgnore("javax.net.ssl.*")
public class TransferModuleTest {
    @Test
    public void provideCoreServiceProvider() {
        //Config
        var module = new TransferModule();

        //Execute
        var coreService = module.provideCoreServiceProvider();

        //Validate
        assertNotNull(coreService);
    }

    @Test
    public void provideBusinessServices() {
        //Config
        var module = new TransferModule();

        //Execute
        var businessServices = module.provideBusinessServices();

        //Validate
        assertNotNull(businessServices);
    }
}
