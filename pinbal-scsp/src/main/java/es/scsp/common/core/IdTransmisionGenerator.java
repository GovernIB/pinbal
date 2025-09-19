package es.scsp.common.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import es.scsp.common.dao.ParametroConfiguracionDao;
import es.scsp.common.dao.SecuenciaIdTransmisionDao;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.exceptions.ScspException;

@Component
public class IdTransmisionGenerator {

    private static final Log log = LogFactory.getLog(IdGenerator.class);

    @Autowired
    @Qualifier("sessionFactory")
    public SessionFactory sessionFactoryManager;

    @Autowired
    private SecuenciaIdTransmisionDao secuenciaIdTransmisionDao;
    @Autowired
    private ParametroConfiguracionDao paramDao;

    public String getIdTransmision(Servicio servicio) throws ScspException {
        log.debug("Generando prefijo para el servicio " + servicio.getCodCertificado());
        String prefix;
        if (servicio.getPrefijoIdTransmision() != null && !"".equals(servicio.getPrefijoIdTransmision())) {
            prefix = servicio.getPrefijoIdTransmision();
        } else {
            log.info("No existe un prefijo configurado para las transmisiones del servicio " + servicio.getCodCertificado() + ", se empleará el parametro global " + "prefijo.idtransmision");
            ParametroConfiguracion idTransmisionParam = this.paramDao.select("prefijo.idtransmision");
            if (idTransmisionParam == null) {
                String msg = "No se ha configurado un prefijo para la generación de ids de transmision,en el certificado " + servicio.getCodCertificado();
                String[] arg = new String[]{msg};
                throw ScspException.getScspException("0504", arg);
            }

            prefix = idTransmisionParam.getValor();
            if (prefix.length() < 3 || prefix.length() > 9) {
                String msg = "El tamaño del prefijo debe ser mayor o igual a 9 y menor o igual a 9";
                log.error(msg);
                String[] arg = new String[]{msg};
                throw ScspException.getScspException("0246", arg);
            }
        }

        log.info("Se empleará el prefijo :" + prefix);
        if (prefix.length() >= 3 && prefix.length() <= 9) {
            /* MOD PBL */ this.secuenciaIdTransmisionDao.setSessionFactory(sessionFactoryManager);
            String secuencial = this.secuenciaIdTransmisionDao.next(prefix).toString();
            int longitudSecuencial;
            if (servicio.getVersionEsquema().endsWith("V2")) {
                longitudSecuencial = 25 - prefix.length();
            } else {
                longitudSecuencial = 28 - prefix.length();
            }

            if (secuencial.length() > longitudSecuencial) {
                String msg = "Se ha excedido el tamaño máximo para el secuencial. Debera seleccionar seleccionar otro prefijo para la generacion de ids transmision para este servicio";
                String[] arg = new String[]{msg};
                log.error(msg);
                throw ScspException.getScspException("0501", arg);
            } else {
                for(int i = secuencial.length(); i < longitudSecuencial; ++i) {
                    secuencial = "0" + secuencial;
                }

                return "T" + prefix + secuencial;
            }
        } else {
            String msg = "El tamaño del prefijo debe ser mayor o igual a 3 y menor o igual a 9";
            String[] arg = new String[]{msg};
            throw ScspException.getScspException("0504", arg);
        }
    }

    public SecuenciaIdTransmisionDao getSecuenciaIdTransmisionDao() {
        return this.secuenciaIdTransmisionDao;
    }

    public void setSecuenciaIdTransmisionDao(SecuenciaIdTransmisionDao secuenciaIdTransmisionDao) {
        this.secuenciaIdTransmisionDao = secuenciaIdTransmisionDao;
    }

    public ParametroConfiguracionDao getParamDao() {
        return this.paramDao;
    }

    public void setParamDao(ParametroConfiguracionDao paramDao) {
        this.paramDao = paramDao;
    }

}
