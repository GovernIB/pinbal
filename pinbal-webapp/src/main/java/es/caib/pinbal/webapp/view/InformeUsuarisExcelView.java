/**
 * 
 */
package es.caib.pinbal.webapp.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;

/**
 * Vista per a generar l'informe de procediments.
 * 
 * @author Josep Gayà
 */
public class InformeUsuarisExcelView extends AbstractExcelView implements MessageSourceAware {

	private MessageSource messageSource;



	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=informeUsuaris.xls");
		List<InformeUsuariDto> informeDades = (List<InformeUsuariDto>)model.get("informeDades");
		HSSFSheet sheet = workbook.createSheet(
				getMessage(
						request,
						"informe.usuaris.excel.fulla.titol"));

		int filaInicial = 0;
		int columnaInicial = 0;

		HSSFCellStyle capsaleraEntitatStyle = workbook.createCellStyle();
		capsaleraEntitatStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		capsaleraEntitatStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont entitatFont = workbook.createFont();
		entitatFont.setFontName("Arial");
		entitatFont.setFontHeightInPoints((short)16);
		entitatFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		capsaleraEntitatStyle.setFont(entitatFont);
		HSSFCellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		capsaleraStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont capsaleraFont = workbook.createFont();
		capsaleraFont.setFontName("Arial");
		capsaleraFont.setFontHeightInPoints((short)10);
		capsaleraFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		capsaleraStyle.setFont(capsaleraFont);
		HSSFCellStyle departamentStyle = workbook.createCellStyle();
		departamentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFFont departamentFont = workbook.createFont();
		departamentFont.setFontName("Arial");
		departamentFont.setFontHeightInPoints((short)12);
		departamentFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		departamentStyle.setFont(departamentFont);

		int rowIndex = 1;
		int totalUsuarisEntitat = 0;
		EntitatDto entitatActual = null;
		HSSFCell entitatActualCell = null;
		int subtotalDepartament = 0;
		String departamentActual = null;
		HSSFCell departamentActualCell = null;
		for (InformeUsuariDto informeDada: informeDades) {
			if (entitatActual == null || !entitatActual.getId().equals(informeDada.getEntitat().getId())) {
				if (entitatActualCell != null) {
					entitatActualCell.setCellValue(entitatActual.getNom() + " (" + totalUsuarisEntitat + ")");
					totalUsuarisEntitat = 0;
				}
				entitatActual = informeDada.getEntitat();
				int filaEntitat = filaInicial + rowIndex++;
				HSSFRow capsaleraEntitat = sheet.createRow(filaEntitat);
				entitatActualCell = capsaleraEntitat.createCell(columnaInicial);
				entitatActualCell.setCellStyle(capsaleraEntitatStyle);
				entitatActualCell.setCellValue(entitatActual.getNom());
				sheet.addMergedRegion(
						new CellRangeAddress(
								filaEntitat,
								filaEntitat,
								columnaInicial,
								columnaInicial + 3));
				capsaleraEntitat.setHeight((short)0);
				insertarValorDepartament(
						request,
						departamentActualCell,
						departamentActual,
						subtotalDepartament);
				subtotalDepartament = 0;
				departamentActual = null;
				departamentActualCell = null;
			}
			if (	(departamentActual == null && departamentActualCell == null) ||
					(departamentActual != null && informeDada.getDepartament() == null) ||
					(departamentActual == null && informeDada.getDepartament() != null) ||
					(departamentActual != null && !departamentActual.equals(informeDada.getDepartament()))) {
				insertarValorDepartament(
						request,
						departamentActualCell,
						departamentActual,
						subtotalDepartament);
				if (departamentActualCell != null)
					subtotalDepartament = 0;
				departamentActual = informeDada.getDepartament();
				int filaDepartament = filaInicial + rowIndex++;
				HSSFRow capsaleraDepartament = sheet.createRow(filaDepartament);
				departamentActualCell = capsaleraDepartament.createCell(columnaInicial);
				departamentActualCell.setCellStyle(departamentStyle);
				//departamentActualCell.setCellValue(departamentActual);
				sheet.addMergedRegion(
						new CellRangeAddress(
								filaDepartament,
								filaDepartament,
								columnaInicial,
								columnaInicial + 2));
				capsaleraDepartament.setHeight((short)0);
				insertarFilaCapsalera(
						request,
						sheet,
						capsaleraStyle,
						rowIndex++,
						columnaInicial);
			}
			HSSFRow filaDada = sheet.createRow(filaInicial + rowIndex++);
			HSSFCell dadaCodiCell = filaDada.createCell(columnaInicial);
			dadaCodiCell.setCellValue(informeDada.getCodi());
			HSSFCell dadaNomCell = filaDada.createCell(columnaInicial + 1);
			dadaNomCell.setCellValue(informeDada.getNom());
			HSSFCell dadaNifCell = filaDada.createCell(columnaInicial + 2);
			dadaNifCell.setCellValue(informeDada.getNif());
			totalUsuarisEntitat++;
			subtotalDepartament++;
		}
		if (entitatActualCell != null) {
			entitatActualCell.setCellValue(entitatActual.getNom() + " (" + totalUsuarisEntitat + ")");
		}
		insertarValorDepartament(
				request,
				departamentActualCell,
				departamentActual,
				subtotalDepartament);
		autoSize(sheet, 3);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private void insertarFilaCapsalera(
			HttpServletRequest request,
			HSSFSheet sheet,
			HSSFCellStyle style,
			int filaInicial,
			int columnaInicial) {
		HSSFRow titolsColumna = sheet.createRow(filaInicial);
		HSSFCell codiCell = titolsColumna.createCell(columnaInicial);
		codiCell.setCellStyle(style);
		codiCell.setCellValue(
				getMessage(
						request,
						"informe.usuaris.excel.columna.codi"));
		HSSFCell nomCell = titolsColumna.createCell(columnaInicial + 1);
		nomCell.setCellStyle(style);
		nomCell.setCellValue(
				getMessage(
						request,
						"informe.usuaris.excel.columna.nom"));
		HSSFCell nifCell = titolsColumna.createCell(columnaInicial + 2);
		nifCell.setCellStyle(style);
		nifCell.setCellValue(
				getMessage(
						request,
						"informe.usuaris.excel.columna.nif"));
	}

	private void insertarValorDepartament(
			HttpServletRequest request,
			HSSFCell cell,
			String departamentActual,
			int subtotal) {
		if (cell != null) {
			if (departamentActual != null)
				cell.setCellValue(
						getMessage(
								request,
								"informe.procediments.excel.departament") +
						": " + departamentActual + " (" + subtotal + ")");
			else
				cell.setCellValue(
						getMessage(
								request,
								"informe.procediments.excel.sense.departament") +
						" (" + subtotal + ")");
		}
	}

	private void autoSize(
			HSSFSheet sheet,
			int numCells) {
		for (int colNum = 0; colNum <= numCells; colNum++)
			sheet.autoSizeColumn(colNum);
	}

	private String getMessage(
			HttpServletRequest request,
			String key) {
		String message = messageSource.getMessage(
				key,
				null,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}

}
