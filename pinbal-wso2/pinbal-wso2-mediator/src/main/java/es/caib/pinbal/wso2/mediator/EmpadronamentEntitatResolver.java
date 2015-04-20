/**
 * 
 */
package es.caib.pinbal.wso2.mediator;

import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

/**
 * EntitatResolver per al servei d'empadronament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmpadronamentEntitatResolver implements EntitatResolver {

	@Override
	public String resolve(OMElement firstElement) {
		try {
			OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(
					new ByteArrayInputStream(
							firstElement.toString().getBytes()));
			OMElement docElement = builder.getDocumentElement();
			
			AXIOMXPath xpathProvincia = new AXIOMXPath("/ns1:Peticion/ns1:Solicitudes/ns1:SolicitudTransmision/ns2:DatosEspecificos/ns2:Solicitud/ns2:ProvinciaSolicitud");
			xpathProvincia.addNamespace("ns1", "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion");
			xpathProvincia.addNamespace("ns2", "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos");
			
			
			OMElement elementProvincia = (OMElement)xpathProvincia.selectSingleNode(docElement);
			
						
			if (elementProvincia == null)
				throw new RuntimeException("Error al resoldre l'entitat: No s'ha trobat el codi de província");
			AXIOMXPath xpathMunicipi = new AXIOMXPath("/ns1:Peticion/ns1:Solicitudes/ns1:SolicitudTransmision/ns2:DatosEspecificos/ns2:Solicitud/ns2:MunicipioSolicitud");
			xpathMunicipi.addNamespace("ns1", "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion");
			xpathMunicipi.addNamespace("ns2", "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos");
			OMElement elementMunicipi = (OMElement)xpathMunicipi.selectSingleNode(docElement);
	
			if (elementMunicipi == null)
				throw new RuntimeException("Error al resoldre l'entitat: No s'ha trobat el codi de municipi");
			return elementProvincia.getText() + elementMunicipi.getText();
		} catch (JaxenException ex) {
			throw new RuntimeException("Error al resoldre l'entitat", ex);
		}
	}

}
