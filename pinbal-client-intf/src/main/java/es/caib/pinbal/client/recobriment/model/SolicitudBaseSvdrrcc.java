package es.caib.pinbal.client.recobriment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

public abstract class SolicitudBaseSvdrrcc extends SolicitudBase{

    @Getter @Setter
    protected TitularDadesAdicionals titularDadesAdicionals;
    @Getter @Setter
    protected DadesRegistrals dadesRegistrals;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class TitularDadesAdicionals {
        private FetRegistral fetregistral;
        private Naixement naixement;
        private String nomMare;
        private String nomPare;
        private Sexe sexe;
        private boolean ausenciaSegonLlinatge;

        public boolean isEmpty() {
            return (sexe == null && isEmptyString(nomMare) && isEmptyString(nomPare) &&
                    (fetregistral == null || fetregistral.isEmpty()) &&
                    (naixement == null || naixement.isEmpty()));
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class FetRegistral {
        private Date data;
        private Lloc municipi;

        public boolean isEmpty() {
            return (data == null && (municipi == null || municipi.isEmpty()));
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Naixement {
        private Date data;
        private String paisCodi;
        private Lloc provincia;
        private Lloc municipi;

        public boolean isEmpty() {
            return (data == null && isEmptyString(paisCodi) &&
                    (provincia == null || provincia.isEmpty()) &&
                    (municipi == null || municipi.isEmpty()));
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class DadesRegistrals {
        private String registreCivil;
        private String tom;
        private String pagina;

        public boolean isEmpty() {
            return (isEmptyString(registreCivil) && isEmptyString(tom) && isEmptyString(pagina));
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Lloc {
        private String codi;
        private String descripcio;

        public boolean isEmpty() {
            return (isEmptyString(codi) && isEmptyString(descripcio));
        }
    }
}
