package es.caib.pinbal.webapp.view;

import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.service.exception.FileTypeException;
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
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Column;
import org.odftoolkit.simple.table.Table;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                        case HSSFCell.CELL_TYPE_BLANK: cellTempList.add(""); break;
                        //case HSSFCell.CELL_TYPE_BOOLEAN: cellTempList.add(hssfCell.getBooleanCellValue()); break;
                        //case HSSFCell.CELL_TYPE_FORMULA: informacionFila.add(hssfCell.getStringCellValue()) ; break;
                        case HSSFCell.CELL_TYPE_NUMERIC: 
                            if(DateUtil.isCellDateFormatted(hssfCell)) {
                            	cellTempList.add(dateFormat.format(hssfCell.getDateCellValue()));
                            } else {
                            	cellTempList.add(String.valueOf((int)(hssfCell.getNumericCellValue())));
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING: cellTempList.add(hssfCell.getStringCellValue()) ; break;
                        default: cellTempList.add(hssfCell.getStringCellValue()); break;
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
                        case XSSFCell.CELL_TYPE_BLANK: cellTempList.add(""); break;
                        case XSSFCell.CELL_TYPE_NUMERIC:
                        	if(DateUtil.isCellDateFormatted(hssfCell)) {
                            	cellTempList.add(dateFormat.format(hssfCell.getDateCellValue()));
                            } else {
                            	cellTempList.add(String.valueOf((int)(hssfCell.getNumericCellValue())));
                            }
                            break;
                        case XSSFCell.CELL_TYPE_STRING: cellTempList.add(hssfCell.getStringCellValue()) ; break;
                        default: cellTempList.add(hssfCell.getStringCellValue()); break;
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

	private static List<String[]> getLinesFromOds (InputStream inputStream) throws Exception {
		List<String[]> cellDataList = new ArrayList<String[]>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		SpreadsheetDocument odsDocument = SpreadsheetDocument.loadDocument(inputStream);
		if (odsDocument != null && odsDocument.getTableList().size() > 0) {
			Table table = odsDocument.getTableList().get(0);
			for (org.odftoolkit.simple.table.Row row: table.getRowList()) {
				String line[] = new String[row.getCellCount()];
				for (int i = 0; i < row.getCellCount(); i++) {
					org.odftoolkit.simple.table.Cell cell = row.getCellByIndex(i);
					String cellType = cell.getValueType();
					String finalValue = null;
					if ("float".equalsIgnoreCase(cellType) || 
						"currency".equalsIgnoreCase(cellType) ||
						"percentage".equalsIgnoreCase(cellType)) {
						finalValue = String.valueOf(cell.getDoubleValue());
					} else if ("time".equalsIgnoreCase(cellType)) {
						finalValue = dateFormat.format(cell.getTimeValue().getTime());
					} else if ("date".equalsIgnoreCase(cellType)) {
						finalValue = dateFormat.format(cell.getDateValue().getTime());
					} else {
						finalValue = cell.getStringValue();
					}
					
					if (finalValue.isEmpty())
						finalValue = null;
					
					line[i] = finalValue;
				}
				cellDataList.add(line);
			}
		}
		
		return cellDataList;
	}

	private static byte[] addColumnaToOds(byte[] contingut, List<String> novaColumna) throws Exception {
		ByteArrayOutputStream outputStream = null;
		try {
			SpreadsheetDocument odsDocument = SpreadsheetDocument.loadDocument(new ByteArrayInputStream(contingut));
			if (odsDocument != null && odsDocument.getTableList().size() > 0) {
				Table table = odsDocument.getTableList().get(0);
				Column newColumn = table.appendColumn();
				int numFila = 0;
				for (String valor : novaColumna) {
					if (valor != null) {
						org.odftoolkit.simple.table.Cell cell = newColumn.getCellByIndex(numFila);
						String[] paragrafs = valor.split(SEPARADOR);
						for (int i = 0; i < paragrafs.length; i++) {
							cell.addParagraph(paragrafs[i]);
						}
					}
					numFila++;
				}
			}
			outputStream = new ByteArrayOutputStream();
			odsDocument.save(outputStream);
			return outputStream.toByteArray();
		} finally {
			outputStream.close();
		}
	}

	private static List<String[]> getLinesFromCsv (byte[] byteArray) throws Exception {
		return getLinesFromCsv(new ByteArrayInputStream(byteArray));
	}

	private static List<String[]> getLinesFromCsv (InputStream inputStream) throws Exception {
		ICsvListReader listReader = null;
		List<String[]> cellDataList = new ArrayList<String[]>();
		try {
			Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
            List<String> linia;
            while( (linia = listReader.read()) != null ) {
            	cellDataList.add(linia.toArray(new String[]{}));
            }
		}
        finally {
            if( listReader != null )
            	listReader.close();
        }
		return cellDataList;
	}

	private static byte[] addColumnaToCsv(byte[] contingut, List<String> novaColumna) throws Exception {
		ICsvListReader csvReader = null;
		ICsvListWriter csvWriter = null;
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			Reader streamReader = new InputStreamReader(new ByteArrayInputStream(contingut), StandardCharsets.UTF_8);
			Writer streamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
			csvReader = new CsvListReader(streamReader, CsvPreference.STANDARD_PREFERENCE);
			csvWriter = new CsvListWriter(streamWriter, CsvPreference.STANDARD_PREFERENCE);
			List<String> fila;
			int numFila = 0;
			while ((fila = csvReader.read()) != null) {
				if (numFila >= 0 && novaColumna.size() > numFila) {
					String novaCela = novaColumna.get(numFila);
					if (novaCela != null) {
						fila.add(novaCela.replace(SEPARADOR, " | "));
					}
					csvWriter.write(fila);
				}
				numFila++;
			}
			csvWriter.flush();
			return outputStream.toByteArray();
		} finally {
			if(csvReader != null)
				csvReader.close();
			if (csvWriter != null)
				csvWriter.close();
			outputStream.close();
		}

	}

	public static List<String[]> getLinesFromSpreadSheat(FitxerDto fitxer) throws Exception {
		List<String[]> linies = new ArrayList<String[]>();
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
		return linies;
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

}
