package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.services.db.CoreErrorTransactionsService;
import com.banistmo.commons.bso.services.db.entities.CoreErrorTransactions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CoreErrorTransactionsServiceMock implements CoreErrorTransactionsService {

    private static File file;

    static {
        ClassLoader classLoader = CoreErrorTransactionsServiceMock.class.getClassLoader();
        URL url = classLoader.getResource("db/error-transaction.json");

        file = new File(url.getFile());
    }

    @Override
    public CoreErrorTransactions get(String code, String source) {
        if (code == null || source == null) {
            return null;
        }

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            List<CoreErrorTransactions> errorTransactions = objectMapper.readValue(file,
                    new TypeReference<List<CoreErrorTransactions>>() {});

            return errorTransactions.stream()
                    .filter(m -> code.equals(m.getCode()) && source.equals(m.getSource()))
                    .findAny()
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
