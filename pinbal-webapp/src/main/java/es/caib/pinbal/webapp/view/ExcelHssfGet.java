package es.caib.pinbal.webapp.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHssfGet {

	/**
	 * This method is used to read the data's from an excel file.
	 * @param fileInputStream - Excel file in InputStream content.
	 * @throws IOException 
	 */
	public List<String[]> readExcelFileInputStream(InputStream fileInputStream, String version) throws IOException{
		
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
	
	private int getNumCols(Sheet sheet) {
		int maxCols = 0;
		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			maxCols = Math.max(maxCols, rowIterator.next().getLastCellNum());
		}
		return maxCols;
	}
	
	/**
	 * Devuelve un listado con el contenido del Excel pasado.
	 * @param byteArray. Excel con formato de byteArray.
	 * @return
	 * @throws IOException 
	 */
	public List<String[]> readExcelByteArray(byte[] byteArray) throws IOException{
		InputStream is = new ByteArrayInputStream(byteArray);
		List<String[]> llistat = readExcelFileInputStream(is, "2003");
		if (llistat.size() == 0){
			is = new ByteArrayInputStream(byteArray);
			llistat = readExcelFileInputStream(is, "2007");
		}
		
		return llistat;
	}
	
}
