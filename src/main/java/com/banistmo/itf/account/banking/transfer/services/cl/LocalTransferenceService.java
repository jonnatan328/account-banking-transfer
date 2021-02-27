package com.banistmo.itf.account.banking.transfer.services.cl;

import com.banistmo.cookie.faster.core.annotations.*;
import com.banistmo.cookie.faster.core.types.CoreFieldType;
import com.banistmo.cookie.faster.core.types.Pad;
import com.banistmo.cookie.faster.core.types.PadDirection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@CoreProgram(name = "CNB0002")
public interface LocalTransferenceService {

   LocalTransferenceOutput applyTransfer(LocalTransferenceInput localTransferenceInput);

   @Getter
   @Setter
   @CoreInput
   class LocalTransferenceInput {
      @ClientId
      @Getter(AccessLevel.NONE)
      String clientId;

      @TraceId
      @Getter(AccessLevel.NONE)
      String traceId;

      @CoreField(name = "WS-LNK-BUS-CNB-TXN", length = 5, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbTxn;

      @CoreField(name = "WS-LNK-BUS-CNB-CUENTA", length = 16, type = CoreFieldType.ALPHANUMERIC, padDirection = PadDirection.LEFT, pad = Pad.ZERO)
      private String wsLnkBusCnbCuenta;

      @CoreField(name = "WS-LNK-BUS-CNB-MONTO", length = 12, type = CoreFieldType.ALPHANUMERIC, padDirection = PadDirection.LEFT, pad = Pad.ZERO)
      private String wsLnkBusCnbMonto;

      @CoreField(name = "WS-LNK-BUS-CNB-CUENT2", length = 16, type = CoreFieldType.ALPHANUMERIC, padDirection = PadDirection.LEFT, pad = Pad.ZERO)
      private String wsLnkBusCnbCuent2;

      @CoreField(name = "WS-LNK-BUS-CNB-CONSECU", length = 6, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbConsecu;

      @CoreField(name = "WS-LNK-BUS-CNB-NOMBRE-APE", length = 30, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbNombreApe;

      @CoreField(name = "WS-LNK-BUS-CNB-ID-BENEFICI", length = 20, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbIdBenefici;

      @CoreField(name = "WS-LNK-BUS-CNB-CIUDAD", length = 10, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbCiudad;

      @CoreField(name = "WS-LNK-BUS-CNB-IP", length = 16, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbIp;

      @CoreField(name = "WS-LNK-BUS-CNB-REFERENTE", length = 12, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbReferente;

      @CoreField(name = "WS-LNK-BUS-CNB-PAG-SER-LONG", length = 5, type = CoreFieldType.NUMERIC, pad = Pad.ZERO, padDirection = PadDirection.LEFT)
      private Integer wsLnkBusCnbPagSerLong;

      @CoreField(name = "WS-LNK-BUS-CNB-PAG-SER-CUER", length = 195, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbPagSerCuer;

      @CoreField(name = "WS-LNK-BUS-CNB-SUCURSAL", length = 5, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbSucursal;

      @CoreField(name = "WS-LNK-BUS-CNB-CAJERO", length = 6, type = CoreFieldType.ALPHANUMERIC)
      private String wsLnkBusCnbCajero;

      @CoreField(name = "WS-LNK-BUS-CNB-DESCRIPCION", length = 40, type = CoreFieldType.ALPHANUMERIC, defaultValue = "TRANSFERENCIA BANCARIA CNB")
      private String wsLnkBusCnbDescripcion;

      @Getter(AccessLevel.NONE)
      @Setter(AccessLevel.NONE)
      @CoreField(name = "FILLER", length = 1415, type = CoreFieldType.ALPHANUMERIC)
      private String filler;

   }

   @Getter
   @Setter
   @CoreOutput
   class LocalTransferenceOutput {
      @Getter(AccessLevel.NONE)
      @Setter(AccessLevel.NONE)
      @CoreField(name = "FILLER", length = 3, type = CoreFieldType.ALPHANUMERIC)
      private String filler;

      @CoreField(name = "WS-RESULTADO-HOGAN", length = 1, type = CoreFieldType.ALPHANUMERIC)
      private String wsResultadoHogan;

      @CoreField(name = "WS-CODIGO-IMPRESION", length = 1, type = CoreFieldType.ALPHANUMERIC)
      private String wsCodigoImpresion;

      @CoreField(name = "WS-LINEAS-MENSAJES", length = 2, type = CoreFieldType.ALPHANUMERIC)
      private String wsLineasMensajes;

      @CoreField(name = "WS-CONSECUTIVO-TX", length = 7, type = CoreFieldType.ALPHANUMERIC)
      private String wsConsecutivoTx;

      @CoreField(name = "WS-NOTEBOOK", length = 1, type = CoreFieldType.ALPHANUMERIC)
      private String wsNotebook;

      @CoreField(name = "WS-RESP-MENSAJE", length = 1794, type = CoreFieldType.ALPHANUMERIC)
      private String wsRespMensaje;

   }
}
