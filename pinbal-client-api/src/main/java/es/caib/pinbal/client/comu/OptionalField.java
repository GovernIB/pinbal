package es.caib.pinbal.client.comu;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que permet encapsular un camp opcional que pot tenir valor o establir-se com a null.
 *
 * @param <T> Tipus del camp.
 */
@Getter @Setter
public class OptionalField<T> {
    private boolean shouldUpdate;
    private T value;

    public static <T> OptionalField<T> of(T value) {
        return new OptionalField<>(value);
    }

    public OptionalField() {
        this.shouldUpdate = false;
    }

    public OptionalField(T value) {
        this.shouldUpdate = true;
        this.value = value;
    }

    
    public void setValue(T value) {
        this.shouldUpdate = true;
        this.value = value;
    }
}