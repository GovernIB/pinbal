package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.model.HistoricConsultaResource;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
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
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class HistoricConsultaResourceServiceImplTest {

    @Mock private HistoricConsultaService historicConsultaService;
    @Mock private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private HistoricConsultaResourceServiceImpl service;

    private ConsultaDto buildDto(Long id) {
        ConsultaDto dto = new ConsultaDto();
        dto.setId(id);
        dto.setScspPeticionId("PET-" + id);
        dto.setEntitatId(10L);
        dto.setEntitatNom("Entitat Test");
        dto.setJustificantEstat(ConsultaDto.JustificantEstat.OK);
        return dto;
    }

    @Test
    public void getOne_admin_usaFindOneAdmin() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
        when(historicConsultaService.findOneAdmin(1L)).thenReturn(buildDto(1L));

        HistoricConsultaResource resource = service.getOne(1L, null);

        assertEquals(1L, resource.getId());
        verify(historicConsultaService).findOneAdmin(1L);
    }

    @Test
    public void getOne_delegat_usaFindOneDelegatPerDefecte() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        when(historicConsultaService.findOneDelegat(4L)).thenReturn(buildDto(4L));

        HistoricConsultaResource resource = service.getOne(4L, null);

        assertEquals(4L, resource.getId());
        verify(historicConsultaService).findOneDelegat(4L);
    }

    @Test
    public void getOne_serveiLlancaExcepcio_esConverteixEnResourceNotFound() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        when(historicConsultaService.findOneDelegat(99L)).thenThrow(new RuntimeException("no trobat"));

        assertThrows(ResourceNotFoundException.class, () -> service.getOne(99L, null));
    }

    @Test
    public void findPage_superauditor_delegaAFindByFiltrePaginatPerSuperauditor() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(false);
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPERAUDIT)).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 10);
        when(historicConsultaService.findByFiltrePaginatPerSuperauditor(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(buildDto(1L)), pageable, 1));

        Page<HistoricConsultaResource> result = service.findPage(null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(historicConsultaService).findByFiltrePaginatPerSuperauditor(any(), any(), any());
    }

    @Test
    public void findPage_auditor_delegaAFindByFiltrePaginatPerAuditor() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(false);
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPERAUDIT)).thenReturn(false);
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_AUDIT)).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 10);
        when(historicConsultaService.findByFiltrePaginatPerAuditor(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(buildDto(1L)), pageable, 1));

        Page<HistoricConsultaResource> result = service.findPage(null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(historicConsultaService).findByFiltrePaginatPerAuditor(any(), any(), any());
    }

    @Test
    public void findPage_delegatMultiple_delegaAFindMultiplesByFiltrePaginatPerDelegat() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);
        when(historicConsultaService.findMultiplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(buildDto(5L)), pageable, 1));

        Page<HistoricConsultaResource> result = service.findPage(null, "multiple:'true'", null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(historicConsultaService).findMultiplesByFiltrePaginatPerDelegat(any(), any(), any());
    }

    @Test
    public void findPage_delegatSimple_delegaAFindSimplesByFiltrePaginatPerDelegat() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);
        when(historicConsultaService.findSimplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(buildDto(6L)), pageable, 1));

        Page<HistoricConsultaResource> result = service.findPage(null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(historicConsultaService).findSimplesByFiltrePaginatPerDelegat(any(), any(), any());
    }

    @Test
    public void findPage_serveiLlancaExcepcio_retornaPaginaBuida() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);
        when(historicConsultaService.findSimplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenThrow(new RuntimeException("error"));

        Page<HistoricConsultaResource> result = service.findPage(null, null, null, null, pageable);

        assertTrue(result.isEmpty());
    }
}
