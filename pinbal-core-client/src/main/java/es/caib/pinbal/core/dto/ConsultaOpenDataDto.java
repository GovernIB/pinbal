package es.caib.pinbal.core.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Calendar;
import java.util.Date;

@Data
@Builder
@ToString
public class ConsultaOpenDataDto {
    private String entitatCodi;
    private Date dataInici;
    private Date dataFi;
    private String procedimentCodi;
    private String serveiCodi;
    private Integer pagina;
    private Integer mida;
    private String appPath;

    public static class ConsultaOpenDataDtoBuilder {
        private Date dataInici;
        private Date dataFi;
        private Integer pagina;
        private Integer mida;

        public ConsultaOpenDataDtoBuilder dataInici(Date dataInici) {
            if (dataInici == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                this.dataInici = calendar.getTime();
            } else {
                this.dataInici = dataInici;
            }
            return this;
        }
        public ConsultaOpenDataDtoBuilder dataFi(Date dataFi) {
            if (dataFi == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                this.dataFi = calendar.getTime();
            } else {
                this.dataFi = dataFi;
            }
            return this;
        }
        public ConsultaOpenDataDtoBuilder pagina(Integer pagina) {
            if (pagina == null) {
                this.pagina = 0;
            } else {
                this.pagina = pagina;
            }
            return this;
        }
        public ConsultaOpenDataDtoBuilder mida(Integer mida) {
            if (mida == null) {
                this.mida = 50;
            } else {
                this.mida = mida;
            }
            return this;
        }
    }
}
