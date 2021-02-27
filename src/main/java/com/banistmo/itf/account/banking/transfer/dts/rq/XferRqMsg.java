package com.banistmo.itf.account.banking.transfer.dts.rq;

import lombok.Data;

@Data
public class XferRqMsg {
    private String revRqUID;
    private XferInfo xferInfo;
}
