package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.services.db.CoreTransactionDetailsService;
import com.banistmo.commons.bso.services.db.entities.CoreTransaction;
import com.banistmo.commons.bso.services.db.entities.TransactionConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CoreTransactionDetailsServiceMock implements CoreTransactionDetailsService {
    private static File file;

    static {
        ClassLoader classLoader = CoreTransactionDetailsServiceMock.class.getClassLoader();
        URL url = classLoader.getResource("db/transaction-detail.json");

        file = new File(url.getFile());
    }

    @Override
    public CoreTransaction get(String channelId, String serviceType) {
        return get(channelId, serviceType, null);
    }

    @Override
    public CoreTransaction get(String channelId, String serviceType, Predicate<TransactionConfig> predicate) {
        if (channelId == null || serviceType == null) {
            return null;
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            List<CoreTransaction> coreTransactions = objectMapper.readValue(file,
                    new TypeReference<List<CoreTransaction>>() {});

            CoreTransaction coreTransaction = coreTransactions.stream()
                    .filter(m -> channelId.equals(m.getChannelId()) && serviceType.equals(m.getServiceType()))
                    .findAny()
                    .orElse(null);

            if (coreTransaction != null && predicate != null) {
                List<TransactionConfig> errorConfigList = coreTransaction.getTransactionConfig().stream()
                        .filter(predicate)
                        .collect(Collectors.toList());

                coreTransaction.setTransactionConfig(errorConfigList);
            }

            return coreTransaction;

        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
