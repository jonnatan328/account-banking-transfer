package com.banistmo.itf.account.banking.transfer.services.core;

import com.banistmo.itf.account.banking.transfer.services.cl.AIBService.*;
import com.banistmo.itf.account.banking.transfer.services.cl.LocalTransferenceService.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CoreIOMapperTest {

    private CoreIOMapper mapper = new CoreIOMapper();

    @Test
    void mapAIBInput() {
        //Config
        InputCore inputCore = new InputCore();
        inputCore.setIndicador("AIBS");
        inputCore.setTxn("3024");
        inputCore.setCuenta1("199124");
        inputCore.setMonto(new BigDecimal("40.76"));
        inputCore.setSucursal("8093");
        inputCore.setCajero("89523");
        inputCore.setDescripcion("label canal");
        inputCore.setReverso(1);
        inputCore.setUsuario("HUPS24");
        inputCore.setCanal("svp");
        inputCore.setFuente("F");

        //Execute

        AIBInput inputResponse = mapper.mapAIBInput(inputCore);

        //Validate

        assertEquals("AIBS", inputResponse.getWsIndicador());
        assertEquals("3024", inputResponse.getWsEsimpTxn());
        assertEquals("199124", inputResponse.getWsEsimpCuenta1());
        assertEquals(new BigDecimal("40.76"), inputResponse.getWsEsimpMonto());
        assertEquals("8093", inputResponse.getWsEsimpSucursal());
        assertEquals("89523", inputResponse.getWsEsimpCajero());
        assertEquals("label canal", inputResponse.getWsEsimpDescripcion());
        assertEquals(1, inputResponse.getWsEsimpReverso().intValue());
        assertEquals("HUPS24", inputResponse.getWsEsimpUsuario());
        assertEquals("svp", inputResponse.getWsEsimpCanal());
        assertEquals("F", inputResponse.getWsEsimpFuente());
    }

    @Test
    void mapCNBInput() {
        InputCore inputCore = new InputCore();
        inputCore.setIndicador(null);
        inputCore.setTxn("3024");
        inputCore.setCuenta1("199124");
        inputCore.setMonto(new BigDecimal("40.76"));
        inputCore.setSucursal("8093");
        inputCore.setCajero("89523");
        inputCore.setDescripcion("label canal");
        inputCore.setReverso(1);
        inputCore.setUsuario("HUPS24");
        inputCore.setCanal("svp");
        inputCore.setFuente("F");

        //Execute

        LocalTransferenceInput inputResponse = mapper.mapCNBInput(inputCore);

        //Validate

        assertEquals("3024", inputResponse.getWsLnkBusCnbTxn());
        assertEquals("199124", inputResponse.getWsLnkBusCnbCuenta());
        assertEquals("4076", inputResponse.getWsLnkBusCnbMonto());
        assertEquals("8093", inputResponse.getWsLnkBusCnbSucursal());
        assertEquals("89523", inputResponse.getWsLnkBusCnbCajero());
        assertEquals("label canal", inputResponse.getWsLnkBusCnbDescripcion());
        assertNull( inputResponse.getWsLnkBusCnbConsecu());
    }

    @Test
    void mapAIBOutput() {
        //Config
        AIBOutput aibOutput =  new AIBOutput();
        aibOutput.setWsSsimpCod("4");
        aibOutput.setWsSsimpDesc("un error cualquiera");
        aibOutput.setWsSsimpAibreas(167);

        //Execute

        OutputCore outputCore = mapper.mapAIBOutput(aibOutput);

        //Validate
        assertEquals("4", outputCore.getCoreCode());
        assertEquals("un error cualquiera", outputCore.getCoreDescription());
        assertEquals(167, outputCore.getAibReas().intValue());
        assertNull( outputCore.getConsecutivoTrx());
    }

    @Test
    void mapCNBOutput() {
        //Config
        LocalTransferenceOutput depositOutput =  new LocalTransferenceOutput();
        depositOutput.setWsResultadoHogan("4");
        depositOutput.setWsRespMensaje("un error cualquiera");
        depositOutput.setWsConsecutivoTx("54698");

        //Execute

        OutputCore outputCore = mapper.mapCNBOutput(depositOutput);

        //Validate
        assertEquals("4", outputCore.getCoreCode());
        assertEquals("un error cualquiera", outputCore.getCoreDescription());
        assertEquals("54698", outputCore.getConsecutivoTrx());
        assertEquals(0, outputCore.getAibReas().intValue());
        assertEquals(0, outputCore.getAibErrGen().intValue());
    }
}
