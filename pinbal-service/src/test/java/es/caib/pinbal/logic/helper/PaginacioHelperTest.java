package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.PaginaDto;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaginacioHelperTest {

    @Mock
    private DtoMappingHelper dtoMappingHelper;

    @InjectMocks
    private PaginacioHelper paginacioHelper;

    private PaginacioAmbOrdreDto paginacioSenseOrdre;
    private PaginacioAmbOrdreDto paginacioAmbOrdre;

    @BeforeEach
    public void setUp() {
        paginacioSenseOrdre = new PaginacioAmbOrdreDto();
        paginacioSenseOrdre.setPaginaNum(0);
        paginacioSenseOrdre.setPaginaTamany(10);

        paginacioAmbOrdre = new PaginacioAmbOrdreDto();
        paginacioAmbOrdre.setPaginaNum(0);
        paginacioAmbOrdre.setPaginaTamany(10);
        paginacioAmbOrdre.afegirOrdre("nom", PaginacioAmbOrdreDto.OrdreDireccioDto.ASCENDENT);
    }

    @Test
    public void esPaginacioActivada_tamanyPositiu_retornaTrue() {
        assertTrue(paginacioHelper.esPaginacioActivada(paginacioSenseOrdre));
    }

    @Test
    public void esPaginacioActivada_tamanyZero_retornaFalse() {
        PaginacioAmbOrdreDto dto = new PaginacioAmbOrdreDto();
        dto.setPaginaTamany(0);
        assertFalse(paginacioHelper.esPaginacioActivada(dto));
    }

    @Test
    public void toSpringDataPageable_senseOrdres_creaPagineble() {
        Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioSenseOrdre);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void toSpringDataPageable_ambOrdre_creaPaginebleAmbSort() {
        Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioAmbOrdre);
        assertNotNull(pageable.getSort());
        Sort.Order order = pageable.getSort().getOrderFor("nom");
        assertNotNull(order);
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    public void toSpringDataPageable_ordreDescendent_creaSortDesc() {
        PaginacioAmbOrdreDto dto = new PaginacioAmbOrdreDto();
        dto.setPaginaNum(0);
        dto.setPaginaTamany(5);
        dto.afegirOrdre("data", PaginacioAmbOrdreDto.OrdreDireccioDto.DESCENDENT);

        Pageable pageable = paginacioHelper.toSpringDataPageable(dto);
        Sort.Order order = pageable.getSort().getOrderFor("data");
        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    public void toSpringDataPageable_ambMapeig_usaPropietatMapeig() {
        Map<String, String[]> mapeig = new HashMap<>();
        mapeig.put("nom", new String[]{"entitat.nom"});

        Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioAmbOrdre, mapeig);
        Sort.Order order = pageable.getSort().getOrderFor("entitat.nom");
        assertNotNull(order);
    }

    @Test
    public void toSpringDataSort_ordresBuits_retornaNull() {
        PaginacioAmbOrdreDto dto = new PaginacioAmbOrdreDto();
        dto.setPaginaTamany(10);
        dto.setOrdres(Collections.emptyList());
        assertNull(paginacioHelper.toSpringDataSort(dto));
    }

    @Test
    public void toPaginaDto_paginaAmbContingut_mapejaCorrectament() {
        List<String> content = Arrays.asList("a", "b");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10), 2);
        when(dtoMappingHelper.convertirList(content, String.class)).thenReturn(content);

        PaginaDto<String> result = paginacioHelper.toPaginaDto(page, String.class);

        assertEquals(0, result.getNumero());
        assertEquals(10, result.getTamany());
        assertEquals(1, result.getTotal());
        assertEquals(2, result.getElementsTotal());
        assertTrue(result.isPrimera());
        assertTrue(result.isDarrera());
        assertFalse(result.isAnteriors());
        assertFalse(result.isPosteriors());
    }

    @Test
    public void toPaginaDto_ambConverter_usaConverter() {
        List<Integer> content = Arrays.asList(1, 2, 3);
        Page<Integer> page = new PageImpl<>(content, PageRequest.of(0, 10), 3);

        PaginaDto<String> result = paginacioHelper.toPaginaDto(page, String.class, (src) -> "num_" + src);

        assertEquals(3, result.getContingut().size());
        assertEquals("num_1", result.getContingut().get(0));
    }

    @Test
    public void toPaginaDto_llistatBuit_returnaPaginaDtoCorrectament() {
        when(dtoMappingHelper.convertirList(Collections.emptyList(), String.class)).thenReturn(Collections.emptyList());

        PaginaDto<String> result = paginacioHelper.toPaginaDto(Collections.emptyList(), String.class);

        assertEquals(0, result.getNumero());
        assertEquals(0, result.getTamany());
        assertEquals(1, result.getTotal());
        assertEquals(0, result.getElementsTotal());
    }

    @Test
    public void getPaginaDtoBuida_retornaPaginaBuida() {
        PaginaDto<String> result = paginacioHelper.getPaginaDtoBuida(String.class);
        assertEquals(0, result.getNumero());
        assertEquals(0, result.getTamany());
        assertEquals(1, result.getTotal());
        assertEquals(0, result.getElementsTotal());
        assertTrue(result.isPrimera());
        assertTrue(result.isDarrera());
    }
}
