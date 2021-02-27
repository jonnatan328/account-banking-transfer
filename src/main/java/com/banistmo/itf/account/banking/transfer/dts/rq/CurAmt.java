package com.banistmo.itf.account.banking.transfer.dts.rq;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class CurAmt {
    private BigDecimal amt;
    private String curCode;
}
