package es.caib.pinbal.core.regles.condicio;

import org.jeasy.rules.support.ActivationRuleGroup;

public class RuleCondicio extends ActivationRuleGroup {

    public RuleCondicio() {
        addRule(new RuleModAlgunGrup());
        addRule(new RuleModGrups());
        addRule(new RuleModAlgunCamp());
        addRule(new RuleModGrups());
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
