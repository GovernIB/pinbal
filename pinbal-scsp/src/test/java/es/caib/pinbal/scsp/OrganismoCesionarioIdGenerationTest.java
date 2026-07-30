package es.caib.pinbal.scsp;

import es.scsp.common.domain.core.OrganismoCesionario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * es.scsp.common.domain.core.OrganismoCesionario (i les altres entitats de
 * domini de scsp-core que fan servir @GenericGenerator(strategy="native"))
 * fallaven sota H2 amb "NULL not allowed for column ID": a diferencia
 * d'Oracle, on "native" resol a sequencia, sota H2Dialect resol a IDENTITY
 * i ignora el sequence_name declarat a l'entitat. Fix: connectar la
 * sequencia (ja existent, 04_initial_schema_sequence.yaml) com a valor per
 * defecte de la columna id via ALTER TABLE (mateix patró que
 * init-sequence-2-h2-acl-identity per a les taules pbl_acl_*, vegeu
 * changes/2.0.1/03_h2_scsp_domain_id_defaults.yaml).
 */
public class OrganismoCesionarioIdGenerationTest {

    @Test
    public void organismoCesionarioGeneraIdCorrectamentSotaH2() throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:orgcesidgen;DB_CLOSE_DELAY=-1;MODE=Oracle";
        Connection jdbcCon = DriverManager.getConnection(url, "sa", "");
        try (Statement st = jdbcCon.createStatement()) {
            // Rèplica mínima de l'esquema real (01_initial_schema_table.yaml +
            // 04_initial_schema_sequence.yaml + el fix a
            // changes/2.0.1/03_h2_scsp_domain_id_defaults.yaml), només el
            // necessari per a aquest test, per evitar dependre de tota la
            // cadena de Liquibase (que té canvis no relacionats sensibles a
            // la versió d'H2 usada en aquest mòdul).
            st.execute("CREATE TABLE core_organismo_cesionario (" +
                    "id NUMBER(38,0) PRIMARY KEY, " +
                    "bloqueado NUMBER(1,0), " +
                    "cif VARCHAR2(50), " +
                    "codigoUnidadTramitadora VARCHAR2(50), " +
                    "fechaAlta TIMESTAMP, " +
                    "fechabaja TIMESTAMP, " +
                    "logo BLOB, " +
                    "nombre VARCHAR2(50))");
            st.execute("CREATE SEQUENCE id_organismo_cesionario_seq START WITH 1");
            st.execute("ALTER TABLE core_organismo_cesionario ALTER COLUMN id SET DEFAULT NEXT VALUE FOR id_organismo_cesionario_seq");
        }
        jdbcCon.close();

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url", url)
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.username", "sa")
                .applySetting("hibernate.connection.password", "")
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        try {
            SessionFactory sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(OrganismoCesionario.class)
                    .buildMetadata()
                    .buildSessionFactory();
            try {
                Session session = sessionFactory.openSession();
                Transaction tx = session.beginTransaction();
                try {
                    OrganismoCesionario oc = new OrganismoCesionario();
                    oc.cif = "Q1111111A";
                    oc.nombre = "Test organisme";
                    oc.fechaAlta = new Date();
                    oc.bloqueado = false;
                    session.save(oc);
                    tx.commit();
                    assertTrue("l'id generat hauria de ser > 0", oc.id > 0);
                } catch (Exception ex) {
                    tx.rollback();
                    throw ex;
                } finally {
                    session.close();
                }
            } finally {
                sessionFactory.close();
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
