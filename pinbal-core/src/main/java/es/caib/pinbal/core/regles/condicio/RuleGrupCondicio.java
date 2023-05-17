package es.caib.pinbal.core.regles.condicio;

import org.jeasy.rules.support.ActivationRuleGroup;

public class RuleGrupCondicio extends ActivationRuleGroup {

    public RuleGrupCondicio() {
        addRule(new RuleModAlgunGrup());
        addRule(new RuleModGrups());
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
