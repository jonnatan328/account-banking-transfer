package com.banistmo.itf.account.banking.transfer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferAppUtilTest {

    @Test
    void getTxnReverse_ChannelId_CNB_ReturnSame(){
        //Execute
        String txnReverse = TransferAppUtil.getTxnReverse("cnb", "200a");

        //Validate
        assertEquals("200a", txnReverse);
    }

    @Test
    void getTxnReverse_ChannelId_Any_ReturnMas1(){
        //Execute
        String txnReverse = TransferAppUtil.getTxnReverse("svp", "1052");

        //Validate
        assertEquals("1053", txnReverse);
    }

    @Test
    void getTxnReverse_ChannelId_Any_Y_TxnNoNumerico_ReturnAdd2(){
        //Execute
        String txnReverse = TransferAppUtil.getTxnReverse("svp", "192a");

        //Validate
        assertEquals("192a2", txnReverse);
    }

    @Test
    void getTrxId_ConChannelId_CNB_ReturnTrim6(){
        //Execute
        String trxId = TransferAppUtil.getTrxId("cnb", "0122544");

        //Validate
        assertEquals("122544", trxId);
    }

    @Test
    void getTrxId_ConChannelId_CNB_Y_TrxId_Null_ReturnNull(){
        //Execute
        String trxId = TransferAppUtil.getTrxId("cnb", null);

        //Validate
        assertNull(trxId);
    }

    @Test
    void getTrxId_ConChannelId_CNB_Y_TrxId_MenorA6_ReturnSame(){
        //Execute
        String trxId = TransferAppUtil.getTrxId("cnb", "12544");

        //Validate
        assertEquals("12544", trxId);
    }

    @Test
    void getTrxId_ConChannelId_Any(){
        //Execute
        String trxId = TransferAppUtil.getTrxId("svp", "0122544");

        //Validate
        assertNotNull(trxId);
    }
}
