package com.banistmo.itf.account.banking.transfer.util;

import com.banistmo.commons.bso.util.TrxGenerator;
import com.banistmo.itf.account.banking.transfer.flow.GenericMessage;

public final class TransferAppUtil {
    private TransferAppUtil() {
    }

    private static boolean isCNB(String channelId) {
        return "cnb".equalsIgnoreCase(channelId);
    }

    public static String getTxnReverse(String channelId, String txn) {
        if (isCNB(channelId)) {
            return txn;
        }

        if (txn != null && txn.matches("\\d+")) {
            int txnRv = Integer.parseInt(txn) + 1;
            return "" + txnRv;
        }

        return txn + "2";
    }

    public static String getTrxId(String channelId, String trx) {
        if (isCNB(channelId)) {
            return trimTrxToSix(trx);
        }

        return TrxGenerator.getId();
    }

    private static String trimTrxToSix(String trxId) {
        if (trxId == null || trxId.length() <= 6) {
            return trxId;
        }

        return trxId.substring(trxId.length() - 6);
    }

    public static <T extends GenericMessage<?, ?>> String getSvcIdentFrom(T message) {
        String svcIdent = "N/D";

        if (message != null && message.getInputCore() != null) {
            svcIdent = message.getInputCore().getTxn();
        }

        return svcIdent;
    }
}
