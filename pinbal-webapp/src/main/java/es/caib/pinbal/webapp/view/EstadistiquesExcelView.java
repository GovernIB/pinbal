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
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.webapp.command.EstadistiquesFiltreCommand;

/**
 * Vista per a generar els fitxers amb format Excel de les estadístiques.
 * 
 * @author Josep Gayà
 */
public class EstadistiquesExcelView extends AbstractExcelView implements MessageSourceAware {

	private static final boolean INCLOURE_SUBTOTALS = false;

	private MessageSource messageSource;



	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=estadistiques.xls");
		EstadistiquesFiltreCommand command = (EstadistiquesFiltreCommand)model.get("estadistiquesFiltreCommand");
		HSSFSheet sheet = workbook.createSheet(
				getMessage(
						request,
						"estadistiques.excel.fulla.titol"));
		int filaInicial = 0;
		int columnaInicial = 0;
		List<EstadisticaDto> estadistiques = (List<EstadisticaDto>)model.get("estadistiques");
		if (estadistiques != null) {
			// Estadístiques d'una única entitat
			taulaEstadistiquesEntitat(
					estadistiques,
					command,
					null,
					request,
					workbook,
					sheet,
					filaInicial,
					columnaInicial);
		} else {
			Map<EntitatDto, List<EstadisticaDto>> estadistiquesPerEntitat = (Map<EntitatDto, List<EstadisticaDto>>)model.get("estadistiquesPerEntitat");
			if (estadistiquesPerEntitat != null) {
				// Estadístiques de totes les entitats i totals
				int filaActual = filaInicial;
				for (EntitatDto entitat: estadistiquesPerEntitat.keySet()) {
					// Taula d'estadístiques per a cada entitat
					if (entitat != null) {
						filaActual = taulaEstadistiquesEntitat(
								estadistiquesPerEntitat.get(entitat),
								command,
								getMessage(
										request,
										"estadistiques.list.entitat") + ": " + entitat.getNom(),
								request,
								workbook,
								sheet,
								filaActual,
								columnaInicial);
						filaActual += 2;
					}
				}
				// Taula d'estadístiques global
				taulaEstadistiquesEntitat(
						estadistiquesPerEntitat.get(null),
						command,
						getMessage(
								request,
								"estadistiques.list.totals"),
						request,
						workbook,
						sheet,
						filaActual,
						columnaInicial);
			}
		}
		autoSize(sheet, 6);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private int taulaEstadistiquesEntitat(
			List<EstadisticaDto> estadistiques,
			EstadistiquesFiltreCommand command,
			String entitatTitol,
			HttpServletRequest request,
			HSSFWorkbook workbook,
			HSSFSheet sheet,
			int filaInicial,
			int columnaInicial) {
		// Configuració dels estils
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
		HSSFCellStyle contingutTextStyle = workbook.createCellStyle();
		contingutTextStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		contingutTextStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		HSSFFont contingutFont = workbook.createFont();
		contingutFont.setFontName("Arial");
		contingutFont.setFontHeightInPoints((short)10);
		contingutTextStyle.setFont(contingutFont);
		HSSFCellStyle totalStyle = workbook.createCellStyle();
		totalStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		HSSFFont totalFont = workbook.createFont();
		totalFont.setFontName("Arial");
		totalFont.setFontHeightInPoints((short)10);
		totalStyle.setFont(totalFont);
		// Capsalera d'entitat
		if (entitatTitol != null) {
			HSSFRow capsaleraEntitat = sheet.createRow(filaInicial);
			HSSFCell entitatCell = capsaleraEntitat.createCell(columnaInicial);
			entitatCell.setCellStyle(capsaleraEntitatStyle);
			entitatCell.setCellValue(entitatTitol);
			sheet.addMergedRegion(
					new CellRangeAddress(
							filaInicial,
							filaInicial,
							columnaInicial,
							columnaInicial + 6));
			capsaleraEntitat.setHeight((short)0);
		}
		// Capsalera de la taula
		HSSFRow capsalera0 = sheet.createRow(filaInicial + 1);
		HSSFRow capsalera1 = sheet.createRow(filaInicial + 2);
		if (command.getAgrupacio().equals(EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI)) {
			HSSFCell procedimentCell = capsalera0.createCell(columnaInicial);
			procedimentCell.setCellStyle(capsaleraStyle);
			capsalera1.createCell(columnaInicial).setCellStyle(capsaleraStyle);
			procedimentCell.setCellValue(
					getMessage(
							request,
							"estadistiques.list.taula.procediment"));
			sheet.addMergedRegion(
					new CellRangeAddress(
							filaInicial + 1,
							filaInicial + 2,
							columnaInicial,
							columnaInicial));
			HSSFCell serveiCell = capsalera0.createCell(columnaInicial + 1);
			serveiCell.setCellStyle(capsaleraStyle);
			capsalera1.createCell(columnaInicial + 1).setCellStyle(capsaleraStyle);
			serveiCell.setCellValue(
					getMessage(
							request,
							"estadistiques.list.taula.servei"));
			sheet.addMergedRegion(
					new CellRangeAddress(
							filaInicial + 1,
							filaInicial + 2,
							columnaInicial + 1,
							columnaInicial + 1));
		} else {
			HSSFCell serveiCell = capsalera0.createCell(columnaInicial);
			serveiCell.setCellStyle(capsaleraStyle);
			capsalera1.createCell(columnaInicial).setCellStyle(capsaleraStyle);
			serveiCell.setCellValue(
					getMessage(
							request,
							"estadistiques.list.taula.servei"));
			sheet.addMergedRegion(
					new CellRangeAddress(
							filaInicial + 1,
							filaInicial + 2,
							columnaInicial,
							columnaInicial));
			HSSFCell procedimentCell = capsalera0.createCell(columnaInicial + 1);
			procedimentCell.setCellStyle(capsaleraStyle);
			capsalera1.createCell(columnaInicial + 1).setCellStyle(capsaleraStyle);
			procedimentCell.setCellValue(
					getMessage(
							request,
							"estadistiques.list.taula.procediment"));
			sheet.addMergedRegion(
					new CellRangeAddress(
							filaInicial + 1,
							filaInicial + 2,
							columnaInicial + 1,
							columnaInicial + 1));
		}
		HSSFCell tramitadesCell = capsalera0.createCell(columnaInicial + 2);
		tramitadesCell.setCellStyle(capsaleraStyle);
		tramitadesCell.setCellValue(
				getMessage(
						request,
						"estadistiques.list.taula.tramitades"));
		sheet.addMergedRegion(
				new CellRangeAddress(
						filaInicial + 1,
						filaInicial + 1,
						columnaInicial + 2,
						columnaInicial + 3));
		HSSFCell errorsCell = capsalera0.createCell(columnaInicial + 4);
		errorsCell.setCellStyle(capsaleraStyle);
		errorsCell.setCellValue(
				getMessage(
						request,
						"estadistiques.list.taula.errors"));
		sheet.addMergedRegion(
				new CellRangeAddress(
						filaInicial + 1,
						filaInicial + 1,
						columnaInicial + 4,
						columnaInicial + 5));
		HSSFCell totalCell = capsalera0.createCell(columnaInicial + 6);
		totalCell.setCellStyle(capsaleraStyle);
		totalCell.setCellValue(
				getMessage(
						request,
						"estadistiques.list.taula.total"));
		sheet.addMergedRegion(
				new CellRangeAddress(
						filaInicial + 1,
						filaInicial + 2,
						columnaInicial + 6,
						columnaInicial + 6));
		HSSFCell tramitadesRecCell = capsalera1.createCell(columnaInicial + 2);
		tramitadesRecCell.setCellStyle(capsaleraStyle);
		tramitadesRecCell.setCellValue(
				getMessage(
						request,
						"estadistiques.list.taula.recobriment"));
		HSSFCell tramitadesWebCell = capsalera1.createCell(columnaInicial + 3);
		tramitadesWebCell.setCellStyle(capsaleraStyle);
		tramitadesWebCell.setCellValue(
				getMessage(
						request,
						"estadistiques.list.taula.web"));
		HSSFCell errorsRecCell = capsalera1.createCell(columnaInicial + 4);
		errorsRecCell.setCellStyle(capsaleraStyle);
		errorsRecCell.setCellValue(
				getMessage(
						request,
						"estadistiques.list.taula.recobriment"));
		HSSFCell errorsWebCell = capsalera1.createCell(columnaInicial + 5);
		errorsWebCell.setCellStyle(capsaleraStyle);
		errorsWebCell.setCellValue(
				getMessage(
						request,
						"estadistiques.list.taula.web"));
		capsalera0.setHeight((short)0);
		capsalera1.setHeight((short)0);
		int rowIndex = 2;
		// Contigut de la taula
		if (estadistiques != null) {
			long subTotalRecOk = 0;
			long subTotalWebOk = 0;
			long subTotalRecError = 0;
			long subTotalWebError = 0;
			long subTotalTotal = 0;
			long totalRecOk = 0;
			long totalWebOk = 0;
			long totalRecError = 0;
			long totalWebError = 0;
			long totalTotal = 0;
			String agrupacioActual = null;
			int primeraFilaAgrupacioActual = 0;
			for (EstadisticaDto estadistica: estadistiques) {
				if (estadistica.isConteSumatori()) {
					// Mostra el subtotal
					if (rowIndex > 2) {
						if (INCLOURE_SUBTOTALS) {
							HSSFRow filaSumatori = sheet.createRow(filaInicial + 1 + rowIndex);
							filaSumatori.createCell(columnaInicial).setCellStyle(totalStyle);
							filaSumatori.createCell(columnaInicial + 1).setCellValue(
									getMessage(
											request,
											"estadistiques.list.taula.subtotal"));
							filaSumatori.getCell(columnaInicial + 1).setCellStyle(totalStyle);
							filaSumatori.createCell(columnaInicial + 2).setCellValue(subTotalRecOk);
							filaSumatori.getCell(columnaInicial + 2).setCellStyle(totalStyle);
							filaSumatori.createCell(columnaInicial + 3).setCellValue(subTotalWebOk);
							filaSumatori.getCell(columnaInicial + 3).setCellStyle(totalStyle);
							filaSumatori.createCell(columnaInicial + 4).setCellValue(subTotalRecError);
							filaSumatori.getCell(columnaInicial + 4).setCellStyle(totalStyle);
							filaSumatori.createCell(columnaInicial + 5).setCellValue(subTotalWebError);
							filaSumatori.getCell(columnaInicial + 5).setCellStyle(totalStyle);
							filaSumatori.createCell(columnaInicial + 6).setCellValue(subTotalTotal);
							filaSumatori.getCell(columnaInicial + 6).setCellStyle(totalStyle);
							rowIndex++;
						}
						sheet.addMergedRegion(
								new CellRangeAddress(
										primeraFilaAgrupacioActual,
										filaInicial + rowIndex + ((INCLOURE_SUBTOTALS) ? 1 : 0),
										columnaInicial,
										columnaInicial));
					}
				}
				// Mostra la fila d'estadístiques
				HSSFRow filaActual = sheet.createRow(filaInicial + 1 + rowIndex);
				if (command.getAgrupacio().equals(EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI)) {
					if (agrupacioActual == null || !agrupacioActual.equals(estadistica.getProcediment().getId().toString())) {
						primeraFilaAgrupacioActual = filaInicial + 1 + rowIndex;
						HSSFCell procCell = filaActual.createCell(columnaInicial + 0);
						procCell.setCellValue(estadistica.getProcediment().getNomAmbDepartament());
						procCell.setCellStyle(contingutTextStyle);
						agrupacioActual = estadistica.getProcediment().getId().toString();
					}
					HSSFCell serCell = filaActual.createCell(columnaInicial + 1);
					serCell.setCellValue(estadistica.getServeiNom());
					serCell.setCellStyle(contingutTextStyle);
				} else {
					if (agrupacioActual == null || !agrupacioActual.equals(estadistica.getServeiCodi())) {
						primeraFilaAgrupacioActual = filaInicial + 1 + rowIndex;
						HSSFCell serCell = filaActual.createCell(columnaInicial + 0);
						serCell.setCellValue(estadistica.getServeiNom());
						serCell.setCellStyle(contingutTextStyle);
						agrupacioActual = estadistica.getServeiCodi();
					}
					HSSFCell procCell = filaActual.createCell(columnaInicial + 1);
					procCell.setCellStyle(contingutTextStyle);
					procCell.setCellValue(estadistica.getProcediment().getNomAmbDepartament());
				}
				HSSFCell numRecOkCell = filaActual.createCell(columnaInicial + 2);
				numRecOkCell.setCellValue(estadistica.getNumRecobrimentOk());
				HSSFCell numWebOkCell = filaActual.createCell(columnaInicial + 3);
				numWebOkCell.setCellValue(estadistica.getNumWebUIOk());
				HSSFCell numRecErrCell = filaActual.createCell(columnaInicial + 4);
				numRecErrCell.setCellValue(estadistica.getNumRecobrimentError());
				HSSFCell numWebErrCell = filaActual.createCell(columnaInicial + 5);
				numWebErrCell.setCellValue(estadistica.getNumWebUIError());
				HSSFCell numTotCell = filaActual.createCell(columnaInicial + 6);
				numTotCell.setCellValue(estadistica.getNumTotal());
				rowIndex++;
				// Calculs per a total i subtotals
				if (estadistica.isConteSumatori()) {
					subTotalRecOk = estadistica.getSumatoriNumRecobrimentOk();
					subTotalWebOk = estadistica.getSumatoriNumWebUIOk();
					subTotalRecError = estadistica.getSumatoriNumRecobrimentError();
					subTotalWebError = estadistica.getSumatoriNumWebUIError();
					subTotalTotal = estadistica.getSumatoriNumTotal();
					totalRecOk += subTotalRecOk;
					totalWebOk += subTotalWebOk;
					totalRecError += subTotalRecError;
					totalWebError += subTotalWebError;
					totalTotal += subTotalTotal;
				}
			}
			if (rowIndex > 2) {
				// Mostra el darrer subtotal
				if (INCLOURE_SUBTOTALS) {
					HSSFRow filaSumatori = sheet.createRow(filaInicial + 1 + rowIndex);
					filaSumatori.createCell(columnaInicial + 0).setCellStyle(totalStyle);
					filaSumatori.createCell(columnaInicial + 1).setCellValue(
							getMessage(
									request,
									"estadistiques.list.taula.subtotal"));
					filaSumatori.getCell(columnaInicial + 1).setCellStyle(totalStyle);
					filaSumatori.createCell(columnaInicial + 2).setCellValue(subTotalRecOk);
					filaSumatori.getCell(columnaInicial + 2).setCellStyle(totalStyle);
					filaSumatori.createCell(columnaInicial + 3).setCellValue(subTotalWebOk);
					filaSumatori.getCell(columnaInicial + 3).setCellStyle(totalStyle);
					filaSumatori.createCell(columnaInicial + 4).setCellValue(subTotalRecError);
					filaSumatori.getCell(columnaInicial + 4).setCellStyle(totalStyle);
					filaSumatori.createCell(columnaInicial + 5).setCellValue(subTotalWebError);
					filaSumatori.getCell(columnaInicial + 5).setCellStyle(totalStyle);
					filaSumatori.createCell(columnaInicial + 6).setCellValue(subTotalTotal);
					filaSumatori.getCell(columnaInicial + 6).setCellStyle(totalStyle);
					rowIndex++;
				}
				sheet.addMergedRegion(
						new CellRangeAddress(
								primeraFilaAgrupacioActual,
								filaInicial + rowIndex + ((INCLOURE_SUBTOTALS) ? 1 : 0),
								columnaInicial,
								columnaInicial));
				// Mostra el total
				HSSFRow filaTotal = sheet.createRow(filaInicial + 1 + rowIndex);
				filaTotal.createCell(columnaInicial + 0).setCellStyle(totalStyle);
				filaTotal.createCell(columnaInicial + 1).setCellValue(
						getMessage(
								request,
								"estadistiques.list.taula.total"));
				filaTotal.getCell(columnaInicial + 1).setCellStyle(totalStyle);
				filaTotal.createCell(columnaInicial + 2).setCellValue(totalRecOk);
				filaTotal.getCell(columnaInicial + 2).setCellStyle(totalStyle);
				filaTotal.createCell(columnaInicial + 3).setCellValue(totalWebOk);
				filaTotal.getCell(columnaInicial + 3).setCellStyle(totalStyle);
				filaTotal.createCell(columnaInicial + 4).setCellValue(totalRecError);
				filaTotal.getCell(columnaInicial + 4).setCellStyle(totalStyle);
				filaTotal.createCell(columnaInicial + 5).setCellValue(totalWebError);
				filaTotal.getCell(columnaInicial + 5).setCellStyle(totalStyle);
				filaTotal.createCell(columnaInicial + 6).setCellValue(totalTotal);
				filaTotal.getCell(columnaInicial + 6).setCellStyle(totalStyle);
			}
		}
		return filaInicial + 1 + rowIndex;
	}

	private void autoSize(
			HSSFSheet sheet,
			int numCells) {
		sheet.setColumnWidth(0, 12000);
		sheet.setColumnWidth(1, 18000);
		/* El següent no funciona (bug?)
		/*for (int colNum = 0; colNum < numCells; colNum++)
			sheet.autoSizeColumn(colNum);*/
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
