package es.caib.pinbal.plugin.dadescomunes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProvinciaMLTest {

    @Test
    void builderIGettersFuncionenCorrectament() {
        ProvinciaML provincia = ProvinciaML.builder()
                .codi("07")
                .nom_ca("Illes Balears")
                .nom_es("Islas Baleares")
                .nom("Illes Balears")
                .build();

        assertThat(provincia.getCodi()).isEqualTo("07");
        assertThat(provincia.getNom_ca()).isEqualTo("Illes Balears");
        assertThat(provincia.getNom_es()).isEqualTo("Islas Baleares");
        assertThat(provincia.getNom()).isEqualTo("Illes Balears");
    }

    @Test
    void equalsHashCodeIToStringFuncionenAmbTotsElsConstructors() {
        ProvinciaML a = new ProvinciaML("07", "Illes Balears", "Islas Baleares", "Illes Balears");
        ProvinciaML b = new ProvinciaML();
        b.setCodi("07");
        b.setNom_ca("Illes Balears");
        b.setNom_es("Islas Baleares");
        b.setNom("Illes Balears");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.toString()).contains("07", "Illes Balears");
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("no es una ProvinciaML");
    }
}
