package es.caib.pinbal.helper;

/**
 * Finestra FIFO per desar els darrers N resultats de peticions i calcular els percentatges d'èxits/errors.
 * Segura per a fils en escenaris simples amb productors/lectors concurrents.
 */
public class LastRequestsFifo {
    private final int capacity;
    private final boolean[] window;
    private int pos = 0;     // següent posició d'escriptura (i índex d'evicció quan està plena)
    private int size = 0;    // nombre d'entrades vàlides a la finestra (<= capacitat)
    private int okCount = 0; // nombre d'OK dins la finestra actual

    public LastRequestsFifo(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("La capacitat ha de ser > 0");
        this.capacity = capacity;
        this.window = new boolean[capacity];
    }

    /** Afegeix un nou resultat a la FIFO, expulsant el més antic si està plena. */
    public synchronized void add(boolean ok) {
        if (size == capacity) {
            boolean old = window[pos];
            if (old) okCount--; // s'expulsa un OK
        } else {
            size++;
        }
        window[pos] = ok;
        if (ok) okCount++;
        pos = (pos + 1) % capacity;
    }

    /** Nombre actual d'elements a la finestra. */
    public synchronized int size() {
        return size;
    }

    /** Percentatge (0-100 arrodonit) de peticions OK a la finestra actual. Retorna 0 si size==0. */
    public synchronized double getOkPercent() {
        if (size == 0) return 0;
        return (okCount * 100.0) / size;
    }

    /** Percentatge (0-100 arrodonit) de peticions amb ERROR a la finestra actual. Retorna 0 si size==0. */
    public synchronized double getErrorPercent() {
        if (size == 0) return 0;
        int errors = size - okCount;
        return (errors * 100.0) / size;
    }
}