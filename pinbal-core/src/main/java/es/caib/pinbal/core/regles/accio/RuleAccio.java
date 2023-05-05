package es.caib.pinbal.core.regles.accio;

import org.jeasy.rules.support.ActivationRuleGroup;

public class RuleAccio extends ActivationRuleGroup {

    public RuleAccio() {
        addRule(new RuleAccioMostrar());
        addRule(new RuleAccioOcultar());
        addRule(new RuleAccioEditar());
        addRule(new RuleAccioBloquejar());
        addRule(new RuleAccioObligatori());
        addRule(new RuleAccioOpcional());
    }

    @Override
    public int getPriority() {
        return 20;
    }
}
