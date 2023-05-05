package es.caib.pinbal.core.config;

import es.caib.pinbal.core.regles.accio.RuleAccio;
import es.caib.pinbal.core.regles.condicio.RuleCondicio;
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

    @Bean
    public Rules getRules() {
        Rules rules = new Rules();
        rules.register(new RuleCondicio());
        rules.register(new RuleAccio());

        return rules;
    }


}
