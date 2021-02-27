package com.banistmo.itf.account.banking.transfer.dts.rq;

import com.banistmo.commons.bso.resources.CommonBodyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XferRevRQ extends CommonBodyRequest {
    private RevReasonCode revReasonCode;
    private XferRqMsg xferRqMsg;
}
