package es.scsp.common.core;

import es.scsp.common.dao.ClavePrivadaDao;
import es.scsp.common.dao.ParametroConfiguracionDao;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.SecuenciaIdPeticionDao;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.exceptions.ScspException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    private static final Log LOG = LogFactory.getLog(IdGenerator.class);

    @Autowired
    @Qualifier("sessionFactory")
    public SessionFactory sessionFactoryManager;

    @Autowired
    private SecuenciaIdPeticionDao secuenciaIdPeticionDao;
    @Autowired
    private ParametroConfiguracionDao paramDao;
    @Autowired
    private ClavePrivadaDao clavePrivadaDao;
    @Autowired
    private PeticionRespuestaDao peticionRespuestaDao;

    public String getIdPeticion(Servicio servicio) throws ScspException {
        LOG.debug("Generando prefijo para el servicio " + servicio.getCodCertificado());
        String prefix;
        if (servicio.getPrefijoPeticion() != null && !"".equals(servicio.getPrefijoPeticion())) {
            prefix = servicio.getPrefijoPeticion();
        } else {
            ParametroConfiguracion prefijoParam = this.paramDao.select("prefijo.idpeticion");
            if (prefijoParam == null || prefijoParam.getValor().equals("")) {
                String msg = "No posee asociado al servicio un prefijo para la generación del identificador de peticiones, ni tiene configurado el parámetro prefijo.idpeticion en la tabla core_parametro_configuración.No se puede generar correctamente un identificador de petición";
                LOG.error(msg);
                String[] arg = new String[]{msg};
                throw ScspException.getScspException("0201", arg);
            }

            prefix = prefijoParam.getValor();
        }

        if (prefix.length() >= 3 && prefix.length() <= 9) {

            /* MOD PBL */ this.secuenciaIdPeticionDao.setSessionFactory(sessionFactoryManager);
            /* MOD PBL */ String idPeticion = null;
            /* MOD PBL */ // Per evitar error si s'ha guardat una petició a la taula de peticion_respuesta, però degut a un rollback
            /* MOD PBL */ do {
                String secuencial = this.secuenciaIdPeticionDao.next(prefix).toString();
                ParametroConfiguracion tipoId = this.paramDao.select("tipoId");
                boolean versionCortaObligatoria = false;
                int longitudSecuencial;
                if (servicio.getVersionEsquema().endsWith("V2")) {
                    longitudSecuencial = 16 - prefix.length();
                    versionCortaObligatoria = true;
                } else if (tipoId != null && tipoId.getValor().compareTo("long") == 0) {
                    longitudSecuencial = 26 - prefix.length();
                } else {
                    longitudSecuencial = 16 - prefix.length();
                }

                if (secuencial.length() <= longitudSecuencial || !versionCortaObligatoria && 26 >= (prefix + secuencial).length()) {
                    for(int i = secuencial.length(); i < longitudSecuencial; ++i) {
                        secuencial = "0" + secuencial;
                    }

                    /* MOD PBL */ idPeticion = prefix + secuencial;
                    /* MOD PBL */ // return prefix + secuencial;
                } else {
                    String msg = "Se ha excedido el tamaño máximo para el secuencial. Deberá seleccionar otro prefijo para el servicio";
                    LOG.error(msg);
                    String[] arg = new String[]{msg};
                    throw ScspException.getScspException("0201", arg);
                }
            /* MOD PBL */ } while (peticionRespuestaDao.select(idPeticion) != null);
            /* MOD PBL */ return idPeticion;
        } else {
            String msg = "El tamaño del prefijo debe ser mayor o igual a 3 y menor o igual a 9";
            LOG.error(msg);
            String[] arg = new String[]{msg};
            throw ScspException.getScspException("0201", arg);
        }
    }

    public ClavePrivadaDao getClavePrivadaDao() {
        return this.clavePrivadaDao;
    }

    public void setClavePrivadaDao(ClavePrivadaDao clavePrivadaDao) {
        this.clavePrivadaDao = clavePrivadaDao;
    }

    public SecuenciaIdPeticionDao getSecuenciaIdPeticionDao() {
        return this.secuenciaIdPeticionDao;
    }

    public void setSecuenciaIdPeticionDao(SecuenciaIdPeticionDao secuenciaIdPeticionDao) {
        this.secuenciaIdPeticionDao = secuenciaIdPeticionDao;
    }

    public ParametroConfiguracionDao getParamDao() {
        return this.paramDao;
    }

    public void setParamDao(ParametroConfiguracionDao paramDao) {
        this.paramDao = paramDao;
    }

}
