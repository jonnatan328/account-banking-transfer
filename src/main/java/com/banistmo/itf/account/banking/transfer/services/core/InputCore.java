package com.banistmo.itf.account.banking.transfer.services.core;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InputCore {
    private String clientId;
    private String traceId;
    private String indicador;
    private String txn;
    private String cuenta1;
    private BigDecimal monto;
    private String cuenta2;
    private String sucursal;
    private String cajero;
    private String descripcion;
    private Integer reverso;
    private Integer standin;
    private String usuario;
    private String canal;
    private String tran01;
    private String tran02;
    private String fuente;
    private String consecutivo;
}
