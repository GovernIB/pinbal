package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaFiltreDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ConsultaFiltreSpringFilterParserTest {

    @Test
    public void parse_filtreNull_retornaDtoBuit() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse(null);

        assertNotNull(dto);
        assertNull(dto.getEntitatId());
        assertNull(dto.getScspPeticionId());
    }

    @Test
    public void parse_filtreBlanc_retornaDtoBuit() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("   ");

        assertNotNull(dto);
        assertNull(dto.getScspPeticionId());
    }

    @Test
    public void parse_entitatIdNumeric_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("entitatId:123");

        assertEquals(123L, dto.getEntitatId());
    }

    @Test
    public void parse_entitatPuntId_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("entitat.id:456");

        assertEquals(456L, dto.getEntitatId());
    }

    @Test
    public void parse_entitatIdNoNumeric_esIgnora() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("entitatId:'abc'");

        assertNull(dto.getEntitatId());
    }

    @Test
    public void parse_scspPeticionIdString_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("scspPeticionId:'PET-001'");

        assertEquals("PET-001", dto.getScspPeticionId());
    }

    @Test
    public void parse_estatValid_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("estat:'" + ConsultaDto.EstatTipus.Tramitada.name() + "'");

        assertEquals(ConsultaDto.EstatTipus.Tramitada, dto.getEstat());
    }

    @Test
    public void parse_estatInvalid_esIgnora() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("estat:'NO_EXISTEIX'");

        assertNull(dto.getEstat());
    }

    @Test
    public void parse_recobrimentTrue_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("recobriment:'true'");

        assertEquals(Boolean.TRUE, dto.getRecobriment());
    }

    @Test
    public void parse_recobrimentFalse_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("recobriment:'false'");

        assertEquals(Boolean.FALSE, dto.getRecobriment());
    }

    @Test
    public void parse_multipleTrue_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("multiple:'true'");

        assertEquals(Boolean.TRUE, dto.getMultiple());
    }

    @Test
    public void parse_funcionariEq_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("funcionariNomAmbDocument:'Joan Bibiloni'");

        assertEquals("Joan Bibiloni", dto.getFuncionari());
    }

    @Test
    public void parse_serveiCodiNomEq_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("serveiCodiNom:'SV001'");

        assertEquals("SV001", dto.getServeiCodi());
    }

    @Test
    public void parse_procedimentIdNumeric_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("procedimentId:789");

        assertEquals(789L, dto.getProcedimentId());
    }

    @Test
    public void parse_procedimentPuntId_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("procediment.id:321");

        assertEquals(321L, dto.getProcedimentId());
    }

    @Test
    public void parse_campDesconegut_esIgnora() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("campInexistent:'valor'");

        assertNull(dto.getScspPeticionId());
        assertNull(dto.getEntitatId());
    }

    @Test
    public void parse_valorBuit_esIgnora() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("scspPeticionId:''");

        assertNull(dto.getScspPeticionId());
    }

    @Test
    public void parse_like_scspPeticionId_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("scspPeticionId~'*PET*'");

        assertEquals("PET", dto.getScspPeticionId());
    }

    @Test
    public void parse_like_funcionari_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("funcionariNomAmbDocument~'*Joan*'");

        assertEquals("Joan", dto.getFuncionari());
    }

    @Test
    public void parse_like_serveiCodiNom_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("serveiCodiNom~'*SV*'");

        assertEquals("SV", dto.getServeiCodi());
    }

    @Test
    public void parse_like_valorBuit_esIgnora() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("scspPeticionId~'**'");

        assertNull(dto.getScspPeticionId());
    }

    @Test
    public void parse_like_titularNomComplet_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("titularNomComplet~'*Joan Bibiloni*'");

        assertEquals("Joan Bibiloni", dto.getTitularNom());
    }

    @Test
    public void parse_like_titularDocumentNum_esParseja() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("titularDocumentNum~'*12345678A*'");

        assertEquals("12345678A", dto.getTitularDocument());
    }

    @Test
    public void parse_dataIniciGte_esParseja() throws Exception {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("creacioData>:'2026-01-15T10:30:00'");

        Date expected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2026-01-15T10:30:00");
        assertEquals(expected, dto.getDataInici());
    }

    @Test
    public void parse_dataFiLte_esParseja() throws Exception {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("creacioData<:'2026-01-31T23:59:59'");

        Date expected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2026-01-31T23:59:59");
        assertEquals(expected, dto.getDataFi());
    }

    @Test
    public void parse_dataInvalida_retornaNullSenseLlancarExcepcio() {
        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse("creacioData>:'no-es-una-data'");

        assertNull(dto.getDataInici());
    }

    @Test
    public void parse_filtreCombinat_esParsegenTotsElsCamps() {
        String filter = "entitatId:5,estat:'Tramitada',recobriment:'true',scspPeticionId~'*ABC*',creacioData>:'2026-01-01T00:00:00',creacioData<:'2026-01-31T00:00:00'";

        ConsultaFiltreDto dto = ConsultaFiltreSpringFilterParser.parse(filter);

        assertEquals(5L, dto.getEntitatId());
        assertEquals(ConsultaDto.EstatTipus.Tramitada, dto.getEstat());
        assertEquals(Boolean.TRUE, dto.getRecobriment());
        assertEquals("ABC", dto.getScspPeticionId());
        assertNotNull(dto.getDataInici());
        assertNotNull(dto.getDataFi());
    }

    @Test
    public void translateSort_pageableNull_retornaUnpaged() {
        Pageable result = ConsultaFiltreSpringFilterParser.translateSort(null);

        assertTrue(result.isUnpaged());
    }

    @Test
    public void translateSort_senseOrdenacio_ordenaPerDataDescPerDefecte() {
        Pageable pageable = PageRequest.of(0, 10);

        Pageable result = ConsultaFiltreSpringFilterParser.translateSort(pageable);

        Sort.Order order = result.getSort().iterator().next();
        assertEquals("data", order.getProperty());
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    public void translateSort_campMapejable_esTradueix() {
        Pageable pageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "scspPeticionId"));

        Pageable result = ConsultaFiltreSpringFilterParser.translateSort(pageable);

        Sort.Order order = result.getSort().iterator().next();
        assertEquals("peticioId", order.getProperty());
        assertEquals(Sort.Direction.ASC, order.getDirection());
        assertEquals(1, result.getPageNumber());
        assertEquals(20, result.getPageSize());
    }

    @Test
    public void translateSort_campNoMapejable_esDescartaINoQuedaCapOrdre_usaDefault() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "campInexistent"));

        Pageable result = ConsultaFiltreSpringFilterParser.translateSort(pageable);

        Sort.Order order = result.getSort().iterator().next();
        assertEquals("data", order.getProperty());
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    public void translateSort_multiplesCampsBarrejats_nomesEsTradueixenElsMapejables() {
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.ASC, "estat").and(Sort.by(Sort.Direction.DESC, "campInexistent")));

        Pageable result = ConsultaFiltreSpringFilterParser.translateSort(pageable);

        assertEquals(1, result.getSort().stream().count());
        Sort.Order order = result.getSort().iterator().next();
        assertEquals("estat", order.getProperty());
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }
}
