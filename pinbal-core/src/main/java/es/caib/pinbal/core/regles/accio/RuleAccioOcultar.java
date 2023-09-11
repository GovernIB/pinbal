
package es.caib.pinbal.core.regles.accio;

import es.caib.pinbal.core.dto.regles.AccioEnum;
import es.caib.pinbal.core.dto.regles.VariableFact;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

@Rule(priority = 22)
public class RuleAccioOcultar {

    @Condition
    public boolean when(@Fact("fact") VariableFact fact) {
        if (fact.isAplicaReglaCondicio() && AccioEnum.OCULTAR.equals(fact.getAccio()))
            return true;
        return false;
    }

    @Action
    public void then(Facts facts) {
        VariableFact fact = facts.get("fact");
        fact.setVisible(false);
        fact.setReglaVisible(fact.getReglaNom());
    }

}