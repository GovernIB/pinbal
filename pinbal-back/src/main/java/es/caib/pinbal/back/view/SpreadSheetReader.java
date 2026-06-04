package es.caib.pinbal.back.view;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.service.exception.FileTypeException;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SpreadSheetReader {

	public static final String SEPARADOR = "#&-&#";
	/**
	 * This method is used to read the data's from an excel file.
	 * @param fileInputStream - Excel file in InputStream content.
	 * @throws IOException 
	 */
	private static List<String[]> readExcelFileInputStream(InputStream fileInputStream, String version) throws IOException{
		
		List<String[]> cellDataList = new ArrayList<String[]>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		if ("2003".equals(version)){
			POIFSFileSystem fsFileSystem = null;
			try {
				fsFileSystem = new POIFSFileSystem(fileInputStream);
			} catch (Exception ex) {
				return cellDataList;
			}
			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);

			int minCols = getNumCols(hssfSheet);
			
			/**
			* Iterate the rows and cells of the spreadsheet
			* to get all the datas.
			*/
			Iterator<Row> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<Cell> iterator = hssfRow.cellIterator();
				List<String> cellTempList = new ArrayList<String>();
				int current = 0;
				int next = 1;
				while (iterator.hasNext()) {
					HSSFCell hssfCell = (HSSFCell) iterator.next();
					current = hssfCell.getColumnIndex();
					while (next <= current)  {
						cellTempList.add(null);
						next++;
					}
					switch(hssfCell.getCellType()){
                        case BLANK:
							cellTempList.add("");
							break;
                        case BOOLEAN:
							cellTempList.add(hssfCell.getBooleanCellValue() + "");
							break;
                        //case HSSFCell.CELL_TYPE_FORMULA: informacionFila.add(hssfCell.getStringCellValue()) ; break;
                        case NUMERIC:
                            if(DateUtil.isCellDateFormatted(hssfCell)) {
                            	cellTempList.add(dateFormat.format(hssfCell.getDateCellValue()));
                            } else {
                            	cellTempList.add(String.valueOf((int)(hssfCell.getNumericCellValue())));
                            }
                            break;
                        case STRING:
							cellTempList.add(hssfCell.getStringCellValue()) ;
							break;
                        default:
							cellTempList.add(hssfCell.getStringCellValue());
							break;
					}
					next++;	
				}
				while (next <= minCols)  {
					cellTempList.add(null);
					next++;
				}
				cellDataList.add(cellTempList.toArray(new String[]{}));
			}
		} else {
			XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream); 
			XSSFSheet hssfSheet = workBook.getSheetAt(0);
			
			int minCols = getNumCols(hssfSheet);
			
			/**
			* Iterate the rows and cells of the spreadsheet
			* to get all the datas.
			*/
			Iterator<Row> rowIterator = hssfSheet.rowIterator();

			while (rowIterator.hasNext()) {
				XSSFRow hssfRow = (XSSFRow) rowIterator.next();
				Iterator<Cell> iterator = hssfRow.cellIterator();
				List<String> cellTempList = new ArrayList<String>();
				int current = 0;
				int next = 1;
				while (iterator.hasNext()) {
					XSSFCell hssfCell = (XSSFCell) iterator.next();
					current = hssfCell.getColumnIndex();
					while (next <= current)  {
						cellTempList.add(null);
						next++;
					}
					switch(hssfCell.getCellType()){
                        case BLANK:
							cellTempList.add("");
							break;
						case BOOLEAN:
							cellTempList.add(hssfCell.getBooleanCellValue() + "");
							break;
                        case NUMERIC:
                        	if(DateUtil.isCellDateFormatted(hssfCell)) {
                            	cellTempList.add(dateFormat.format(hssfCell.getDateCellValue()));
                            } else {
                            	cellTempList.add(String.valueOf((int)(hssfCell.getNumericCellValue())));
                            }
                            break;
                        case STRING:
							cellTempList.add(hssfCell.getStringCellValue());
							break;
                        default:
							cellTempList.add(hssfCell.getStringCellValue());
							break;
					}
					next++;
				}
				while (next <= minCols)  {
					cellTempList.add(null);
					next++;
				}
				cellDataList.add(cellTempList.toArray(new String[]{}));
			}
		}
		
		return cellDataList;
	}
	
	private static int getNumCols(Sheet sheet) {
		int maxCols = 0;
		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			maxCols = Math.max(maxCols, rowIterator.next().getLastCellNum());
		}
		return maxCols;
	}
	
	/**
	 * Devuelve un listado con el contenido del Excel pasado.
	 * @param byteArray Excel con formato de byteArray.
	 * @return
	 * @throws IOException 
	 */
	private static List<String[]> getLinesFromXls(byte[] byteArray) throws IOException{
		InputStream is = new ByteArrayInputStream(byteArray);
		List<String[]> llistat = readExcelFileInputStream(is, "2003");
		if (llistat.size() == 0){
			is = new ByteArrayInputStream(byteArray);
			llistat = readExcelFileInputStream(is, "2007");
		}
		
		return llistat;
	}

	private static byte[] addColumnaToXls(byte[] contingut, List<String> novaColumna) throws Exception {
		POIFSFileSystem fsFileSystem = null;
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(contingut);
			fsFileSystem = new POIFSFileSystem(is);
		} catch (Exception ex) {
			return addColumnaToXlsx(contingut, novaColumna);
		} finally {
			is.close();
		}

		HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
		HSSFSheet sheet = workBook.getSheetAt(0);
		CellStyle cs = workBook.createCellStyle();
		cs.setWrapText(true);

		int numCols = getNumCols(sheet);
		int numFila = 0;
		for (String valor: novaColumna) {
			if (valor != null) {
				HSSFRow row = sheet.getRow(numFila);
				HSSFCell cell = row.createCell(numCols);
				cell.setCellValue(valor.replace(SEPARADOR, "\n"));
				cell.setCellStyle(cs);
				row.setHeight((short)-1);
			}
			numFila++;
		}
		sheet.autoSizeColumn(numCols);
		return workBook.getBytes();
	}

	private static byte[] addColumnaToXlsx(byte[] contingut, List<String> novaColumna) throws Exception {
		InputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			is = new ByteArrayInputStream(contingut);
			XSSFWorkbook workBook = new XSSFWorkbook(is);
			XSSFSheet sheet = workBook.getSheetAt(0);
			CellStyle cs = workBook.createCellStyle();
			cs.setWrapText(true);

			int numCols = getNumCols(sheet);
			int numFila = 0;
			for (String valor: novaColumna) {
				if (valor != null) {
					XSSFRow row = sheet.getRow(numFila);
					XSSFCell cell = row.createCell(numCols);
					cell.setCellValue(valor.replace(SEPARADOR, "\n"));
					cell.setCellStyle(cs);
				}
				numFila++;
			}
			bos = new ByteArrayOutputStream();
			workBook.write(bos);
			return bos.toByteArray();
		} finally {
			is.close();
			bos.close();
		}
	}

	private static List<String[]> getLinesFromOds (byte[] byteArray) throws Exception {
		return getLinesFromOds(new ByteArrayInputStream(byteArray));
	}

	private static List<String[]> getLinesFromOds(InputStream inputStream) throws Exception {
		List<String[]> cellDataList = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		OdfSpreadsheetDocument odsDocument = OdfSpreadsheetDocument.loadDocument(inputStream);
		OdfTable table = odsDocument.getTableList().get(0);

		int columns = table.getColumnCount();
		if (columns >= 255) {
			columns = getConsecutiveCellsCountInThirdRow(odsDocument, columns);
		}

		for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
			OdfTableRow row = table.getRowByIndex(rowIndex);
			String[] line = new String[columns];
			for (int i = 0; i < columns; i++) {
				String finalValue = null;
				try {
					OdfTableCell cell = row.getCellByIndex(i);
					String cellType = cell.getValueType();

					if ("float".equalsIgnoreCase(cellType)
							|| "currency".equalsIgnoreCase(cellType)
							|| "percentage".equalsIgnoreCase(cellType)) {
						finalValue = String.valueOf(cell.getDoubleValue());

					} else if ("time".equalsIgnoreCase(cellType)) {
						finalValue = dateFormat.format(cell.getTimeValue().getTime());

					} else if ("date".equalsIgnoreCase(cellType)) {
						finalValue = dateFormat.format(cell.getDateValue().getTime());

					} else if ("boolean".equalsIgnoreCase(cellType)) {
						finalValue = String.valueOf(cell.getBooleanValue());

					} else {
						finalValue = cell.getStringValue();
					}

					if (finalValue != null && finalValue.isEmpty()) {
						finalValue = null;
					}
				} catch (Exception ex) {}

				line[i] = finalValue;
			}
			cellDataList.add(line);
		}

		return cellDataList;
	}

	public static int getConsecutiveCellsCountInThirdRow(OdfSpreadsheetDocument odsDocument, int maxColumns) {
		if (odsDocument != null && !odsDocument.getTableList().isEmpty()) {
			OdfTable table = odsDocument.getTableList().get(0);
			if (table.getRowCount() < 3) {
				return maxColumns;
			}
			OdfTableRow thirdRow = table.getRowByIndex(2);
			int consecutiveCellsWithValue = 0;
			for (int i = 0; i < maxColumns; i++) {
				OdfTableCell cell = thirdRow.getCellByIndex(i);
				String currentValue = cell.getDisplayText();
				if (Strings.isEmpty(currentValue)) {
					break;
				}
				consecutiveCellsWithValue++;
			}
			return consecutiveCellsWithValue;
		}
		return 0;
	}

	private static byte[] addColumnaToOds(byte[] contingut, List<String> novaColumna) throws Exception {
		OdfSpreadsheetDocument odsDocument = OdfSpreadsheetDocument.loadDocument(
				new ByteArrayInputStream(contingut));

		if (odsDocument != null && !odsDocument.getTableList().isEmpty()) {
			OdfTable table = odsDocument.getTableList().get(0);
			int newColumnIndex = table.getColumnCount();
			table.appendColumn();

			for (int numFila = 0; numFila < novaColumna.size(); numFila++) {
				String valor = novaColumna.get(numFila);
				if (valor != null) {
					OdfTableCell cell = table.getCellByPosition(newColumnIndex, numFila);
					// odfdom no té addParagraph, construïm el text amb salts de línia
					String text = String.join("\n", valor.split(SEPARADOR));
					cell.setStringValue(text);
				}
			}
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		odsDocument.save(outputStream);
		return outputStream.toByteArray();
	}

	private static List<String[]> getLinesFromCsv (byte[] byteArray) throws Exception {
		return getLinesFromCsv(new ByteArrayInputStream(byteArray));
	}

	private static List<String[]> getLinesFromCsv(InputStream inputStream) throws Exception {
		try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		     CSVReader csvReader = new CSVReaderBuilder(reader)
					 .withCSVParser(new CSVParserBuilder()
							 .withSeparator(',')
							 .build())
					 .build()) {

			return csvReader.readAll();
		}
	}

	private static byte[] addColumnaToCsv(byte[] contingut, List<String> novaColumna) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try (Reader streamReader = new InputStreamReader(new ByteArrayInputStream(contingut), StandardCharsets.UTF_8);
		     Writer streamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
		     CSVReader csvReader = new CSVReaderBuilder(streamReader)
					 .withCSVParser(new CSVParserBuilder()
							 .withSeparator(',')
							 .build())
					 .build();
		     CSVWriter csvWriter = new CSVWriter(streamWriter,
					 CSVWriter.DEFAULT_SEPARATOR,
					 CSVWriter.NO_QUOTE_CHARACTER,
					 CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					 CSVWriter.DEFAULT_LINE_END)) {

			String[] fila;
			int numFila = 0;
			while ((fila = csvReader.readNext()) != null) {
				if (novaColumna.size() > numFila) {
					String novaCela = novaColumna.get(numFila);
					if (novaCela != null) {
						// Afegim la nova cel·la al array
						String[] filaAmpliada = Arrays.copyOf(fila, fila.length + 1);
						filaAmpliada[fila.length] = novaCela.replace(SEPARADOR, " | ");
						csvWriter.writeNext(filaAmpliada);
					} else {
						csvWriter.writeNext(fila);
					}
				}
				numFila++;
			}
		}

		return outputStream.toByteArray();
	}

	public static List<String[]> getLinesFromSpreadSheat(FitxerDto fitxer) throws Exception {
		List<String[]> linies;
		// Obtenir dades del fitxer
		if ("text/csv".equals(fitxer.getContentType()) || "csv".equals(fitxer.getExtensio())) {
			linies = getLinesFromCsv(fitxer.getContingut());
		} else if ("application/vnd.ms-excel".equals(fitxer.getContentType()) ||
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fitxer.getContentType())  ||
				"xls".equals(fitxer.getExtensio())) {
			linies = getLinesFromXls(fitxer.getContingut());
		} else if (
				"application/vnd.oasis.opendocument.spreadsheet".equals(fitxer.getContentType()) || "ods".equals(fitxer.getExtensio())) {
			linies = getLinesFromOds(fitxer.getContingut());
		} else {
			throw new FileTypeException();
		}
		if (linies != null)
			removeLastEmptyLines(linies);
		return linies;
	}

	static void removeLastEmptyLines(List<String[]> lines) {
		// Find the index of the last non-empty line
		int lastContentIndex = -1;
		for (int i = 0; i < lines.size(); i++) {
			if (!isEmptyLine(lines.get(i))) {
				lastContentIndex = i;
			}
		}

		// If there is at least one non-empty line,
		// remove all the lines after the last non-empty line
		if (lastContentIndex != -1) {
			lines.subList(lastContentIndex + 1, lines.size()).clear();
		}
	}

	static boolean isEmptyLine(String[] line) {
		for (String s : line) {
			if (s != null && !s.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static FitxerDto addColumnaToSpreadSheat(FitxerDto fitxer, List<String> novaColumna) throws Exception {
		byte[] contingutAmbErrors = null;
		if ("text/csv".equals(fitxer.getContentType()) || "csv".equals(fitxer.getExtensio())) {
			contingutAmbErrors = addColumnaToCsv(fitxer.getContingut(), novaColumna);
		} else if ("application/vnd.ms-excel".equals(fitxer.getContentType()) ||
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fitxer.getContentType())  ||
				"xls".equals(fitxer.getExtensio())) {
			contingutAmbErrors = addColumnaToXls(fitxer.getContingut(), novaColumna);
		} else if (
				"application/vnd.oasis.opendocument.spreadsheet".equals(fitxer.getContentType()) || "ods".equals(fitxer.getExtensio())) {
			contingutAmbErrors = addColumnaToOds(fitxer.getContingut(), novaColumna);
		} else {
			throw new FileTypeException();
		}
		return FitxerDto.builder()
				.nom(fitxer.getNom())
				.contentType(fitxer.getContentType())
				.contingut(contingutAmbErrors).build();
	}

	public static void main(String[] args){

		//Descomentar dependencies de dom4j i xerces al pom de pinbal-webapp
		String xls = ""; // Afegir ruta
		String xlsx = ""; // Afegir ruta
		String odt = ""; // Afegir ruta
		try {
			FileInputStream fis = new FileInputStream(xls);
			readExcelFileInputStream(fis, "2003");
			fis.close();
			FileInputStream fis2 = new FileInputStream(xlsx);
			readExcelFileInputStream(fis2, "2007");
			fis2.close();
			FileInputStream fis3 = new FileInputStream(odt);
			getLinesFromOds(fis3);
			fis3.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Fitxer no trobat ");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("Error lllegint el fitxer");
			ex.printStackTrace();
        } catch (Exception ex) {
			System.out.println("Error llegint el fitxer ods");
			ex.printStackTrace();
        }
    }

}
