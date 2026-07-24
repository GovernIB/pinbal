package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.ConfigDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.persist.entity.Config;
import es.caib.pinbal.persist.entity.ConfigType;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.Servei;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DtoMappingHelperTest {

    private DtoMappingHelper dtoMappingHelper;

    @BeforeEach
    public void setUp() {
        dtoMappingHelper = new DtoMappingHelper();
    }

    @Test
    public void convertir_sourceNull_retornaNull() {
        assertNull(dtoMappingHelper.convertir(null, ServeiDto.class));
    }

    @Test
    public void convertirList_llistaNull_retornaNull() {
        assertNull(dtoMappingHelper.convertirList(null, ServeiDto.class));
    }

    @Test
    public void convertirSet_conjuntNull_retornaNull() {
        assertNull(dtoMappingHelper.convertirSet(null, ServeiDto.class));
    }

    @Test
    public void convertir_servei_mapejaCampsPerDefecte() {
        Servei servei = new Servei();
        servei.setCodi("SV001");
        servei.setDescripcio("Servei de prova");

        ServeiDto dto = dtoMappingHelper.convertir(servei, ServeiDto.class);

        assertEquals("SV001", dto.getCodi());
        assertEquals("Servei de prova", dto.getDescripcio());
    }

    @Test
    public void convertir_entitat_mapejaCampsPerDefecte() {
        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi("ENT01");
        entitat.setNom("Entitat de prova");
        entitat.setCif("Q0700000A");

        EntitatDto dto = dtoMappingHelper.convertir(entitat, EntitatDto.class);

        assertEquals(1L, dto.getId());
        assertEquals("ENT01", dto.getCodi());
        assertEquals("Entitat de prova", dto.getNom());
        assertEquals("Q0700000A", dto.getCif());
    }

    @Test
    public void convertir_procediment_mapejaEntitatIdIEntitatNomDesDeLEntitatRelacionada() {
        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(5L);
        entitat.setNom("Entitat Pare");

        Procediment procediment = Procediment.getBuilder(
                entitat, "PROC01", "Procediment de prova", "Departament", null, null, null, null).build();
        procediment.configurarIdPerTest(10L);

        ProcedimentDto dto = dtoMappingHelper.convertir(procediment, ProcedimentDto.class);

        assertEquals(10L, dto.getId());
        assertEquals("PROC01", dto.getCodi());
        assertEquals(5L, dto.getEntitatId());
        assertEquals("Entitat Pare", dto.getEntitatNom());
    }

    @Test
    public void convertir_config_usaCustomMapperPerObtenirValidValuesDesDeLTipus() {
        ConfigType tipus = new ConfigType();
        ReflectionTestUtils.setField(tipus, "code", "TIPUS1");
        ReflectionTestUtils.setField(tipus, "value", "A,B,C");

        Config config = new Config("clau.test", "valorActual");
        ReflectionTestUtils.setField(config, "type", tipus);

        ConfigDto dto = dtoMappingHelper.convertir(config, ConfigDto.class);

        assertEquals("clau.test", dto.getKey());
        assertEquals("valorActual", dto.getValue());
        assertEquals("TIPUS1", dto.getTypeCode());
        assertEquals(List.of("A", "B", "C"), dto.getValidValues());
    }

    @Test
    public void convertirList_llistaDeServeis_mapejaTots() {
        Servei s1 = new Servei();
        s1.setCodi("SV001");
        Servei s2 = new Servei();
        s2.setCodi("SV002");

        List<ServeiDto> result = dtoMappingHelper.convertirList(List.of(s1, s2), ServeiDto.class);

        assertEquals(2, result.size());
        assertEquals("SV001", result.get(0).getCodi());
        assertEquals("SV002", result.get(1).getCodi());
    }

    @Test
    public void convertirSet_conjuntDeServeis_mapejaTots() {
        Servei s1 = new Servei();
        s1.setCodi("SV001");

        Set<ServeiDto> result = dtoMappingHelper.convertirSet(Set.of(s1), ServeiDto.class);

        assertEquals(1, result.size());
        assertEquals("SV001", result.iterator().next().getCodi());
    }

    @Test
    public void pageEntities2pageDto_ambPageableNull_usaUnpaged() {
        Servei s1 = new Servei();
        s1.setCodi("SV001");
        Page<Servei> pageEntities = new PageImpl<>(List.of(s1));

        Page<ServeiDto> result = dtoMappingHelper.pageEntities2pageDto(pageEntities, ServeiDto.class, null);

        assertTrue(result.getPageable().isUnpaged());
        assertEquals(1, result.getTotalElements());
        assertEquals("SV001", result.getContent().get(0).getCodi());
    }

    @Test
    public void pageEntities2pageDto_ambPageable_preservaPaginacioITotal() {
        Servei s1 = new Servei();
        s1.setCodi("SV001");
        PageRequest pageable = PageRequest.of(2, 5);
        Page<Servei> pageEntities = new PageImpl<>(List.of(s1), pageable, 42);

        Page<ServeiDto> result = dtoMappingHelper.pageEntities2pageDto(pageEntities, ServeiDto.class, pageable);

        assertEquals(42, result.getTotalElements());
        assertEquals(2, result.getPageable().getPageNumber());
    }

    @Test
    public void getMapperFacade_noEsNull() {
        assertNotNull(dtoMappingHelper.getMapperFacade());
    }
}
