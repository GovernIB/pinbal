package es.caib.pinbal.core.config;

import es.caib.pinbal.core.regles.accio.RuleAccio;
import es.caib.pinbal.core.regles.condicio.RuleCampCondicio;
import es.caib.pinbal.core.regles.condicio.RuleGrupCondicio;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RulesConfig {

    @Bean
    public RulesEngine getRulesEngine() {
        RulesEngine rulesEngine = new DefaultRulesEngine();

        return rulesEngine;
    }

    @Bean(name = "campRules")
    public Rules getCampRules() {
        Rules rules = new Rules();
        rules.register(new RuleCampCondicio());
        rules.register(new RuleAccio());

        return rules;
    }

    @Bean(name = "grupRules")
    public Rules getGrupRules() {
        Rules rules = new Rules();
        rules.register(new RuleGrupCondicio());
        rules.register(new RuleAccio());

        return rules;
    }


}
