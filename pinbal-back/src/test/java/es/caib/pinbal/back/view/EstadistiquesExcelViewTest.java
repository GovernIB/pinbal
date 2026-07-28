package es.caib.pinbal.back.view;

import es.caib.pinbal.back.command.EstadistiquesFiltreCommand;
import es.caib.pinbal.logic.intf.dto.EstadisticaDto;
import es.caib.pinbal.logic.intf.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EstadistiquesExcelViewTest {

    @Test
    public void buildExcelDocumentAgrupatPerProcedimentServeiAmbTotal() throws Exception {
        EstadistiquesExcelView view = new EstadistiquesExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        EstadistiquesFiltreCommand command = new EstadistiquesFiltreCommand();
        command.setAgrupacio(EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI);

        // Dos grups de 2 files (mateix procediment dins cada grup): la vista només combina en
        // una regió de cel·les els grups amb 2 o més files, i el darrer grup abans d'acabar la
        // llista també ha de tenir-ne 2 o més perquè la regió final sigui vàlida per a POI.
        EstadisticaDto fila1 = EstadisticaDto.builder()
                .procedimentId(1L)
                .procedimentNom("Procediment 1")
                .serveiNom("Servei 1")
                .numRecobrimentOk(3L)
                .numRecobrimentError(1L)
                .numWebUIOk(2L)
                .numWebUIError(0L)
                .build();
        EstadisticaDto fila2 = EstadisticaDto.builder()
                .procedimentId(1L)
                .procedimentNom("Procediment 1")
                .serveiNom("Servei 2")
                .numRecobrimentOk(1L)
                .build();
        EstadisticaDto fila3 = EstadisticaDto.builder()
                .procedimentId(2L)
                .procedimentNom("Procediment 2")
                .serveiNom("Servei 3")
                .numWebUIOk(4L)
                .build();
        EstadisticaDto fila4 = EstadisticaDto.builder()
                .procedimentId(2L)
                .procedimentNom("Procediment 2")
                .serveiNom("Servei 4")
                .numWebUIOk(1L)
                .build();

        Map<String, Object> model = new HashMap<>();
        model.put("estadistiquesFiltreCommand", command);
        model.put("estadistiques", List.of(fila1, fila2, fila3, fila4));

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=estadistiques.xls");
        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("estadistiques.list.taula.procediment", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("Procediment 1", sheet.getRow(3).getCell(0).getStringCellValue());
        assertEquals("Servei 1", sheet.getRow(3).getCell(1).getStringCellValue());
        assertEquals(3d, sheet.getRow(3).getCell(2).getNumericCellValue());
        assertEquals(6d, sheet.getRow(3).getCell(6).getNumericCellValue());
        // Segona fila del mateix grup: no repeteix el nom del procediment
        assertEquals("Servei 2", sheet.getRow(4).getCell(1).getStringCellValue());
        // Nou grup: torna a escriure el nom del procediment
        assertEquals("Procediment 2", sheet.getRow(5).getCell(0).getStringCellValue());
        // Fila de total final (cap fila conté sumatori, així que els totals resten a 0)
        assertEquals("estadistiques.list.taula.total", sheet.getRow(7).getCell(1).getStringCellValue());
        assertEquals(0d, sheet.getRow(7).getCell(2).getNumericCellValue());
        workbook.close();
    }
}
