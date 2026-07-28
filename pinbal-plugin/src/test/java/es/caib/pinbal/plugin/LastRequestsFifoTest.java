package es.caib.pinbal.plugin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LastRequestsFifoTest {

    @Test
    void constructorRejectsNonPositiveCapacity() {
        assertThatThrownBy(() -> new LastRequestsFifo(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new LastRequestsFifo(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyFifoHasZeroPercentages() {
        LastRequestsFifo fifo = new LastRequestsFifo(5);
        assertThat(fifo.size()).isZero();
        assertThat(fifo.getOkPercent()).isZero();
        assertThat(fifo.getErrorPercent()).isZero();
    }

    @Test
    void addWithinCapacityAccumulates() {
        LastRequestsFifo fifo = new LastRequestsFifo(4);
        fifo.add(true);
        fifo.add(true);
        fifo.add(false);

        assertThat(fifo.size()).isEqualTo(3);
        assertThat(fifo.getOkPercent()).isCloseTo(66.666, org.assertj.core.data.Offset.offset(0.01));
        assertThat(fifo.getErrorPercent()).isCloseTo(33.333, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void addBeyondCapacityEvictsOldestOk() {
        LastRequestsFifo fifo = new LastRequestsFifo(2);
        fifo.add(true);
        fifo.add(true);
        // La finestra és plena de OK, ara expulsam un OK i afegim un error
        fifo.add(false);

        assertThat(fifo.size()).isEqualTo(2);
        assertThat(fifo.getOkPercent()).isEqualTo(50.0);
        assertThat(fifo.getErrorPercent()).isEqualTo(50.0);
    }

    @Test
    void addBeyondCapacityEvictsOldestError() {
        LastRequestsFifo fifo = new LastRequestsFifo(2);
        fifo.add(false);
        fifo.add(false);
        // Expulsam un error (no decrementa okCount) i afegim un OK
        fifo.add(true);

        assertThat(fifo.size()).isEqualTo(2);
        assertThat(fifo.getOkPercent()).isEqualTo(50.0);
        assertThat(fifo.getErrorPercent()).isEqualTo(50.0);
    }

    @Test
    void allOkGivesHundredPercent() {
        LastRequestsFifo fifo = new LastRequestsFifo(3);
        fifo.add(true);
        fifo.add(true);
        fifo.add(true);

        assertThat(fifo.getOkPercent()).isEqualTo(100.0);
        assertThat(fifo.getErrorPercent()).isZero();
    }

    @Test
    void allErrorsGivesHundredPercentError() {
        LastRequestsFifo fifo = new LastRequestsFifo(3);
        fifo.add(false);
        fifo.add(false);
        fifo.add(false);

        assertThat(fifo.getErrorPercent()).isEqualTo(100.0);
        assertThat(fifo.getOkPercent()).isZero();
    }
}
