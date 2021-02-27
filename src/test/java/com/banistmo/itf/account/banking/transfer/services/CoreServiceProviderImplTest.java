package com.banistmo.itf.account.banking.transfer.services;

import com.banistmo.itf.account.banking.transfer.services.core.AIBTransferCoreService;
import com.banistmo.itf.account.banking.transfer.services.core.CNBTransferCoreService;
import lombok.var;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.assertTrue;

@PowerMockIgnore("javax.net.ssl.*")
public class CoreServiceProviderImplTest {

    @Test
    public void getCoreService_ConChannelId_CNB_ReturnCNBService() {
        //Config
        var coreServiceProvider = new CoreServiceProviderImpl();

        //Execute
        var coreService =  coreServiceProvider.getCoreService("cnb");

        //Validate
        assertTrue(coreService instanceof CNBTransferCoreService);
    }

    @Test
    public void getCoreService_ConChannelId_Any_ReturnCNBService() {
        //Config
        var coreServiceProvider = new CoreServiceProviderImpl();

        //Execute
        var coreService =  coreServiceProvider.getCoreService("svp");

        //Validate
        assertTrue(coreService instanceof AIBTransferCoreService);
    }
}
