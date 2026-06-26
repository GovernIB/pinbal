package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.intf.dto.ClauPrivadaDto;
import es.caib.pinbal.logic.intf.dto.EmissorCertDto;
import es.caib.pinbal.logic.intf.dto.OrganismeCessionariDto;
import es.caib.pinbal.logic.intf.dto.ParamConfDto;
import es.caib.pinbal.logic.intf.service.exception.EmissorCertNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ParamConfNotFoundException;
import es.caib.pinbal.persist.entity.ClauPrivada;
import es.caib.pinbal.persist.entity.EmissorCert;
import es.caib.pinbal.persist.entity.OrganismeCessionari;
import es.caib.pinbal.persist.entity.ParamConf;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.repository.ClauPublicaRepository;
import es.caib.pinbal.persist.repository.EmissorCertRepository;
import es.caib.pinbal.persist.repository.OrganismeCessionariRepository;
import es.caib.pinbal.persist.repository.ParamConfRepository;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ScspServiceImplTest {

    @Mock private ParamConfRepository paramConfRepository;
    @Mock private EmissorCertRepository emissorCertRepository;
    @Mock private ClauPrivadaRepository clauPrivadaRepository;
    @Mock private OrganismeCessionariRepository organismeCessionariRepository;
    @Mock private ClauPublicaRepository clauPublicaRepository;
    @Mock private EntitatClauHelper entitatClauHelper; // same package: es.caib.pinbal.logic.service
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private MapperFacade mapperFacade;

    @InjectMocks
    private ScspServiceImpl scspService;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
    }

    @Test
    public void findParamConfByNom_trobat_retornaDto() {
        ParamConf entity = mock(ParamConf.class);
        ParamConfDto dto = new ParamConfDto();
        when(paramConfRepository.findByNom("NomParam")).thenReturn(entity);
        when(mapperFacade.map(entity, ParamConfDto.class)).thenReturn(dto);

        ParamConfDto result = scspService.findParamConfByNom("NomParam");

        assertNotNull(result);
    }

    @Test
    public void createParamConf_guardaIRetornaDto() {
        ParamConfDto dto = new ParamConfDto();
        dto.setNom("Param1");
        dto.setValor("valor1");
        dto.setDescripcio("desc");
        ParamConf entity = mock(ParamConf.class);
        when(paramConfRepository.save(any())).thenReturn(entity);
        when(mapperFacade.map(entity, ParamConfDto.class)).thenReturn(dto);

        ParamConfDto result = scspService.createParamConf(dto);

        assertNotNull(result);
        verify(paramConfRepository).save(any(ParamConf.class));
    }

    @Test
    public void updateParamConf_noExisteix_llancaException() {
        ParamConfDto dto = new ParamConfDto();
        dto.setNom("Inexistent");
        when(paramConfRepository.findByNom("Inexistent")).thenReturn(null);

        assertThrows(ParamConfNotFoundException.class, () -> scspService.updateParamConf(dto));
    }

    @Test
    public void updateParamConf_existeix_actualitzaIRetornaDto() throws Exception {
        ParamConf entity = mock(ParamConf.class);
        ParamConfDto dto = new ParamConfDto();
        dto.setNom("Param1");
        dto.setValor("nouValor");
        when(paramConfRepository.findByNom("Param1")).thenReturn(entity);
        when(mapperFacade.map(entity, ParamConfDto.class)).thenReturn(dto);

        ParamConfDto result = scspService.updateParamConf(dto);

        assertNotNull(result);
        verify(entity).update(eq("nouValor"), any());
    }

    @Test
    public void findAllEmissorCert_retornaPage() {
        org.springframework.data.domain.Page<EmissorCert> page = new org.springframework.data.domain.PageImpl<>(
                Collections.singletonList(mock(EmissorCert.class)));
        when(emissorCertRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        EmissorCertDto dto = new EmissorCertDto();
        when(dtoMappingHelper.pageEntities2pageDto(page, EmissorCertDto.class, org.springframework.data.domain.Pageable.unpaged()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.singletonList(dto)));

        org.springframework.data.domain.Page<EmissorCertDto> result =
                scspService.findAllEmissorCert(org.springframework.data.domain.Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    public void findAllOrganismeCessionari_retornaLlista() {
        OrganismeCessionari entity = mock(OrganismeCessionari.class);
        OrganismeCessionariDto dto = new OrganismeCessionariDto();
        when(organismeCessionariRepository.findAll()).thenReturn(Collections.singletonList(entity));
        when(mapperFacade.mapAsList(anyList(), eq(OrganismeCessionariDto.class))).thenReturn(Collections.singletonList(dto));

        List<OrganismeCessionariDto> result = scspService.findAllOrganismeCessionari();

        assertEquals(1, result.size());
    }
}
