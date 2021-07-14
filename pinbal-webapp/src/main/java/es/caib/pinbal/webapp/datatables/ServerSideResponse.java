package es.caib.pinbal.webapp.datatables;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import es.caib.pinbal.core.dto.Identificable;


/**
 * Representació d'una petició ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServerSideResponse<T extends Identificable<ID>, ID extends Serializable> {

	private static final String ATRIBUT_ID = "DT_Id";
	private static final String ATRIBUT_ROW_ID = "DT_RowId";

	private int draw;
	private long recordsTotal;
	private long recordsFiltered;
	private List<Map<String, Object>> data;
	private String error;

	public ServerSideResponse(
			ServerSideRequest serverSideRequest,
			Page<T> page) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		fillServerSideRequest(serverSideRequest, page);
	}
	
	public ServerSideResponse(
			ServerSideRequest serverSideRequest,
			List<T> llista) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		List<T> sublist = llista.subList(
				serverSideRequest.getStart(),
				(serverSideRequest.getLength() > 0 && serverSideRequest.getLength() < (llista.size() - serverSideRequest.getStart())) ? serverSideRequest.getLength() : llista.size());

		Page<T> page = new PageImpl<T>(
				sublist,
				serverSideRequest.toPageable(),
				(llista != null) ? llista.size() : 0);

		
//		 new ServerSideResponse<T, Long>(serverSideRequest, page);
		
		fillServerSideRequest(serverSideRequest, page);
		
	}
	
	
	public void fillServerSideRequest(
				ServerSideRequest serverSideRequest,
				Page<T> page) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		draw = serverSideRequest.getDraw();
		recordsTotal = page.getTotalElements();
		recordsFiltered = page.getTotalElements();
		data = new ArrayList<Map<String, Object>>();
		if (page.getContent() != null) {
			for (T registre: page.getContent()) {
				int numColumns = serverSideRequest.getColumns().size();
				List<PropertyDescriptor> descriptors = getBeanPropertyDescriptors(registre);
				Object[] dadesRegistre = new Object[numColumns];
				Map<String, Object> mapRegistre = new HashMap<String, Object>();
				for (int i = 0; i < numColumns; i++) {
					//String propietatNom = serverSideRequest.getColumns().get(i).getName();
					String propietatNom = serverSideRequest.getColumns().get(i).getData();
					if (propietatNom.contains(".")) {
						propietatNom = propietatNom.substring(0, propietatNom.indexOf("."));
					}
					Object valor = getPropertyValue(registre, propietatNom, descriptors);
					mapRegistre.put(
							propietatNom,
							valor);
					dadesRegistre[i] = valor;
				}
				mapRegistre.put(
						ATRIBUT_ID,
						registre.getId());
				mapRegistre.put(
						ATRIBUT_ROW_ID,
						"row_" + registre.getId());
				data.add(mapRegistre);
			}
		}
	}
	

	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public long getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public long getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public List<Map<String, Object>> getData() {
		return data;
	}
	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static List<PropertyDescriptor> getBeanPropertyDescriptors(
			Object bean) {
		List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>(
				Arrays.asList(
						PropertyUtils.getPropertyDescriptors(bean)));
		Iterator<PropertyDescriptor> it = descriptors.iterator();
		while (it.hasNext()) {
			PropertyDescriptor pd = it.next();
			if ("class".equals(pd.getName())) {
				it.remove();
				break;
			}
		}
		return descriptors;
	}
	private static Object getPropertyValue(
			Object registre,
			String propietatNom,
			List<PropertyDescriptor> descriptors) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object valor = null;
		try {
			int index = Integer.parseInt(propietatNom);
			valor = PropertyUtils.getProperty(registre, descriptors.get(index).getName());
		} catch (NumberFormatException ex) {
			if (propietatNom != null && !propietatNom.isEmpty() && !"<null>".equals(propietatNom)) {
				valor = PropertyUtils.getProperty(registre, propietatNom);
			}
		}
		return valor;
	}

}
