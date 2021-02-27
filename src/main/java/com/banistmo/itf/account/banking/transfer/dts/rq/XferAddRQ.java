package com.banistmo.itf.account.banking.transfer.dts.rq;

import com.banistmo.commons.bso.resources.CommonBodyRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XferAddRQ extends CommonBodyRequest {
    private XferInfo xferInfo;
}
