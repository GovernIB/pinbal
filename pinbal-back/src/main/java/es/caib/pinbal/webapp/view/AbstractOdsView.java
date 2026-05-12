package es.caib.pinbal.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractOdsView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Content-Disposition","attachment; filename=\"file.ods\"");
		response.setContentType("application/vnd.oasis.opendocument.spreadsheet");
		
		SpreadsheetDocument ods = SpreadsheetDocument.newSpreadsheetDocument();
		
		buildOdsDocument(model, ods, request, response);
		
		ods.save(response.getOutputStream());
	}
	
	protected abstract void buildOdsDocument(
			Map<String, Object> model,
			SpreadsheetDocument ods,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception;

}
