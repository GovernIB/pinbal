package es.caib.pinbal.scsp;

import es.scsp.common.domain.core.CacheCertificado;
import es.scsp.common.domain.core.ClavePrivada;
import es.scsp.common.domain.core.ClavePrivadaExterna;
import es.scsp.common.domain.core.ClavePublica;
import es.scsp.common.domain.core.CodigoError;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.EstadoPeticion;
import es.scsp.common.domain.core.Modulo;
import es.scsp.common.domain.core.ModuloConfiguracion;
import es.scsp.common.domain.core.ModuloPdf;
import es.scsp.common.domain.core.ModuloPdfCesionario;
import es.scsp.common.domain.core.Organismo;
import es.scsp.common.domain.core.OrganismoCesionario;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.TipoMensaje;
import es.scsp.common.domain.core.Token;
import es.scsp.common.domain.core.Transmision;
import es.scsp.common.domain.req.ServicioOrganismoCesionario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Test;

import javax.persistence.EntityNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Reprodueix, i després verifica el fix de, l'error real vist en producció
 * en activar un servei d'una entitat:
 * TwoPhaseLoad$EntityResolver ... EntityNotFoundException en carregar
 * es.scsp.common.domain.req.ServicioOrganismoCesionario.clavePrivada.
 *
 * Arrel del problema: core_req_cesionarios_servicios.claveprivada mai ha
 * tingut una FK cap a core_clave_privada, així que esborrar una ClauPrivada
 * (ScspServiceImpl.deleteClauPrivada) encara referenciada per un registre
 * històric (bloqueado=1) deixa una referència òrfena. ServicioOrganismo
 * Cesionario mapeja clavePrivada amb @ManyToOne per defecte (EAGER, sense
 * @NotFound), així que la propera vegada que es carrega l'històric complet
 * de l'organisme (ScspHelper.selectHistorico, cridat en activar/desactivar
 * un servei) Hibernate no pot resoldre la clau i llança
 * EntityNotFoundException.
 *
 * Fix (changes/2.0.1/04_fk_clave_privada_reqcesionaris_servicis.yaml):
 * afegir la FK amb ON DELETE SET NULL, perquè esborrar la clau deixi el
 * camp a NULL (que és com ja es tracta arreu al codi quan no hi ha clau
 * específica) en lloc d'una referència trencada.
 */
public class ServicioOrganismoCesionarioClavePrivadaFkTest {

    private String url;

    @Test
    public void senseLaFkQuedaOrfeIFallaEnCarregarHistoric() throws Exception {
        SessionFactory sessionFactory = buildSessionFactory("orfefk", /* ambFk= */ false);
        try {
            long clauId = crearClauPrivadaIServeiHistoric(sessionFactory);
            esborrarClauPrivadaPerJdbc(clauId);

            try {
                llistarServeisHistorics(sessionFactory);
                fail("Sense la FK, la referencia orfena hauria de fer fallar la carrega EAGER de clavePrivada");
            } catch (EntityNotFoundException ex) {
                // Aquest es exactament l'error de l'stack trace en producció:
                // TwoPhaseLoad (via FetchNotFoundException, que extén
                // EntityNotFoundException) no pot resoldre la clavePrivada
                // EAGER en carregar l'històric complet de l'organisme.
            }
        } finally {
            sessionFactory.close();
        }
    }

    @Test
    public void ambLaFkOnDeleteSetNullElHistoricEsCarregaSenseError() throws Exception {
        SessionFactory sessionFactory = buildSessionFactory("ambfk", /* ambFk= */ true);
        try {
            long clauId = crearClauPrivadaIServeiHistoric(sessionFactory);
            esborrarClauPrivadaPerJdbc(clauId);

            List<ServicioOrganismoCesionario> historic = llistarServeisHistorics(sessionFactory);

            assertEquals(1, historic.size());
            assertNull(
                    "La FK ON DELETE SET NULL hauria d'haver netejat la referencia orfena",
                    historic.get(0).getClavePrivada());
        } finally {
            sessionFactory.close();
        }
    }

    private long crearClauPrivadaIServeiHistoric(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            ClavePrivada clave = new ClavePrivada();
            clave.setAlias("test-alias");
            clave.setNombre("Test clau");
            clave.setPassword("secret");
            clave.setNumeroSerie("123456");
            clave.setFechaAlta(new Date());
            session.save(clave);

            ServicioOrganismoCesionario historic = new ServicioOrganismoCesionario();
            historic.setClavePrivada(clave);
            historic.setFechaAlta(new Date());
            historic.setBloqueado(true);
            session.save(historic);

            tx.commit();
            return clave.getId();
        } catch (RuntimeException ex) {
            tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
    }

    private void esborrarClauPrivadaPerJdbc(long clauId) throws Exception {
        // Simula clauPrivadaRepository.delete(clauPrivada): un esborrat fet des
        // d'un altre context de persistencia (pinbal-persistence, via JPA/
        // Spring Data), no des d'aquesta mateixa SessionFactory de scsp-core.
        try (Connection con = DriverManager.getConnection(url, "sa", "");
                Statement st = con.createStatement()) {
            st.execute("DELETE FROM core_clave_privada WHERE id = " + clauId);
            con.commit();
        }
    }

    @SuppressWarnings("unchecked")
    private List<ServicioOrganismoCesionario> llistarServeisHistorics(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        try {
            // Analeg a ServicioOrganismoCesionarioDao.selectHistorico: llista
            // tots els registres (Criteria a producció, HQL aqui) fent EAGER
            // fetch de clavePrivada.
            return session.createQuery("from ServicioOrganismoCesionario").list();
        } finally {
            session.close();
        }
    }

    private SessionFactory buildSessionFactory(String dbName, boolean ambFk) throws Exception {
        Class.forName("org.h2.Driver");
        this.url = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1;MODE=Oracle";
        Connection jdbcCon = DriverManager.getConnection(url, "sa", "");
        try (Statement st = jdbcCon.createStatement()) {
            // Repica minima de l'esquema real (01_initial_schema_table.yaml),
            // nomes les columnes rellevants per aquest test. Igual que
            // OrganismoCesionarioIdGenerationTest, l'id es genera via una
            // sequencia connectada com a DEFAULT (no AUTO_INCREMENT, no
            // suportat per H2 en MODE=Oracle) perque @GenericGenerator
            // (strategy="native") resol a IDENTITY sota H2Dialect.
            st.execute("CREATE SEQUENCE test_clave_privada_seq START WITH 1");
            st.execute("CREATE SEQUENCE test_req_cesionaris_serv_seq START WITH 1");
            // ClavePrivada.organismo es EAGER: sense la FK, en fer TwoPhaseLoad
            // de la clau (per id, ja sense fila) Hibernate igualment genera
            // l'outer join cap a aquesta taula, així que ha d'existir encara
            // que no s'hi insereixi cap fila.
            st.execute("CREATE TABLE core_organismo_cesionario (" +
                    "id NUMBER(38,0) PRIMARY KEY, " +
                    "bloqueado NUMBER(1,0), " +
                    "cif VARCHAR2(50), " +
                    "codigoUnidadTramitadora VARCHAR2(50), " +
                    "fechaAlta TIMESTAMP, " +
                    "fechabaja TIMESTAMP, " +
                    "logo BLOB, " +
                    "nombre VARCHAR2(50))");
            st.execute("CREATE TABLE core_clave_privada (" +
                    "id NUMBER(38,0) PRIMARY KEY, " +
                    "alias VARCHAR(256) NOT NULL, " +
                    "nombre VARCHAR(256) NOT NULL, " +
                    "password VARCHAR(256) NOT NULL, " +
                    "numeroserie VARCHAR(256) NOT NULL, " +
                    "fechaalta TIMESTAMP NOT NULL, " +
                    "fechabaja TIMESTAMP, " +
                    "organismo BIGINT, " +
                    "interoperabilidad NUMBER(1,0), " +
                    "bloqueado NUMBER(1,0) DEFAULT 0 NOT NULL)");
            st.execute("ALTER TABLE core_clave_privada ALTER COLUMN id SET DEFAULT NEXT VALUE FOR test_clave_privada_seq");
            st.execute("CREATE TABLE core_req_cesionarios_servicios (" +
                    "id NUMBER(38,0) PRIMARY KEY, " +
                    "servicio BIGINT, " +
                    "claveprivada BIGINT, " +
                    "organismo BIGINT, " +
                    "fechaalta TIMESTAMP NOT NULL, " +
                    "fechabaja TIMESTAMP, " +
                    "bloqueado NUMBER(1,0) NOT NULL)");
            st.execute("ALTER TABLE core_req_cesionarios_servicios ALTER COLUMN id SET DEFAULT NEXT VALUE FOR test_req_cesionaris_serv_seq");
            if (ambFk) {
                st.execute("ALTER TABLE core_req_cesionarios_servicios " +
                        "ADD CONSTRAINT fk_clave_priv_reqcesionaris " +
                        "FOREIGN KEY (claveprivada) REFERENCES core_clave_privada(id) " +
                        "ON DELETE SET NULL");
            }
        }
        jdbcCon.close();

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url", url)
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.username", "sa")
                .applySetting("hibernate.connection.password", "")
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .applySetting("hibernate.connection.autocommit", "false")
                .build();
        try {
            MetadataSources sources = new MetadataSources(registry)
                    .addAnnotatedClass(ClavePrivada.class)
                    .addAnnotatedClass(ServicioOrganismoCesionario.class)
                    // La resta del paquet es.scsp.common.domain.core: cal
                    // registrar-lo sencer perque les entitats de domini de
                    // scsp-core estan totes interconnectades per @ManyToOne
                    // (p.ex. Servicio -> ClavePublica/ClavePrivada/Emisor
                    // Certificado) i Hibernate exigeix conèixer el metamodel
                    // de tot objectiu referenciat en fer el binding
                    // d'anotacions, encara que cap taula/fila d'aquestes
                    // altres entitats es creï ni s'usi en aquest test
                    // (hbm2ddl.auto=none, no es valida contra la BD real).
                    .addAnnotatedClass(CacheCertificado.class)
                    .addAnnotatedClass(ClavePrivadaExterna.class)
                    .addAnnotatedClass(ClavePublica.class)
                    .addAnnotatedClass(CodigoError.class)
                    .addAnnotatedClass(EmisorCertificado.class)
                    .addAnnotatedClass(EstadoPeticion.class)
                    .addAnnotatedClass(Modulo.class)
                    .addAnnotatedClass(ModuloConfiguracion.class)
                    .addAnnotatedClass(ModuloPdf.class)
                    .addAnnotatedClass(ModuloPdfCesionario.class)
                    .addAnnotatedClass(Organismo.class)
                    .addAnnotatedClass(OrganismoCesionario.class)
                    .addAnnotatedClass(ParametroConfiguracion.class)
                    .addAnnotatedClass(PeticionRespuesta.class)
                    .addAnnotatedClass(Servicio.class)
                    .addAnnotatedClass(TipoMensaje.class)
                    .addAnnotatedClass(Token.class)
                    .addAnnotatedClass(Transmision.class);
            return sources.buildMetadata().buildSessionFactory();
        } catch (RuntimeException ex) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw ex;
        }
    }
}
