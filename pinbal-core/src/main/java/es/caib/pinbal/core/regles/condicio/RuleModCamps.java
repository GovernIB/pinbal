package es.caib.pinbal.core.regles.condicio;

import es.caib.pinbal.core.dto.regles.ModificatEnum;
import es.caib.pinbal.core.dto.regles.TipusVarEnum;
import es.caib.pinbal.core.dto.regles.VariableFact;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

@Rule(priority = 4)
public class RuleModCamps {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        return ModificatEnum.CAMPS.equals(fact.getModificat())
                && TipusVarEnum.CAMP.equals(fact.getTipus())
                && fact.getAfectatValors().contains(fact.getVarCodi())
                && fact.getCampsModificats() != null
                && fact.getCampsModificats().containsAll(fact.getModificatValors());
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setAplicaReglaCondicio(true);
    }

}
