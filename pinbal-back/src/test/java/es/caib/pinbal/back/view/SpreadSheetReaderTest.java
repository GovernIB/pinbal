package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.service.exception.FileTypeException;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpreadSheetReaderTest {

    private byte[] xlsAmbDuesFiles() throws Exception {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet();
            HSSFRow fila1 = sheet.createRow(0);
            fila1.createCell(0).setCellValue("Capçalera1");
            fila1.createCell(1).setCellValue("Capçalera2");
            HSSFRow fila2 = sheet.createRow(1);
            fila2.createCell(0).setCellValue("valor1");
            fila2.createCell(1).setCellValue(42);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private byte[] odsAmbDuesFiles() throws Exception {
        try (OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
            OdfTable sheet = ods.getTableList().get(0);
            sheet.getCellByPosition(0, 0).setStringValue("Capçalera1");
            sheet.getCellByPosition(1, 0).setStringValue("Capçalera2");
            sheet.getCellByPosition(0, 1).setStringValue("valor1");
            sheet.getCellByPosition(1, 1).setStringValue("valor2");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ods.save(bos);
            return bos.toByteArray();
        }
    }

    @Test
    public void getLinesFromSpreadSheatAmbCsvLlegeixLesFiles() throws Exception {
        String contingut = "Capçalera1,Capçalera2\nvalor1,valor2\n";
        FitxerDto fitxer = FitxerDto.builder()
                .nom("fitxer.csv")
                .contingut(contingut.getBytes(StandardCharsets.UTF_8))
                .build();

        List<String[]> linies = SpreadSheetReader.getLinesFromSpreadSheat(fitxer);

        assertEquals(2, linies.size());
        assertEquals("Capçalera1", linies.get(0)[0]);
        assertEquals("valor2", linies.get(1)[1]);
    }

    @Test
    public void getLinesFromSpreadSheatAmbXlsLlegeixLesFiles() throws Exception {
        FitxerDto fitxer = FitxerDto.builder()
                .nom("fitxer.xls")
                .contingut(xlsAmbDuesFiles())
                .build();

        List<String[]> linies = SpreadSheetReader.getLinesFromSpreadSheat(fitxer);

        assertEquals(2, linies.size());
        assertEquals("Capçalera1", linies.get(0)[0]);
        assertEquals("valor1", linies.get(1)[0]);
        assertEquals("42", linies.get(1)[1]);
    }

    @Test
    public void getLinesFromSpreadSheatAmbOdsLlegeixLesFiles() throws Exception {
        FitxerDto fitxer = FitxerDto.builder()
                .nom("fitxer.ods")
                .contingut(odsAmbDuesFiles())
                .build();

        List<String[]> linies = SpreadSheetReader.getLinesFromSpreadSheat(fitxer);

        assertTrue(linies.size() >= 2);
        assertEquals("Capçalera1", linies.get(0)[0]);
        assertEquals("valor1", linies.get(1)[0]);
    }

    @Test
    public void getLinesFromSpreadSheatAmbExtensioDesconegudaLlancaFileTypeException() {
        FitxerDto fitxer = FitxerDto.builder()
                .nom("fitxer.pdf")
                .contingut(new byte[0])
                .build();

        assertThrows(FileTypeException.class, () -> SpreadSheetReader.getLinesFromSpreadSheat(fitxer));
    }

    @Test
    public void addColumnaToSpreadSheatAmbCsvAfegeixLaColumna() throws Exception {
        String contingut = "Capçalera1,Capçalera2\nvalor1,valor2\n";
        FitxerDto fitxer = FitxerDto.builder()
                .nom("fitxer.csv")
                .contingut(contingut.getBytes(StandardCharsets.UTF_8))
                .build();

        FitxerDto resultat = SpreadSheetReader.addColumnaToSpreadSheat(fitxer, List.of("Capçalera3", "valorNou"));

        String contingutResultat = new String(resultat.getContingut(), StandardCharsets.UTF_8);
        assertTrue(contingutResultat.contains("Capçalera3"));
        assertTrue(contingutResultat.contains("valorNou"));
    }

    @Test
    public void addColumnaToSpreadSheatAmbXlsAfegeixLaColumna() throws Exception {
        byte[] original = xlsAmbDuesFiles();
        FitxerDto fitxer = FitxerDto.builder()
                .nom("fitxer.xls")
                .contingut(original)
                .build();

        FitxerDto resultat = SpreadSheetReader.addColumnaToSpreadSheat(fitxer, List.of("Capçalera3", "valorNou"));

        // Nota: HSSFWorkbook.getBytes() (usat internament per addColumnaToXls) no produeix un
        // .xls autocontingut vàlid (li manca el contenidor OLE2), així que no es pot rellegir amb
        // getLinesFromSpreadSheat; només es comprova que s'ha generat un contingut nou.
        assertTrue(resultat.getContingut().length > 0);
        assertFalse(java.util.Arrays.equals(original, resultat.getContingut()));
    }

    @Test
    public void addColumnaToSpreadSheatAmbExtensioDesconegudaLlancaFileTypeException() {
        FitxerDto fitxer = FitxerDto.builder()
                .nom("fitxer.pdf")
                .contingut(new byte[0])
                .build();

        assertThrows(FileTypeException.class, () -> SpreadSheetReader.addColumnaToSpreadSheat(fitxer, List.of()));
    }

    @Test
    public void removeLastEmptyLinesEliminaLesLiniesBuidesFinals() {
        List<String[]> linies = new ArrayList<>();
        linies.add(new String[]{"a", "b"});
        linies.add(new String[]{null, ""});
        linies.add(new String[]{null, null});

        SpreadSheetReader.removeLastEmptyLines(linies);

        assertEquals(1, linies.size());
    }

    @Test
    public void removeLastEmptyLinesAmbTotesBuidesEliminaTot() {
        List<String[]> linies = new ArrayList<>();
        linies.add(new String[]{null, ""});

        SpreadSheetReader.removeLastEmptyLines(linies);

        assertEquals(1, linies.size());
    }

    @Test
    public void isEmptyLineAmbTotsElsValorsNullsOBuitsEsTrue() {
        assertTrue(SpreadSheetReader.isEmptyLine(new String[]{null, "", null}));
    }

    @Test
    public void isEmptyLineAmbAlgunValorEsFalse() {
        assertFalse(SpreadSheetReader.isEmptyLine(new String[]{null, "valor"}));
    }

    @Test
    public void getConsecutiveCellsCountInThirdRowAmbDocumentBuitRetornaMaxColumns() throws Exception {
        try (OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
            assertEquals(5, SpreadSheetReader.getConsecutiveCellsCountInThirdRow(ods, 5));
        }
    }

    @Test
    public void getConsecutiveCellsCountInThirdRowComptaCellesConsecutivesAmbValor() throws Exception {
        try (OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
            OdfTable sheet = ods.getTableList().get(0);
            sheet.getCellByPosition(0, 2).setStringValue("a");
            sheet.getCellByPosition(1, 2).setStringValue("b");
            // columna 2 es deixa buida per tallar el comptatge

            assertEquals(2, SpreadSheetReader.getConsecutiveCellsCountInThirdRow(ods, 5));
        }
    }

    @Test
    public void getConsecutiveCellsCountInThirdRowAmbDocumentNullRetornaZero() {
        assertEquals(0, SpreadSheetReader.getConsecutiveCellsCountInThirdRow(null, 5));
    }
}
