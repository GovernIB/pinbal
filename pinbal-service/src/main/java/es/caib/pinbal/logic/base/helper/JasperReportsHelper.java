package es.caib.pinbal.logic.base.helper;

import es.caib.pinbal.logic.intf.base.exception.ReportGenerationException;
import es.caib.pinbal.logic.intf.base.model.*;
import es.caib.pinbal.logic.intf.base.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.FieldBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.expression.JasperExpression;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.definition.datatype.DRIDataType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 * Generació d'informes amb Jasper Reports.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
public class JasperReportsHelper {

	private static final String EXPORT_REPORT_PATH = "/export_report.jrxml";

	public DownloadableFile export(
			Class<?> resourceClass,
			List<?> resultats,
			ExportField[] fields,
			ReportFileType fileType,
			OutputStream out) throws ReportGenerationException {
		try {
			InputStream is = getClass().getResourceAsStream("/dynamic_template.jrxml");
			ComponentBuilder<?, ?> dynamicReportsComponent = cmp.horizontalList(cmp.image(getClass().getResource("/logo192.png")).setFixedDimension(60, 60),
					cmp.verticalList(
							cmp.text("DynamicReports").setStyle(ReportTemplateStyle.bold22CenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
							cmp.text("http://www.dynamicreports.org").setStyle(ReportTemplateStyle.italicStyle))).
					setFixedWidth(300);
			JasperReportBuilder reportBuilder = DynamicReports.report().
					setParameter("net.sf.jasperreports.export.xlsx.exclude.origin.band.1","columnHeader").
					/*setParameter("net.sf.jasperreports.export.xls.one.page.per.sheet", "true").
					setParameter("net.sf.jasperreports.export.xlsx.one.page.per.sheet", "true").
					setParameter("net.sf.jasperreports.export.ods.one.page.per.sheet", "true").*/
					setTemplate(ReportTemplateStyle.reportTemplate).
					setTemplateDesign(is).
					title(cmp.horizontalList()
							.add(dynamicReportsComponent, cmp.text("Export").setStyle(ReportTemplateStyle.bold18CenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
							.newRow()
							.add(cmp.line())
							.newRow()
							.add(cmp.verticalGap(10))).
					columnHeader();
			reportBuilder.fields(getReportFields(resourceClass, fields));
			reportBuilder.columns(getReportColumns(resourceClass, fields));
			JRDataSource dataSource = new JRBeanCollectionDataSource(resultats);
			printJrxml(reportBuilder);
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					reportBuilder.toJasperReport(),
					reportBuilder.getJasperParameters(),
					dataSource);
			return generateDownloadableFile(
					resourceClass,
					jasperPrint,
					fileType,
					out);
		} catch (DRException | JRException ex) {
			throw new ReportGenerationException(
					resourceClass,
					"Couldn't generate export file",
					ex);
		}
	}

	public DownloadableFile generate(
			Class<?> resourceClass,
			String code,
			URL reportUrl,
			List<?> data,
			Locale locale,
			String i18nResourceBundlePath,
			ReportFileType fileType,
			OutputStream out) {
		try {
			JasperReport jasperReport = getJasperReportCompiled(reportUrl);
			Map<String, Object> jasperReportParams = new HashMap<>();
			if (locale != null) {
				jasperReportParams.put(JRParameter.REPORT_LOCALE, locale);
			}
			if (i18nResourceBundlePath != null) {
				jasperReportParams.put(
						JRParameter.REPORT_RESOURCE_BUNDLE,
						(locale != null) ? ResourceBundle.getBundle(i18nResourceBundlePath, locale) : ResourceBundle.getBundle(i18nResourceBundlePath));
			}
			JRDataSource dataSource = new JRBeanCollectionDataSource(data);
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport,
					jasperReportParams,
					dataSource);
			return generateDownloadableFile(
					resourceClass,
					jasperPrint,
					fileType,
					out);
		} catch (JRException | IOException ex) {
			throw new ReportGenerationException(
					resourceClass,
					null,
					code,
					"Couldn't generate export file",
					ex);
		}
	}

	private FieldBuilder<?>[] getReportFields(
			Class<?> resourceClass,
			ExportField[] fields) {
		return Arrays.stream(fields).
				map(f -> {
					Field ff = ReflectionUtils.findField(resourceClass, f.getName());
					if (ff != null) {
						return DynamicReports.field(f.getName(), ff.getType());
					} else {
						String getMethodName = "get" + StringUtil.capitalize(f.getName());
						Method m = ReflectionUtils.findMethod(resourceClass, getMethodName);
						if (m != null) {
							return DynamicReports.field(f.getName(), m.getReturnType());
						} else {
							throw new ReportGenerationException(
									resourceClass,
									"Unknown export field (resourceClass=" + resourceClass.getName() + ", fieldName=" + f.getName() + ")");
						}
					}
				}).
				toArray(FieldBuilder[]::new);
	}

	private ColumnBuilder<?, ?>[] getReportColumns(
			Class<?> resourceClass,
			ExportField[] fields) {
		return Arrays.stream(fields).
				map(f -> {
					Field ff = ReflectionUtils.findField(resourceClass, f.getName());
					if (ff != null) {
						return toColumn(resourceClass, f, ff.getType());
					} else {
						String getMethodName = "get" + StringUtil.capitalize(f.getName());
						Method m = ReflectionUtils.findMethod(resourceClass, getMethodName);
						if (m != null) {
							return toColumn(resourceClass, f, m.getReturnType());
						} else {
							throw new ReportGenerationException(
									resourceClass,
									"Unknown export field (resourceClass=" + resourceClass.getName() + ", fieldName=" + f.getName() + ")");
						}
					}
				}).
				toArray(ColumnBuilder[]::new);
	}

	private ColumnBuilder<?, ?> toColumn(
			Class<?> resourceClass,
			ExportField field,
			Class<?> type) {
		try {
			if (LocalDate.class.isAssignableFrom(type)) {
				JasperExpression<Date> localDateToDateExpression = DynamicReports.exp.jasperSyntax(
						"java.util.Date.from($F{" + field.getName() + "}.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())");
				return Columns.column(field.getLabel(), localDateToDateExpression);
			} else if (LocalDateTime.class.isAssignableFrom(type)) {
				JasperExpression<Date> localDateTimeToDateExpression = DynamicReports.exp.jasperSyntax(
						"java.util.Date.from($F{" + field.getName() + "}.atZone(java.time.ZoneId.systemDefault()).toInstant())");
				return Columns.column(field.getLabel(), localDateTimeToDateExpression);
			} else if (type.isEnum()) {
				JasperExpression<String> toStringExpression = DynamicReports.exp.jasperSyntax("$F{" + field.getName() + "}.toString()");
				return Columns.column(field.getLabel(), toStringExpression);
			} else if (ResourceReference.class.isAssignableFrom(type)) {
				JasperExpression<String> resourceReferenceExpression = DynamicReports.exp.jasperSyntax("$F{" + field.getName() + "}.getDescription()");
				return Columns.column(field.getLabel(), resourceReferenceExpression);
			} else if (Serializable.class.equals(type)) {
				JasperExpression<String> toStringExpression = DynamicReports.exp.jasperSyntax("$F{" + field.getName() + "}.toString()");
				return Columns.column(field.getLabel(), toStringExpression);
			} else if (Resource.class.isAssignableFrom(type)) {
				JasperExpression<String> toStringExpression = DynamicReports.exp.jasperSyntax("$F{" + field.getName() + "}.toString()");
				return Columns.column(field.getLabel(), toStringExpression);
			} else {
				DRIDataType<?, ?> dataType = DataTypes.detectType(type);
				return Columns.column(field.getLabel(), field.getName(), dataType);
			}
		} catch (DRException ex) {
			throw new ReportGenerationException(
					resourceClass,
					"Unknown export data type for field (resourceClass=" + resourceClass.getName() + ", fieldName=" + field.getName() + ")",
					ex);
		}
	}

	private JasperReport getJasperReportCompiled(URL reportUrl) throws JRException, IOException {
		URLConnection urlConnection = reportUrl.openConnection();
		return JasperCompileManager.compileReport(urlConnection.getInputStream());
	}

	private DownloadableFile generateDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			ReportFileType fileType,
			OutputStream out) throws JRException, ReportGenerationException {
		if (ReportFileType.CSV.equals(fileType)) {
			return generateCSVDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ReportFileType.ODS.equals(fileType)) {
			return generateOdsDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ReportFileType.ODT.equals(fileType)) {
			return generateOdtDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ReportFileType.XLSX.equals(fileType)) {
			return generateXlsxDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ReportFileType.DOCX.equals(fileType)) {
			return generateDocxDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ReportFileType.PDF.equals(fileType)) {
			return generatePdfDownloadableFile(resourceClass, jasperPrint, out);
		} else {
			throw new ReportGenerationException(
					resourceClass,
					"Unsupported export file type: " + fileType);
		}
	}

	private DownloadableFile generateCSVDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRCsvExporter exporter = new JRCsvExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(out));
		SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
		configuration.setWriteBOM(Boolean.TRUE);
		configuration.setRecordDelimiter("\r\n");
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.csv",
				"text/csv",
				null);
	}

	private DownloadableFile generateOdsDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JROdsExporter exporter = new JROdsExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

		SimpleOdsExporterConfiguration configuration = new SimpleOdsExporterConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.ods",
				"application/vnd.oasis.opendocument.spreadsheet",
				null);
	}

	private DownloadableFile generateOdtDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JROdtExporter exporter = new JROdtExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimpleOdtReportConfiguration configuration = new SimpleOdtReportConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.odt",
				"application/vnd.oasis.opendocument.text",
				null);
	}

	private DownloadableFile generateXlsxDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setOnePagePerSheet(false);
		configuration.setRemoveEmptySpaceBetweenRows(true);
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				null);
	}

	private DownloadableFile generateDocxDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRDocxExporter exporter = new JRDocxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimpleDocxReportConfiguration configuration = new SimpleDocxReportConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				null);
	}

	private DownloadableFile generatePdfDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);
		exporter.setConfiguration(reportConfig);
		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(exportConfig);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.pdf",
				"application/pdf",
				null);
	}

	private void printJrxml(JasperReportBuilder reportBuilder) throws DRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportBuilder.toJrXml(baos);
		System.out.println(baos);
	}

	public static class ReportTemplateStyle {
		public static final StyleBuilder rootStyle = stl.style().setPadding(2);
		public static final StyleBuilder boldStyle = stl.style(rootStyle).bold();
		public static final StyleBuilder italicStyle = stl.style(rootStyle).italic();
		public static final StyleBuilder boldCenteredStyle = stl.style(boldStyle).setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE);
		public static final StyleBuilder bold12CenteredStyle = stl.style(boldCenteredStyle).setFontSize(12);
		public static final StyleBuilder bold18CenteredStyle = stl.style(boldCenteredStyle).setFontSize(18);
		public static final StyleBuilder bold22CenteredStyle = stl.style(boldCenteredStyle).setFontSize(22);
		public static final StyleBuilder columnStyle = stl.style(rootStyle).setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
		public static final StyleBuilder columnTitleStyle = stl.style(columnStyle).setBorder(stl.pen1Point()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setBackgroundColor(Color.LIGHT_GRAY).bold();
		public static final StyleBuilder groupStyle = stl.style(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		public static final StyleBuilder subtotalStyle = stl.style(boldStyle).setTopBorder(stl.pen1Point());
		public static final StyleBuilder  crosstabGroupStyle = stl.style(columnTitleStyle);
		public static final StyleBuilder  crosstabGroupTotalStyle = stl.style(columnTitleStyle).setBackgroundColor(new Color(170, 170, 170));
		public static final StyleBuilder  crosstabGrandTotalStyle = stl.style(columnTitleStyle).setBackgroundColor(new Color(140, 140, 140));
		public static final StyleBuilder  crosstabCellStyle = stl.style(columnStyle).setBorder(stl.pen1Point());
		public static final TableOfContentsCustomizerBuilder tableOfContentsCustomizer = tableOfContentsCustomizer().setHeadingStyle(0, stl.style(rootStyle).bold());
		public static final ReportTemplateBuilder reportTemplate = template().setLocale(Locale.ENGLISH).
				setColumnStyle(columnStyle).
				setColumnTitleStyle(columnTitleStyle).
				setGroupStyle(groupStyle).
				setGroupTitleStyle(groupStyle).
				setSubtotalStyle(subtotalStyle).
				highlightDetailEvenRows().
				crosstabHighlightEvenRows().
				setCrosstabGroupStyle(crosstabGroupStyle).
				setCrosstabGroupTotalStyle(crosstabGroupTotalStyle).
				setCrosstabGrandTotalStyle(crosstabGrandTotalStyle).
				setCrosstabCellStyle(crosstabCellStyle).
				setTableOfContentsCustomizer(tableOfContentsCustomizer);
	}

}
