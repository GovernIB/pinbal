package es.caib.pinbal.client.usuaris;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsuariUniqueFieldValidator implements ConstraintValidator<UsuariUniqueField, UsuariEntitat> {

    @Override
    public void initialize(UsuariUniqueField usuariUniqueField) {

    }

    @Override
    public boolean isValid(UsuariEntitat usuari, ConstraintValidatorContext context) {
        int count = 0;

        if (usuari.getCodi() != null && !usuari.getCodi().trim().isEmpty()) {
            count++;
        }
        if (usuari.getNif() != null && !usuari.getNif().trim().isEmpty()) {
            count++;
        }
        if (usuari.getNom() != null && !usuari.getNom().trim().isEmpty()) {
            count++;
        }

        return count == 1;
    }
}