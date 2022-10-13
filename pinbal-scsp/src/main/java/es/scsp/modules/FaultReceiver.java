//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package es.scsp.modules;

import es.scsp.common.exceptions.ScspException;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.Handler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FaultReceiver extends Fault {
    private static final Log LOG = LogFactory.getLog(FaultReceiver.class);

    public FaultReceiver() {
    }

    public Handler.InvocationResponse invoke(MessageContext messageContext) throws AxisFault {
        LOG.debug("Procesando SOAP fault en el mensaje de entrada.");
        LOG.warn("--------------------- obtenida respuesta fault --------------------");
        LOG.warn(messageContext.getEnvelope().toString());
        LOG.warn("-------------------- /obtenida respuesta fault --------------------");
        OMElement body = messageContext.getEnvelope().getBody();
        OMElement nodeFaultstring = getChildrenWithNameRecursive(body, "faultstring");
        OMElement nodeError = getChildrenWithNameRecursive(body, "LiteralError");
        OMElement nodeErrorSec = getChildrenWithNameRecursive(body, "LiteralErrorSec");
        OMElement nodeCodigoEstado = getChildrenWithNameRecursive(body, "CodigoEstado");
        OMElement nodeCodigoEstadoSec = getChildrenWithNameRecursive(body, "CodigoEstadoSecundario");
        String msg = "El servidor ha devuelto un mensaje SOAP Fault.";
        if (nodeError != null && nodeError.getText() != null && !"".equals(nodeError.getText())) {
            if (nodeFaultstring != null && nodeFaultstring.getText() != null && !"".equals(nodeFaultstring.getText()) && nodeFaultstring.getText().contains(nodeError.getText()) && nodeError.getText().endsWith("...")) {
                msg = msg + " " + nodeFaultstring.getText();
            } else {
                msg = msg + " " + nodeError.getText();
            }
        } else if (nodeFaultstring != null && nodeFaultstring.getText() != null && !"".equals(nodeFaultstring.getText())) {
            msg = msg + " " + nodeFaultstring.getText();
        }

        String code = "0904";
        if (nodeCodigoEstado != null && nodeCodigoEstado.getText() != null && !"".equals(nodeCodigoEstado.getText())) {
            code = nodeCodigoEstado.getText();
        }

        String msgsecundario = null;
        if (nodeErrorSec != null && nodeErrorSec.getText() != null && !"".equals(nodeErrorSec.getText())) {
            msgsecundario = nodeErrorSec.getText();
        }

        String codesecundario = null;
        if (nodeCodigoEstadoSec != null && nodeCodigoEstadoSec.getText() != null && !"".equals(nodeCodigoEstadoSec.getText())) {
            codesecundario = nodeCodigoEstadoSec.getText();
        }

        throw new ScspException(msg, code, codesecundario, msgsecundario);
    }
}
