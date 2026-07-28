package es.caib.pinbal.back.datatables;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerSideRequestTest {

    private HttpServletRequest requestAmbColumnaIOrdre() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("draw")).thenReturn("1");
        when(request.getParameter("start")).thenReturn("10");
        when(request.getParameter("length")).thenReturn("5");
        when(request.getParameter("search[value]")).thenReturn("cerca");
        when(request.getParameter("search[regex]")).thenReturn("true");
        when(request.getParameter("order[0][column]")).thenReturn("0");
        when(request.getParameter("order[0][dir]")).thenReturn("asc");
        when(request.getParameter("columns[0][data]")).thenReturn("nom");
        when(request.getParameter("columns[0][name]")).thenReturn("nom");
        when(request.getParameter("columns[0][searchable]")).thenReturn("true");
        when(request.getParameter("columns[0][orderable]")).thenReturn("true");
        when(request.getParameter("columns[0][search][value]")).thenReturn("filtreCol");
        when(request.getParameter("columns[0][search][regex]")).thenReturn("false");
        return request;
    }

    @Test
    public void constructorParsejaDrawStartLengthISearch() {
        ServerSideRequest ssr = new ServerSideRequest(requestAmbColumnaIOrdre());

        assertEquals(1, ssr.getDraw());
        assertEquals(10, ssr.getStart());
        assertEquals(5, ssr.getLength());
        assertEquals("cerca", ssr.getSearch().getValue());
        assertTrue(ssr.getSearch().isRegex());
    }

    @Test
    public void constructorParsejaOrdreIColumnes() {
        ServerSideRequest ssr = new ServerSideRequest(requestAmbColumnaIOrdre());

        assertEquals(1, ssr.getOrder().size());
        assertEquals(0, ssr.getOrder().get(0).getColumn());
        assertEquals("asc", ssr.getOrder().get(0).getDir());

        assertEquals(1, ssr.getColumns().size());
        assertEquals("nom", ssr.getColumns().get(0).getData());
        assertTrue(ssr.getColumns().get(0).isSearchable());
        assertEquals("filtreCol", ssr.getColumns().get(0).getSearch().getValue());
    }

    @Test
    public void constructorSenseParametresDeixaValorsPerDefecte() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        ServerSideRequest ssr = new ServerSideRequest(request);

        assertEquals(0, ssr.getDraw());
        assertNull(ssr.getSearch());
        assertNull(ssr.getOrder());
        assertNull(ssr.getColumns());
    }

    @Test
    public void constructorAmbCercaBuidaNoCreaObjecteSearch() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("search[value]")).thenReturn("");

        ServerSideRequest ssr = new ServerSideRequest(request);

        assertNull(ssr.getSearch());
    }

    @Test
    public void toPageableConstrueixPageRequestAmbOrdre() {
        ServerSideRequest ssr = ServerSideRequest.builder()
                .start(10)
                .length(5)
                .order(List.of(ServerSideOrder.builder().column(0).dir("asc").build()))
                .columns(List.of(ServerSideColumn.builder().data("nom").build()))
                .build();

        PageRequest pageRequest = ssr.toPageable();

        assertEquals(2, pageRequest.getPageNumber());
        assertEquals(5, pageRequest.getPageSize());
        assertEquals(Sort.Direction.ASC, pageRequest.getSort().getOrderFor("nom").getDirection());
    }

    @Test
    public void getPaginacioDtoFromRequestCalculaPaginaIFiltres() {
        var dto = ServerSideRequest.getPaginacioDtoFromRequest(requestAmbColumnaIOrdre());

        assertEquals(2, dto.getPaginaNum());
        assertEquals(5, dto.getPaginaTamany());
        assertEquals("cerca", dto.getFiltre());
    }

    @Test
    public void getPaginacioDtoFromRequestAmbLengthMenysUnUsaMaxEnter() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("draw")).thenReturn("1");
        when(request.getParameter("start")).thenReturn("0");
        when(request.getParameter("length")).thenReturn("-1");

        var dto = ServerSideRequest.getPaginacioDtoFromRequest(request);

        assertEquals(Integer.MAX_VALUE, dto.getPaginaTamany());
    }
}
