package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.persist.resourceentity.AvisResourceEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prova l'única lògica pròpia d'aquest recurs: un avís es crea sempre actiu, encara que el
 * mapeig genèric ja hagi copiat un {@code actiu=false} per defecte des del recurs entrant
 * (JSP: {@code avisForm.jsp} no exposa aquest camp a la creació).
 */
public class AvisResourceServiceImplTest {

    private final AvisResourceServiceImpl service = new AvisResourceServiceImpl();

    @Test
    public void beforeCreateSave_forcaActiuATrue() {
        AvisResourceEntity entity = new AvisResourceEntity();
        entity.setActiu(false);

        service.beforeCreateSave(entity, null, null);

        assertTrue(entity.isActiu());
    }

}
