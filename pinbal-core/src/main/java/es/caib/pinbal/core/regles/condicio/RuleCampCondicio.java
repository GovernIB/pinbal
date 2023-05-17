package es.caib.pinbal.core.regles.condicio;

import org.jeasy.rules.support.ActivationRuleGroup;

public class RuleCampCondicio extends ActivationRuleGroup {

    public RuleCampCondicio() {
        addRule(new RuleModAlgunCamp());
        addRule(new RuleModCamps());
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
