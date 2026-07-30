package es.caib.pinbal.scsp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Suport per generar seqüencials (idpeticion/idtransmision) sota H2, on els
 * procediments emmagatzemats propis d'Oracle (GETSECUENCIAIDPETICION,
 * GETSECUENCIAIDTRANSMISION) no existeixen. Fet servir per
 * es.scsp.common.dao.SecuenciaIdPeticionDao/SecuenciaIdTransmisionDao
 * (overrides locals a pinbal-scsp, vegeu patch-scsp-local-repo.py), que
 * deleguen aquí només quan detecten que la connexió és H2; contra Oracle
 * segueixen cridant el procediment original sense cap canvi de comportament.
 */
public final class SecuenciaH2Helper {

    private static final Object LOCK = new Object();

    private SecuenciaH2Helper() {
    }

    public static boolean esH2(Connection connection) throws SQLException {
        return "H2".equalsIgnoreCase(connection.getMetaData().getDatabaseProductName());
    }

    /**
     * Incrementa i retorna el següent valor sencer per a la taula/prefix
     * indicats, creant la fila amb valor 1 si encara no existeix. La taula
     * ha de tenir columnes (prefijo VARCHAR PRIMARY KEY, valor INT). El
     * `synchronized` serialitza totes les crides dins d'aquesta JVM, evitant
     * la mateixa condició de carrera que un procediment Oracle evitaria amb
     * bloqueig a nivell de fila; suficient per a l'ús real d'aquest mètode
     * (entorns de desenvolupament/e2e amb H2, no producció).
     */
    public static int seguentValor(Connection connection, String taula, String prefijo) throws SQLException {
        synchronized (LOCK) {
            try (PreparedStatement update = connection.prepareStatement(
                    "UPDATE " + taula + " SET valor = valor + 1 WHERE prefijo = ?")) {
                update.setString(1, prefijo);
                if (update.executeUpdate() == 0) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO " + taula + " (prefijo, valor) VALUES (?, 1)")) {
                        insert.setString(1, prefijo);
                        insert.executeUpdate();
                    }
                    return 1;
                }
            }
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT valor FROM " + taula + " WHERE prefijo = ?")) {
                select.setString(1, prefijo);
                try (ResultSet rs = select.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        }
    }
}
