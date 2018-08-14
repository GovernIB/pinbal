/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.ServeiXsdDto;
import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import es.caib.pinbal.core.model.Servei;
import es.scsp.common.domain.core.Servicio;


/**
 * Helper per a la gestió dels arxius XSD dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ServeiXsdHelper {

	public void modificarXsd(
			Servicio servei,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException {
		String fitxerPath = getPathPerFitxerXsd(servei, xsd.getTipus());
		File file = new File(fitxerPath);
		file.getParentFile().mkdirs();
		file.delete();
		FileOutputStream outputStream = new FileOutputStream(fitxerPath);
	    outputStream.write(contingut);
	    outputStream.close();
	}

	public void esborrarXsd(
			Servicio servei,
			XsdTipusEnumDto tipus) throws IOException {
		String fitxerPath = getPathPerFitxerXsd(servei, tipus);
		new File(fitxerPath).delete();
	}

	public FitxerDto descarregarXsd(
			Servicio servei,
			XsdTipusEnumDto tipus) throws IOException {
		String fitxerPath = getPathPerFitxerXsd(servei, tipus);
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(getXsdTipusNom(tipus));
		fitxer.setContingut(IOUtils.toByteArray(new FileInputStream(fitxerPath)));
		fitxer.setContentType("text/xml");
		return fitxer;
	}

	public List<ServeiXsdDto> findAll(
			String serveiCodi) {
		List<ServeiXsdDto> fitxers = new ArrayList<ServeiXsdDto>();
		String serveiPath = getPathPerServei(serveiCodi);
		File[] files = new File(serveiPath).listFiles();
		if (files != null) {
			for (File file: files) {
				ServeiXsdDto dto = new ServeiXsdDto();
				if ("peticion.xsd".equals(file.getName())) {
					dto.setTipus(XsdTipusEnumDto.PETICIO);
				} else if ("respuesta.xsd".equals(file.getName())) {
					dto.setTipus(XsdTipusEnumDto.RESPOSTA);
				} else if ("datos-especificos.xsd".equals(file.getName())) {
					dto.setTipus(XsdTipusEnumDto.DATOS_ESPECIFICOS);
				} else if ("confirmacion-peticion.xsd".equals(file.getName())) {
					dto.setTipus(XsdTipusEnumDto.CONFIRMACIO_PETICIO);
				} else if ("solicitud-respuesta.xsd".equals(file.getName())) {
					dto.setTipus(XsdTipusEnumDto.SOLICITUD_RESPOSTA);
				}
				dto.setNomArxiu(file.getName());
				fitxers.add(dto);
			}
		}
		return fitxers;
	}

	public String getPathPerServei(
			String serveiCodi) {
		StringBuilder path = new StringBuilder();
		String basePath = getPropertyXsdBasePath();
		path.append(basePath);
		if (!basePath.endsWith(File.separator)) {
			path.append(File.separator);
		}
		path.append(serveiCodi);
		return path.toString();
	}



	private String getPathPerFitxerXsd(
			Servicio servei,
			XsdTipusEnumDto tipus) {
		StringBuilder path = new StringBuilder();
		path.append(getPathPerServei(servei.getCodCertificado()));
		path.append(File.separator);
		path.append(getXsdTipusNom(tipus));
		return path.toString();
	}

	private String getXsdTipusNom(XsdTipusEnumDto tipus) {
		switch (tipus) {
		case PETICIO:
			return "peticion.xsd";
		case RESPOSTA:
			return "respuesta.xsd";
		case DATOS_ESPECIFICOS:
			return "datos-especificos.xsd";
		case CONFIRMACIO_PETICIO:
			return "confirmacion-peticion.xsd";
		case SOLICITUD_RESPOSTA:
			return "solicitud-respuesta.xsd";
		}
		return null;
	}

	private String getPropertyXsdBasePath() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.pinbal.xsd.base.path");
	}

}
