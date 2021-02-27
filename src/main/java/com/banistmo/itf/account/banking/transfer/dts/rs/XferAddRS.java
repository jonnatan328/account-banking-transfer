package com.banistmo.itf.account.banking.transfer.dts.rs;

import com.banistmo.commons.bso.resources.Response;
import lombok.*;

@Getter
@Setter
public class XferAddRS extends Response {
    private XferRec xferRec;
}
