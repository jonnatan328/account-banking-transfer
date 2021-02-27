package com.banistmo.itf.account.banking.transfer.services.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutputCore {
    private String coreCode;
    private String coreDescription;
    private Integer aibResp;
    private Integer aibReas;
    private Integer aibErrGen;
    private String consecutivoTrx;
}
