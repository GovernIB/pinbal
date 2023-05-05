package es.caib.pinbal.core.regles.condicio;

import es.caib.pinbal.core.dto.regles.ModificatEnum;
import es.caib.pinbal.core.dto.regles.TipusVarEnum;
import es.caib.pinbal.core.dto.regles.VariableFact;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import java.util.Collections;

@Rule(priority = 3)
public class RuleModAlgunCamp {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        return ModificatEnum.ALGUN_CAMP.equals(fact.getModificat())
                && TipusVarEnum.CAMP.equals(fact.getTipus())
                && fact.getAfectatValors().contains(fact.getVarCodi())
                && !Collections.disjoint(fact.getModificatValors(), fact.getCampsModificats());
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaCondicio(true);
    }

}
