package com.banistmo.itf.account.banking.transfer.dts.rq;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class XferInfo {
    private FromAcctRef fromAcctRef;
    private ToAcctRef toAcctRef;
    private CurAmt curAmt;
}
