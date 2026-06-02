package es.caib.pinbal.logic.regles.condicio;


import org.jeasy.rules.support.composite.ActivationRuleGroup;

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
