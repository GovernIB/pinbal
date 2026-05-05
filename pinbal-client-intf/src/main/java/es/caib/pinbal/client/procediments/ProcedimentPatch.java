package es.caib.pinbal.client.procediments;

import es.caib.pinbal.client.comu.OptionalField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentPatch {

    private OptionalField<String> codi;
    private OptionalField<String> nom;
    private OptionalField<String> departament;
    private OptionalField<Boolean> actiu;
//    private OptionalField<String> entitatCodi;
    private OptionalField<String> organGestorDir3;
    private OptionalField<String> codiSia;
    private OptionalField<Boolean> valorCampAutomatizado;
    private OptionalField<ClaseTramite> valorCampClaseTramite;


    public static class ProcedimentPatchDtoBuilder {

        private OptionalField<String> codi;
        private OptionalField<String> nom;
        private OptionalField<String> departament;
        private OptionalField<Boolean> actiu;
//        private OptionalField<String> entitatCodi;
        private OptionalField<String> organGestorDir3;
        private OptionalField<String> codiSia;
        private OptionalField<Boolean> valorCampAutomatizado;
        private OptionalField<ClaseTramite> valorCampClaseTramite;

        public ProcedimentPatchDtoBuilder codi(String codi) {
            this.codi = OptionalField.of(codi);
            return this;
        }
        public ProcedimentPatchDtoBuilder nom(String nom) {
            this.nom = OptionalField.of(nom);
            return this;
        }
        public ProcedimentPatchDtoBuilder departament(String departament) {
            this.departament = OptionalField.of(departament);
            return this;
        }
        public ProcedimentPatchDtoBuilder actiu(Boolean actiu) {
            this.actiu = OptionalField.of(actiu);
            return this;
        }
//        public ProcedimentPatchDtoBuilder entitatCodi(String entitatCodi) {
//            this.entitatCodi = OptionalField.of(entitatCodi);
//            return this;
//        }
        public ProcedimentPatchDtoBuilder organGestorDir3(String organGestorDir3) {
            this.organGestorDir3 = OptionalField.of(organGestorDir3);
            return this;
        }
        public ProcedimentPatchDtoBuilder codiSia(String codiSia) {
            this.codiSia = OptionalField.of(codiSia);
            return this;
        }
        public ProcedimentPatchDtoBuilder valorCampAutomatizado(Boolean valorCampAutomatizado) {
            this.valorCampAutomatizado = OptionalField.of(valorCampAutomatizado);
            return this;
        }
        public ProcedimentPatchDtoBuilder valorCampClaseTramite(ClaseTramite valorCampClaseTramite) {
            this.valorCampClaseTramite = OptionalField.of(valorCampClaseTramite);
            return this;
        } 

    }
}
