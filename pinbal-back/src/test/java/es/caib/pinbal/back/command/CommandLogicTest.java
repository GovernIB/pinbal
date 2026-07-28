package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.AvisDto;
import es.caib.pinbal.logic.intf.dto.ClauPrivadaDto;
import es.caib.pinbal.logic.intf.dto.ClauPublicaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaFiltreDto;
import es.caib.pinbal.logic.intf.dto.EmisorDto;
import es.caib.pinbal.logic.intf.dto.EmissorCertDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.logic.intf.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.logic.intf.dto.IntegracioFiltreDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.dto.ParamConfDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.logic.intf.dto.ServeiBusDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.dto.ServeiJustificantCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiXsdDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.dto.regles.AccioEnum;
import es.caib.pinbal.logic.intf.dto.regles.ModificatEnum;
import es.caib.pinbal.logic.intf.dto.regles.ServeiReglaDto;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Els mètodes {@code asCommand}/{@code asDto} generats per Lombok NO conten a la cobertura de
 * jacoco (marcats {@code @lombok.Generated} i filtrats automàticament pel
 * {@code LombokGeneratedFilter} intern de jacoco). Aquest test es centra únicament en el codi
 * escrit a mà de cada classe {@code *Command}: els mètodes de conversió DTO/Command amb lògica
 * pròpia i els mètodes {@code eliminarEspaisCampsCerca}.
 */
public class CommandLogicTest {

    // ------------------------- conversions simples (smoke, delegen totalment al mapper) -------------------------

    @Test
    public void avisAsCommandIAsDto() {
        assertNotNull(AvisCommand.asCommand(new AvisDto()));
        assertNotNull(AvisCommand.asDto(new AvisCommand()));
    }

    @Test
    public void clauPrivadaAsCommandIAsDto() {
        assertNotNull(ClauPrivadaCommand.asCommand(new ClauPrivadaDto()));
        assertNotNull(ClauPrivadaCommand.asDto(new ClauPrivadaCommand()));
    }

    @Test
    public void clauPublicaAsCommandIAsDto() {
        assertNotNull(ClauPublicaCommand.asCommand(new ClauPublicaDto()));
        assertNotNull(ClauPublicaCommand.asDto(new ClauPublicaCommand()));
    }

    @Test
    public void emissorCertAsCommandIAsDto() {
        assertNotNull(EmissorCertCommand.asCommand(new EmissorCertDto()));
        assertNotNull(EmissorCertCommand.asDto(new EmissorCertCommand()));
    }

    @Test
    public void usuariAsCommandIAsDto() {
        assertNotNull(UsuariCommand.asCommand(new UsuariDto()));
        assertNotNull(UsuariCommand.asDto(new UsuariCommand()));
    }

    @Test
    public void serveiXsdAsCommandIAsDto() throws Exception {
        assertNotNull(ServeiXsdCommand.asCommand(new ServeiXsdDto()));
        assertNotNull(ServeiXsdCommand.asDto(new ServeiXsdCommand()));
    }

    @Test
    public void integracioFiltreAsCommandIAsDto() {
        assertNotNull(IntegracioFiltreCommand.asCommand(new IntegracioFiltreDto()));
        assertNotNull(IntegracioFiltreCommand.asDto(new IntegracioFiltreCommand()));
    }

    @Test
    public void consultaAsDto() {
        assertNotNull(ConsultaCommand.asDto(new ConsultaCommand()));
    }

    @Test
    public void serveiJustificantCampAsCommandIAsDto() {
        ServeiJustificantCampDto dto = new ServeiJustificantCampDto();
        assertNotNull(ServeiJustificantCampCommand.asCommand(dto));
        assertNotNull(ServeiJustificantCampCommand.asDto(new ServeiJustificantCampCommand()));
        assertEquals(1, ServeiJustificantCampCommand.toEntitatCommands(List.of(dto)).size());
    }

    @Test
    public void informeRepresentantFiltreAsDto() {
        assertNull(InformeRepresentantFiltreCommand.asDto(null));
        assertNotNull(InformeRepresentantFiltreCommand.asDto(new InformeRepresentantFiltreCommand()));
    }

    @Test
    public void entitatAsCommandIAsDtoITransformacioDeLlistes() {
        EntitatDto dto = new EntitatDto();
        dto.setId(1L);
        assertNotNull(EntitatCommand.asCommand(dto));
        assertNotNull(EntitatCommand.asDto(new EntitatCommand()));
        assertEquals(1, EntitatCommand.toEntitatCommands(List.of(dto)).size());
    }

    // ------------------------- conversions amb lògica pròpia -------------------------

    @Test
    public void configAsDtoConstrueixElDtoAmbElBuilder() {
        ConfigCommand command = new ConfigCommand();
        command.setKey("clau");
        command.setValue("valor");

        assertEquals("clau", command.asDto().getKey());
        assertEquals("valor", command.asDto().getValue());
    }

    @Test
    public void procedimentAsCommandAmbOrganGestorNulNoEstableixOrganGestorId() {
        ProcedimentDto dto = new ProcedimentDto();

        ProcedimentCommand command = ProcedimentCommand.asCommand(dto);

        assertNull(command.getOrganGestorId());
    }

    @Test
    public void procedimentAsCommandAmbOrganGestorEstableixOrganGestorId() {
        ProcedimentDto dto = new ProcedimentDto();
        OrganGestorDto organGestor = new OrganGestorDto();
        organGestor.setId(5L);
        dto.setOrganGestor(organGestor);

        ProcedimentCommand command = ProcedimentCommand.asCommand(dto);

        assertEquals(5L, command.getOrganGestorId());
        assertEquals(1, ProcedimentCommand.toProcedimentCommands(List.of(dto)).size());
    }

    @Test
    public void procedimentAsDtoSenseOrganGestorIdNoEstableixOrganGestor() {
        ProcedimentCommand command = new ProcedimentCommand();

        assertNull(command.asDto().getOrganGestor());
    }

    @Test
    public void procedimentAsDtoAmbOrganGestorIdEstableixOrganGestor() {
        ProcedimentCommand command = new ProcedimentCommand();
        command.setOrganGestorId(7L);

        assertEquals(7L, command.asDto().getOrganGestor().getId());
    }

    @Test
    public void serveiCampAsCommandAmbCampPareEstableixCampPareId() {
        ServeiCampDto dto = new ServeiCampDto();
        ServeiCampDto campPare = new ServeiCampDto();
        campPare.setId(9L);
        dto.setCampPare(campPare);

        ServeiCampCommand command = ServeiCampCommand.asCommand(dto);

        assertEquals(9L, command.getCampPareId());
        assertEquals(1, ServeiCampCommand.toEntitatCommands(List.of(dto)).size());
    }

    @Test
    public void serveiCampAsCommandSenseCampPareNoEstableixCampPareId() {
        assertNull(ServeiCampCommand.asCommand(new ServeiCampDto()).getCampPareId());
    }

    @Test
    public void serveiCampAsDtoAmbCampPareICamp2EstableixDadesRelacionades() {
        ServeiCampCommand command = new ServeiCampCommand();
        command.setCampPareId(3L);
        command.setValidacioDataCmpCamp2Id(4L);

        ServeiCampDto dto = ServeiCampCommand.asDto(command);

        assertEquals(3L, dto.getCampPare().getId());
        assertEquals(4L, dto.getValidacioDataCmpCamp2().getId());
    }

    @Test
    public void serveiCampAsDtoSenseCampPareNiCamp2NoEstableixRelacions() {
        ServeiCampDto dto = ServeiCampCommand.asDto(new ServeiCampCommand());

        assertNull(dto.getCampPare());
        assertNull(dto.getValidacioDataCmpCamp2());
    }

    @Test
    public void serveiCampGrupAsCommandIAsDto() {
        assertNotNull(ServeiCampGrupCommand.asCommand(new ServeiCampGrupDto()));
        assertNotNull(ServeiCampGrupCommand.asDto(new ServeiCampGrupCommand()));
    }

    @Test
    public void serveiCampGrupAsDtoAmbAjudaPerDefecteLaPosaANull() {
        ServeiCampGrupCommand command = new ServeiCampGrupCommand();
        command.setAjuda("<br>");

        assertNull(ServeiCampGrupCommand.asDto(command).getAjuda());
    }

    @Test
    public void serveiCampGrupAsDtoAmbAjudaRealLaConserva() {
        ServeiCampGrupCommand command = new ServeiCampGrupCommand();
        command.setAjuda("text d'ajuda");

        assertEquals("text d'ajuda", ServeiCampGrupCommand.asDto(command).getAjuda());
    }

    @Test
    public void serveiBusAsCommandAmbEntitatEstableixEntitatId() {
        ServeiBusDto dto = new ServeiBusDto();
        EntitatDto entitat = new EntitatDto();
        entitat.setId(11L);
        dto.setEntitat(entitat);

        assertEquals(11L, ServeiBusCommand.asCommand(dto).getEntitatId());
    }

    @Test
    public void serveiBusAsCommandSenseEntitatNoEstableixEntitatId() {
        assertNull(ServeiBusCommand.asCommand(new ServeiBusDto()).getEntitatId());
    }

    @Test
    public void serveiBusAsDtoAmbEntitatIdEstableixEntitat() {
        ServeiBusCommand command = new ServeiBusCommand();
        command.setEntitatId(12L);

        assertEquals(12L, ServeiBusCommand.asDto(command).getEntitat().getId());
    }

    @Test
    public void serveiBusAsDtoSenseEntitatIdNoEstableixEntitat() {
        assertNull(ServeiBusCommand.asDto(new ServeiBusCommand()).getEntitat());
    }

    @Test
    public void estadistiquesFiltreAsDtoAmbCommandNulRetornaNull() {
        assertNull(EstadistiquesFiltreCommand.asDto(null));
    }

    @Test
    public void estadistiquesFiltreAsDtoMapejaProcedimentIServei() {
        EstadistiquesFiltreCommand command = new EstadistiquesFiltreCommand();
        command.setProcediment(3L);
        command.setServei("SERV1");

        EstadistiquesFiltreDto dto = EstadistiquesFiltreCommand.asDto(command);

        assertEquals(3L, dto.getProcedimentId());
        assertEquals("SERV1", dto.getServeiCodi());
    }

    @Test
    public void estadistiquesFiltreAsDtoAmbServeiBuitNoEstableixServeiCodi() {
        EstadistiquesFiltreCommand command = new EstadistiquesFiltreCommand();
        command.setServei("");

        assertNull(EstadistiquesFiltreCommand.asDto(command).getServeiCodi());
    }

    @Test
    public void paramConfCommandForCreateMarcaForcreate() {
        assertTrue(ParamConfCommand.commandForCreate().isForcreate());
    }

    @Test
    public void paramConfAsCommandForUpdateMarcaNoForcreate() {
        assertFalse(ParamConfCommand.asCommandForUpdate(new ParamConfDto()).isForcreate());
    }

    @Test
    public void paramConfAsCommandIAsDto() {
        assertNotNull(ParamConfCommand.asCommand(new ParamConfDto()));
        assertNotNull(ParamConfCommand.asDto(new ParamConfCommand()));
    }

    @Test
    public void serveiReglaAsCommandIAsDtoAmbValorsBuits() {
        ServeiReglaDto dto = ServeiReglaDto.builder()
                .id(1L)
                .nom("Regla")
                .serveiId(2L)
                .modificat(ModificatEnum.CAMPS)
                .accio(AccioEnum.OCULTAR)
                .build();

        ServeiReglaCommand command = ServeiReglaCommand.asCommand(dto);

        assertEquals("Regla", command.getNom());
        assertNotNull(command.getModificatValor());
        assertNotNull(command.getAfectatValor());

        ServeiReglaDto tornat = ServeiReglaCommand.asDto(command);
        assertEquals("Regla", tornat.getNom());
        assertNotNull(tornat.getModificatValor());
    }

    @Test
    public void serveiReglaAsCommandIAsDtoAmbValors() {
        ServeiReglaDto dto = ServeiReglaDto.builder()
                .id(1L)
                .nom("Regla")
                .serveiId(2L)
                .modificatValor(new LinkedHashSet<>(List.of("A")))
                .afectatValor(new LinkedHashSet<>(List.of("B")))
                .build();

        ServeiReglaCommand command = ServeiReglaCommand.asCommand(dto);

        assertTrue(command.getModificatValor().contains("A"));
        assertTrue(command.getAfectatValor().contains("B"));

        ServeiReglaDto tornat = ServeiReglaCommand.asDto(command);
        assertTrue(tornat.getModificatValor().contains("A"));
        assertTrue(tornat.getAfectatValor().contains("B"));
    }

    @Test
    public void serveiGetDataDarreraActualitzacioStringAmbDataNullRetornaNull() {
        ServeiCommand command = new ServeiCommand();

        assertNull(command.getDataDarreraActualitzacioString());
    }

    @Test
    public void serveiGetDataDarreraActualitzacioStringAmbDataFormataLaData() {
        ServeiCommand command = new ServeiCommand();
        command.setDataDarreraActualitzacio(new java.util.Date(0));

        assertNotNull(command.getDataDarreraActualitzacioString());
    }

    @Test
    public void serveiAsCommandMapejaEmissorIClausIFitxersXsd() {
        ServeiDto dto = new ServeiDto();
        EmisorDto emisor = new EmisorDto();
        emisor.setCif("B00000000");
        dto.setScspEmisor(emisor);
        ClauPrivadaDto clauPrivada = new ClauPrivadaDto();
        clauPrivada.setAlies("clauFirma");
        dto.setScspClaveFirma(clauPrivada);
        ClauPublicaDto clauPublica = new ClauPublicaDto();
        clauPublica.setAlies("clauXifrat");
        dto.setScspClaveCifrado(clauPublica);
        ServeiXsdDto xsd = new ServeiXsdDto();
        dto.setFitxersXsd(List.of(xsd));

        ServeiCommand command = ServeiCommand.asCommand(dto);

        assertEquals("B00000000", command.getScspEmisor());
        assertEquals("clauFirma", command.getScspClaveFirma());
        assertEquals("clauXifrat", command.getScspClaveCifrado());
        assertEquals(1, command.getFitxersXsd().size());
    }

    @Test
    public void serveiAsCommandSenseEmissorNiClausNiFitxersXsdNoElsEstableix() {
        ServeiCommand command = ServeiCommand.asCommand(new ServeiDto());

        assertNull(command.getScspEmisor());
        assertNull(command.getScspClaveFirma());
        assertNull(command.getScspClaveCifrado());
    }

    @Test
    public void serveiAsDtoAmbCamposBuitsElsPosaANullIFitxersXsdBuitPerDefecte() {
        ServeiCommand command = new ServeiCommand();
        command.setPinbalEntitatTipus("");
        command.setScspEmisor("");
        command.setScspClaveFirma("");
        command.setScspClaveCifrado("");

        ServeiDto dto = ServeiCommand.asDto(command);

        assertNull(command.getPinbalEntitatTipus());
        assertNull(dto.getScspEmisor());
        assertNull(dto.getScspClaveFirma());
        assertNull(dto.getScspClaveCifrado());
        assertNotNull(dto.getFitxersXsd());
        assertTrue(dto.getFitxersXsd().isEmpty());
    }

    @Test
    public void serveiAsDtoAmbEmissorIClausIFitxersXsdElsMapeja() {
        ServeiCommand command = new ServeiCommand();
        command.setScspEmisor("B00000000");
        command.setScspClaveFirma("clauFirma");
        command.setScspClaveCifrado("clauXifrat");
        command.setFitxersXsd(List.of(new ServeiXsdDto()));

        ServeiDto dto = ServeiCommand.asDto(command);

        assertEquals("B00000000", dto.getScspEmisor().getCif());
        assertEquals("clauFirma", dto.getScspClaveFirma().getAlies());
        assertEquals("clauXifrat", dto.getScspClaveCifrado().getAlies());
        assertEquals(1, dto.getFitxersXsd().size());
    }

    @Test
    public void serveiAsXsdDtoDelegaEnConversioTipusHelper() throws Exception {
        assertNotNull(ServeiCommand.asXsdDto(new ServeiCommand()));
    }

    // ------------------------- eliminarEspaisCampsCerca -------------------------

    @Test
    public void consultaFiltreEliminarEspaisCampsCercaRetallaElsCamps() {
        ConsultaFiltreCommand command = ConsultaFiltreCommand.builder()
                .scspPeticionId(" PET1 ")
                .titularNom(" Nom ")
                .titularDocument(" Doc ")
                .funcionari(" Func ")
                .usuari(" Usuari ")
                .build();

        command.eliminarEspaisCampsCerca();

        assertEquals("PET1", command.getScspPeticionId());
        assertEquals("Nom", command.getTitularNom());
        assertEquals("Doc", command.getTitularDocument());
        assertEquals("Func", command.getFuncionari());
        assertEquals("Usuari", command.getUsuari());
    }

    @Test
    public void consultaFiltreEliminarEspaisCampsCercaAmbCampsNullNoFalla() {
        new ConsultaFiltreCommand().eliminarEspaisCampsCerca();
    }

    @Test
    public void consultaFiltreConstructorAmbEntitatIdFiltraDarrersTresMesos() {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand(5L);

        assertEquals(5L, command.getEntitatId());
        assertNotNull(command.getDataInici());
    }

    @Test
    public void consultaFiltreFiltrarDarrersMesosActualitzaDataInici() {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();

        command.filtrarDarrersMesos(2);

        assertNotNull(command.getDataInici());
    }

    @Test
    public void consultaFiltreUpdateDefaultDataIniciPassaDeNormalAHistoric() {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        command.filtrarDarrersMesos(3);
        java.util.Date dataNormal = command.getDataInici();

        command.updateDefaultDataInici(true);

        assertNotNull(command.getDataInici());
        assertFalse(dataNormal.equals(command.getDataInici()));
    }

    @Test
    public void consultaFiltreUpdateDefaultDataIniciPassaDeHistoricANormal() {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        command.filtrarDarrersMesos(9);

        command.updateDefaultDataInici(false);

        assertNotNull(command.getDataInici());
    }

    @Test
    public void consultaFiltreUpdateDefaultDataIniciAmbDataQueNoCoincideixNoLaCanvia() {
        ConsultaFiltreCommand command = new ConsultaFiltreCommand();
        java.util.Date dataOriginal = new java.util.Date(0);
        command.setDataInici(dataOriginal);

        command.updateDefaultDataInici(true);
        command.updateDefaultDataInici(false);

        assertEquals(dataOriginal, command.getDataInici());
    }

    @Test
    public void entitatFiltreEliminarEspaisCampsCercaRetallaElsCamps() {
        EntitatFiltreCommand command = EntitatFiltreCommand.builder()
                .codi(" C1 ")
                .nom(" Nom ")
                .cif(" CIF1 ")
                .unitatArrel(" U1 ")
                .build();

        command.eliminarEspaisCampsCerca();

        assertEquals("C1", command.getCodi());
        assertEquals("Nom", command.getNom());
        assertEquals("CIF1", command.getCif());
        assertEquals("U1", command.getUnitatArrel());
    }

    @Test
    public void organGestorFiltreConstructorIEliminarEspaisCampsCerca() {
        OrganGestorFiltreCommand command = new OrganGestorFiltreCommand(3L);
        command.setCodi(" C1 ");
        command.setNom(" Nom ");

        assertEquals(3L, command.getEntitatId());
        command.eliminarEspaisCampsCerca();
        assertEquals("C1", command.getCodi());
        assertEquals("Nom", command.getNom());
    }

    @Test
    public void procedimentFiltreEliminarEspaisCampsCercaRetallaElsCamps() {
        ProcedimentFiltreCommand command = ProcedimentFiltreCommand.builder()
                .codi(" C1 ")
                .nom(" Nom ")
                .departament(" Dept ")
                .codiSia(" SIA1 ")
                .build();

        command.eliminarEspaisCampsCerca();

        assertEquals("C1", command.getCodi());
        assertEquals("Nom", command.getNom());
        assertEquals("Dept", command.getDepartament());
        assertEquals("SIA1", command.getCodiSia());
    }

    @Test
    public void procedimentServeiPermisFiltreEliminarEspaisCampsCercaRetallaElsCamps() {
        ProcedimentServeiPermisFiltreCommand command = ProcedimentServeiPermisFiltreCommand.builder()
                .codi(" C1 ")
                .nif(" N1 ")
                .nom(" Nom ")
                .build();

        command.eliminarEspaisCampsCerca();

        assertEquals("C1", command.getCodi());
        assertEquals("N1", command.getNif());
        assertEquals("Nom", command.getNom());
    }

    @Test
    public void serveiFiltreEliminarEspaisCampsCercaRetallaElsCamps() {
        ServeiFiltreCommand command = ServeiFiltreCommand.builder()
                .codi(" C1 ")
                .descripcio(" Desc ")
                .scspVersionEsquema(" 1.0 ")
                .build();

        command.eliminarEspaisCampsCerca();

        assertEquals("C1", command.getCodi());
        assertEquals("Desc", command.getDescripcio());
        assertEquals("1.0", command.getScspVersionEsquema());
    }

    @Test
    public void usuariFiltreEliminarEspaisCampsCercaRetallaElsCamps() {
        UsuariFiltreCommand command = UsuariFiltreCommand.builder()
                .codi(" C1 ")
                .nif(" N1 ")
                .nom(" Nom ")
                .departament(" Dept ")
                .build();

        command.eliminarEspaisCampsCerca();

        assertEquals("C1", command.getCodi());
        assertEquals("N1", command.getNif());
        assertEquals("Nom", command.getNom());
        assertEquals("Dept", command.getDepartament());
    }

    // ------------------------- EntitatUsuariCommand -------------------------

    @Test
    public void entitatUsuariGetRolsIsetRolsFanRoundTripAmbTotsElsRols() {
        EntitatUsuariCommand command = new EntitatUsuariCommand();

        command.setRols("RDAP");

        assertTrue(command.isRolRepresentant());
        assertTrue(command.isRolDelegat());
        assertTrue(command.isRolAuditor());
        assertTrue(command.isRolAplicacio());
        assertEquals("RDAP", command.getRols());
    }

    @Test
    public void entitatUsuariSetRolsAmbCapRolBuidaTotsElsFlags() {
        EntitatUsuariCommand command = new EntitatUsuariCommand();

        command.setRols("");

        assertFalse(command.isRolRepresentant());
        assertEquals("", command.getRols());
    }

    @Test
    public void entitatUsuariAsCommandMapejaDadesDeLUsuari() {
        UsuariDto usuari = new UsuariDto();
        usuari.setCodi("U1");
        usuari.setNom("Usuari 1");
        usuari.setNif("12345678Z");
        usuari.setEmail("u1@test.com");
        EntitatUsuariDto dto = new EntitatUsuariDto(usuari, "Dept", false, true, false, true, false, true);

        EntitatUsuariCommand command = EntitatUsuariCommand.asCommand(dto, 9L);

        assertEquals(9L, command.getId());
        assertEquals("U1", command.getCodi());
        assertEquals("Usuari 1", command.getNom());
        assertEquals("12345678Z", command.getNif());
        assertTrue(command.isRolRepresentant());
        assertFalse(command.isRolDelegat());
        assertTrue(command.isRolAuditor());
        assertFalse(command.isRolAplicacio());
        assertFalse(command.isAfegir());
    }
}
