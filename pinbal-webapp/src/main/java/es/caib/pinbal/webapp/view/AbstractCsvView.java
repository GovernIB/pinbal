package es.caib.pinbal.webapp.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public abstract class AbstractCsvView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Content-Disposition","attachment; filename=\"file.csv\"");
		response.setContentType("text/csv");
		
		ICsvListWriter csvWriter = new CsvListWriter(
				response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
		buildCsvDocument(model, csvWriter, request, response);
		csvWriter.close();
	}
	
	protected abstract void buildCsvDocument(
			Map<String, Object> model,
			ICsvListWriter csvWriter,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException;

}
