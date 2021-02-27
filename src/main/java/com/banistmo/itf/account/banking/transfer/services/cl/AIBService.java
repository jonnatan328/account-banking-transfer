package com.banistmo.itf.account.banking.transfer.services.cl;

import java.math.BigDecimal;

import com.banistmo.cookie.faster.core.annotations.*;
import com.banistmo.cookie.faster.core.types.CoreFieldType;
import com.banistmo.cookie.faster.core.types.Pad;
import com.banistmo.cookie.faster.core.types.PadDirection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@CoreProgram(name = "OPLEMONE")
public interface AIBService {

   AIBOutput applyTransfer(AIBInput aibInput);

   @Getter
   @Setter
   @CoreInput
   class AIBInput {
      @ClientId
      @Getter(AccessLevel.NONE)
      String clientId;

      @TraceId
      @Getter(AccessLevel.NONE)
      String traceId;

      @CoreField(name = "WS-INDICADOR", length = 4, type = CoreFieldType.ALPHANUMERIC)
      private String wsIndicador;

      @CoreField(name = "WS-ESIMP-TXN", length = 4, type = CoreFieldType.ALPHANUMERIC)
      private String wsEsimpTxn;

      @CoreField(name = "WS-ESIMP-CUENTA1", length = 10, type = CoreFieldType.ALPHANUMERIC, pad = Pad.ZERO, padDirection = PadDirection.LEFT)
      private String wsEsimpCuenta1;

      @CoreField(name = "WS-ESIMP-MONTO", length = 15, type = CoreFieldType.NUMERIC_IMPLIED_DECIMAL, scale = 2, pad = Pad.ZERO, padDirection = PadDirection.LEFT)
      private BigDecimal wsEsimpMonto;

      @CoreField(name = "WS-ESIMP-CUENTA2", length = 10, type = CoreFieldType.ALPHANUMERIC, pad = Pad.ZERO, padDirection = PadDirection.LEFT)
      private String wsEsimpCuenta2;

      @CoreField(name = "WS-ESIMP-SUCURSAL", length = 5, type = CoreFieldType.ALPHANUMERIC, pad = Pad.ZERO, padDirection = PadDirection.LEFT)
      private String wsEsimpSucursal;

      @CoreField(name = "WS-ESIMP-CAJERO", length = 6, type = CoreFieldType.ALPHANUMERIC, pad = Pad.ZERO, padDirection = PadDirection.LEFT)
      private String wsEsimpCajero;

      @CoreField(name = "WS-ESIMP-DESCRIPCION", length = 40, type = CoreFieldType.ALPHANUMERIC)
      private String wsEsimpDescripcion;

      @CoreField(name = "WS-ESIMP-REVERSO", length = 1, type = CoreFieldType.NUMERIC, pad = Pad.ZERO)
      private Integer wsEsimpReverso;

      @CoreField(name = "WS-ESIMP-STANDIN", length = 1, type = CoreFieldType.NUMERIC, pad = Pad.ZERO)
      private Integer wsEsimpStandin;

      @CoreField(name = "WS-ESIMP-USUARIO", length = 8, type = CoreFieldType.ALPHANUMERIC)
      private String wsEsimpUsuario;

      @CoreField(name = "WS-ESIMP-CANAL", length = 3, type = CoreFieldType.ALPHANUMERIC)
      private String wsEsimpCanal;

      @CoreField(name = "WS-ESIMP-TRAN01", length = 4, type = CoreFieldType.ALPHANUMERIC)
      private String wsEsimpTran01;

      @CoreField(name = "WS-ESIMP-TRAN02", length = 4, type = CoreFieldType.ALPHANUMERIC)
      private String wsEsimpTran02;

      @CoreField(name = "WS-ESIMP-FUENTE", length = 2, type = CoreFieldType.ALPHANUMERIC)
      private String wsEsimpFuente;

      @Getter(AccessLevel.NONE)
      @Setter(AccessLevel.NONE)
      @CoreField(name = "FILLER", length = 387, type = CoreFieldType.ALPHANUMERIC)
      private String filler;

   }

   @Getter
   @Setter
   @CoreOutput
   class AIBOutput {
      @CoreField(name = "WS-SSIMP-COD", length = 2, type = CoreFieldType.ALPHANUMERIC)
      private String wsSsimpCod;

      @CoreField(name = "WS-SSIMP-DESC", length = 100, type = CoreFieldType.ALPHANUMERIC)
      private String wsSsimpDesc;

      @CoreField(name = "WS-SSIMP-AIBRESP", length = 4, type = CoreFieldType.NUMERIC)
      private Integer wsSsimpAibresp;

      @CoreField(name = "WS-SSIMP-AIBREAS", length = 4, type = CoreFieldType.NUMERIC)
      private Integer wsSsimpAibreas;

      @CoreField(name = "WS-SSIMP-ERR-GEN", length = 5, type = CoreFieldType.NUMERIC)
      private Integer wsSsimpErrGen;

      @Getter(AccessLevel.NONE)
      @Setter(AccessLevel.NONE)
      @CoreField(name = "FILLER", length = 385, type = CoreFieldType.ALPHANUMERIC)
      private String filler;

   }
}
