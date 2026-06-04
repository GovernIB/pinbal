package es.caib.pinbal.back.view;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class AbstractOdsView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=\"file.ods\"");
        response.setContentType("application/vnd.oasis.opendocument.spreadsheet");

        OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
        buildOdsDocument(model, ods, request, response);
        ods.save(response.getOutputStream());
        ods.close();
    }

    protected abstract void buildOdsDocument(
            Map<String, Object> model,
            OdfSpreadsheetDocument ods,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception;
}
