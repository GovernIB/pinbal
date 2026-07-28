package es.caib.pinbal.plugin.dadescomunes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaisMLTest {

    @Test
    void builderIGettersFuncionenCorrectament() {
        PaisML pais = PaisML.builder()
                .codi_numeric("724")
                .alpha2("ES")
                .alpha3("ESP")
                .nom_ca("Espanya")
                .nom_es("España")
                .nom("Espanya")
                .build();

        assertThat(pais.getCodi_numeric()).isEqualTo("724");
        assertThat(pais.getAlpha2()).isEqualTo("ES");
        assertThat(pais.getAlpha3()).isEqualTo("ESP");
        assertThat(pais.getNom_ca()).isEqualTo("Espanya");
        assertThat(pais.getNom_es()).isEqualTo("España");
        assertThat(pais.getNom()).isEqualTo("Espanya");
    }

    @Test
    void equalsHashCodeIToStringFuncionenAmbTotsElsConstructors() {
        PaisML a = new PaisML("724", "ES", "ESP", "Espanya", "España", "Espanya");
        PaisML b = new PaisML();
        b.setCodi_numeric("724");
        b.setAlpha2("ES");
        b.setAlpha3("ESP");
        b.setNom_ca("Espanya");
        b.setNom_es("España");
        b.setNom("Espanya");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.toString()).contains("724", "ES", "ESP");
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("no es un PaisML");
    }
}
