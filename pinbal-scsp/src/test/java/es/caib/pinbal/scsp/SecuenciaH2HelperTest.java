package es.caib.pinbal.scsp;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SecuenciaH2HelperTest {

    @Test
    public void seguentValorIncrementaPerPrefixIEsIndependentEntrePrefixos() throws Exception {
        Class.forName("org.h2.Driver");
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:secuenciah2test;MODE=Oracle", "sa", "")) {
            try (Statement st = con.createStatement()) {
                st.execute("CREATE TABLE pbl_e2e_seq_id_peticion (prefijo VARCHAR(9) PRIMARY KEY, valor INT NOT NULL)");
            }

            assertEquals(1, SecuenciaH2Helper.seguentValor(con, "pbl_e2e_seq_id_peticion", "PBL"));
            assertEquals(2, SecuenciaH2Helper.seguentValor(con, "pbl_e2e_seq_id_peticion", "PBL"));
            assertEquals(3, SecuenciaH2Helper.seguentValor(con, "pbl_e2e_seq_id_peticion", "PBL"));

            assertEquals(1, SecuenciaH2Helper.seguentValor(con, "pbl_e2e_seq_id_peticion", "ALTRE"));
            assertEquals(2, SecuenciaH2Helper.seguentValor(con, "pbl_e2e_seq_id_peticion", "ALTRE"));

            assertEquals(4, SecuenciaH2Helper.seguentValor(con, "pbl_e2e_seq_id_peticion", "PBL"));
        }
    }

    @Test
    public void esH2RetornaTrueContraUnaConnexioH2() throws Exception {
        Class.forName("org.h2.Driver");
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:secuenciah2test2;MODE=Oracle", "sa", "")) {
            assertTrue(SecuenciaH2Helper.esH2(con));
        }
    }

    @Test
    public void seguentValorEsThreadSafeSotaConcurrencia() throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:secuenciah2concurrent;MODE=Oracle;DB_CLOSE_DELAY=-1";
        try (Connection setupCon = DriverManager.getConnection(url, "sa", "")) {
            try (Statement st = setupCon.createStatement()) {
                st.execute("CREATE TABLE pbl_e2e_seq_id_peticion (prefijo VARCHAR(9) PRIMARY KEY, valor INT NOT NULL)");
            }

            int threads = 20;
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(pool.submit(() -> {
                    try (Connection con = DriverManager.getConnection(url, "sa", "")) {
                        return SecuenciaH2Helper.seguentValor(con, "pbl_e2e_seq_id_peticion", "CONC");
                    }
                }));
            }
            List<Integer> resultats = new ArrayList<>();
            for (Future<Integer> f : futures) {
                resultats.add(f.get(10, TimeUnit.SECONDS));
            }
            pool.shutdown();

            resultats.sort(Integer::compareTo);
            for (int i = 0; i < threads; i++) {
                assertEquals(Integer.valueOf(i + 1), resultats.get(i));
            }
        }
    }
}
