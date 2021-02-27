package com.banistmo.itf.account.banking.transfer.services.core;

import com.banistmo.itf.account.banking.transfer.services.cl.AIBService.*;
import com.banistmo.itf.account.banking.transfer.services.cl.LocalTransferenceService.*;


import java.math.BigDecimal;
import java.math.RoundingMode;

class CoreIOMapper {

    AIBInput mapAIBInput(InputCore inputCore) {

        AIBInput aibInput = new AIBInput();
        aibInput.setClientId(inputCore.getClientId());
        aibInput.setTraceId(inputCore.getTraceId());
        aibInput.setWsEsimpCajero(inputCore.getCajero());
        aibInput.setWsEsimpCanal(inputCore.getCanal());
        aibInput.setWsEsimpCuenta1(inputCore.getCuenta1());
        aibInput.setWsEsimpCuenta2(inputCore.getCuenta2());
        aibInput.setWsEsimpDescripcion(inputCore.getDescripcion());
        aibInput.setWsEsimpFuente(inputCore.getFuente());
        aibInput.setWsEsimpMonto(inputCore.getMonto());
        aibInput.setWsEsimpReverso(inputCore.getReverso());
        aibInput.setWsEsimpStandin(inputCore.getStandin());
        aibInput.setWsEsimpSucursal(inputCore.getSucursal());
        aibInput.setWsEsimpTran01(inputCore.getTran01());
        aibInput.setWsEsimpTran02(inputCore.getTran02());
        aibInput.setWsEsimpTxn(inputCore.getTxn());
        aibInput.setWsEsimpUsuario(inputCore.getUsuario());
        aibInput.setWsIndicador(inputCore.getIndicador());


        return aibInput;
    }

    LocalTransferenceInput mapCNBInput(InputCore inputCore) {
        BigDecimal monto = inputCore.getMonto();
        String strMonto = monto.setScale(2, RoundingMode.DOWN).toString();
        strMonto = strMonto.replaceAll("\\.", "");

        LocalTransferenceInput localTransferenceInput = new LocalTransferenceInput();
        localTransferenceInput.setClientId(inputCore.getClientId());
        localTransferenceInput.setTraceId(inputCore.getTraceId());
        localTransferenceInput.setWsLnkBusCnbTxn(inputCore.getTxn());
        localTransferenceInput.setWsLnkBusCnbCajero(inputCore.getCajero());
        localTransferenceInput.setWsLnkBusCnbCuent2(inputCore.getCuenta2());
        localTransferenceInput.setWsLnkBusCnbCuenta(inputCore.getCuenta1());
        localTransferenceInput.setWsLnkBusCnbDescripcion(inputCore.getDescripcion());
        localTransferenceInput.setWsLnkBusCnbMonto(strMonto);
        localTransferenceInput.setWsLnkBusCnbSucursal(inputCore.getSucursal());
        localTransferenceInput.setWsLnkBusCnbConsecu(inputCore.getConsecutivo());

        return localTransferenceInput;
    }

    OutputCore mapAIBOutput(AIBOutput aibOutput) {

        OutputCore outputCore = new OutputCore();

        outputCore.setCoreCode(aibOutput.getWsSsimpCod());
        outputCore.setCoreDescription(aibOutput.getWsSsimpDesc());
        outputCore.setAibReas(aibOutput.getWsSsimpAibreas());
        outputCore.setAibResp(aibOutput.getWsSsimpAibresp());
        outputCore.setAibErrGen(aibOutput.getWsSsimpErrGen());

        return outputCore;
    }

    OutputCore mapCNBOutput(LocalTransferenceOutput depositOutput) {
        OutputCore outputCore = new OutputCore();
        outputCore.setCoreCode(depositOutput.getWsResultadoHogan());
        outputCore.setCoreDescription(depositOutput.getWsRespMensaje());
        outputCore.setConsecutivoTrx(depositOutput.getWsConsecutivoTx());
        outputCore.setAibReas(0);
        outputCore.setAibErrGen(0);

        return outputCore;
    }
}
