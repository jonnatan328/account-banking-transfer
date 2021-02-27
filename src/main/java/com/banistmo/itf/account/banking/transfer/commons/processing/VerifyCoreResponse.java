package com.banistmo.itf.account.banking.transfer.commons.processing;

import com.banistmo.commons.bso.exceptions.BadGatewayException;
import com.banistmo.commons.bso.exceptions.BsoCauseException;
import com.banistmo.commons.bso.pipe.Pipe;
import com.banistmo.commons.bso.resources.CommonBodyRequest;
import com.banistmo.commons.bso.services.db.CoreErrorTransactionsService;
import com.banistmo.commons.bso.services.db.entities.CoreErrorTransactions;
import com.banistmo.itf.account.banking.transfer.flow.GenericMessage;
import com.banistmo.itf.account.banking.transfer.services.core.OutputCore;

import java.util.function.Function;

public class VerifyCoreResponse<T extends GenericMessage<? extends CommonBodyRequest, ?>>
        implements Pipe<T> {

    private static final String BAD_GATEWAY_MSG = System.getenv("EXCEPTION502");
    private static final String SOURCE_ODPA = "ODPA";
    private static final String SOURCE_AIB = "AIB";
    public static final Integer GENERIC_ERROR = 167;
    public static final String SUCCESS_CODE_RESPONSE = "00";
    public static final String SUCCESS_CODE_RESPONSE_ODPA = "0";
    private CoreErrorTransactionsService errorTransactionsService;

    public VerifyCoreResponse(CoreErrorTransactionsService errorTransactionsService) {
        this.errorTransactionsService = errorTransactionsService;
    }

    @Override
    public T process(T message) {
        String channelId = message.getServiceConfig().getChannelId();

        if ("cnb".equalsIgnoreCase(channelId)) {
            return verifyOutput(message, SUCCESS_CODE_RESPONSE_ODPA, SOURCE_ODPA, outputCore -> {
                String coreErrorCode = outputCore.getCoreCode();
                String trxId = message.getInputCore().getTxn();

                return String.format("%s-%s", coreErrorCode, trxId);
            });
        }

        return verifyOutput(message, SUCCESS_CODE_RESPONSE, SOURCE_AIB, outputCore -> {
            Integer aibCoreCode = outputCore.getAibReas();

            if (GENERIC_ERROR.equals(aibCoreCode)) {
                aibCoreCode = outputCore.getAibErrGen();
            }

            return aibCoreCode.toString();
        });
    }

    private T verifyOutput(T pipeMessage, String successCode, String source, Function<OutputCore, String> additionalCoreErrorCode) {
        OutputCore outputCore = pipeMessage.getOutputCore();
        String coreErrorCode = outputCore.getCoreCode();

        if (successCode.equals(coreErrorCode)) {
            return pipeMessage;
        }

        String coreDescription = outputCore.getCoreDescription();
        String errorMsg = BAD_GATEWAY_MSG;
        String detailErrorMsg = coreDescription;

        if (additionalCoreErrorCode != null) {
            coreErrorCode = additionalCoreErrorCode.apply(outputCore);
        }

        CoreErrorTransactions cet = errorTransactionsService.get(coreErrorCode, source);

        if (cet != null) {
            errorMsg = cet.getBusinessDetail();
            detailErrorMsg = cet.getTechnicalDetail();
        }

        throw new BadGatewayException(errorMsg, detailErrorMsg, new BsoCauseException(coreErrorCode, coreDescription));
    }
}
