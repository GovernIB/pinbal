package es.caib.pinbal.back.view;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Classe base per a vistes Excel (.xls) amb Apache POI HSSF.
 * Substitueix l'AbstractExcelView eliminat a Spring 5.
 */
public abstract class AbstractHssfView extends AbstractView {

    public AbstractHssfView() {
        setContentType("application/vnd.ms-excel");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        buildExcelDocument(model, workbook, request, response);
        response.setContentType(getContentType());
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    protected abstract void buildExcelDocument(
            Map<String, Object> model,
            HSSFWorkbook workbook,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception;
}
