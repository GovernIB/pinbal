package es.caib.pinbal.back.view;

import com.opencsv.CSVWriter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractCsvView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
	                                       HttpServletRequest request,
	                                       HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"file.csv\"");
		response.setContentType("text/csv");
		response.setCharacterEncoding("UTF-8");

		CSVWriter csvWriter = new CSVWriter(
				response.getWriter(),
				CSVWriter.DEFAULT_SEPARATOR,
				CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER,
				CSVWriter.DEFAULT_LINE_END);

		buildCsvDocument(model, csvWriter, request, response);
		csvWriter.close();
	}

	protected abstract void buildCsvDocument(
			Map<String, Object> model,
			CSVWriter csvWriter,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException;
}
