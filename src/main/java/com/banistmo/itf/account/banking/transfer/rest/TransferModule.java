package com.banistmo.itf.account.banking.transfer.rest;

import com.banistmo.commons.bso.services.BusinessServices;
import com.banistmo.commons.bso.services.ServiceConfigServicesImpl;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProvider;
import com.banistmo.itf.account.banking.transfer.services.CoreServiceProviderImpl;
import com.banistmo.itf.account.banking.transfer.services.FileBusinessService;

import dagger.Module;
import dagger.Provides;

import java.io.InputStream;

import javax.inject.Singleton;

@Module
class TransferModule {

    @Provides
    @Singleton
    CoreServiceProvider provideCoreServiceProvider(){
        return new CoreServiceProviderImpl();
    }

    @Provides
    @Singleton
    BusinessServices provideBusinessServices(){
    	ClassLoader classLoader = ServiceConfigServicesImpl.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream("core-transaction-details.json");
        return new FileBusinessService(is);
    }
}
