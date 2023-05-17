package es.caib.pinbal.core.regles;

import es.caib.pinbal.core.dto.regles.CampFormProperties;
import es.caib.pinbal.core.dto.regles.TipusVarEnum;
import es.caib.pinbal.core.dto.regles.VariableFact;
import es.caib.pinbal.core.model.Servei;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCampGrup;
import es.caib.pinbal.core.model.ServeiRegla;
import es.caib.pinbal.core.repository.ServeiCampGrupRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiReglaRepository;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReglaHelper {

    @Autowired
    private Rules campRules;
    @Autowired
    private Rules grupRules;
    @Autowired
    private RulesEngine rulesEngine;
    @Autowired
    private ServeiReglaRepository serveiReglaRepository;
    @Autowired
    private ServeiCampRepository serveiCampRepository;
    @Autowired
    private ServeiCampGrupRepository serveiCampGrupRepository;

    public List<CampFormProperties> getCampFormProperties(Servei servei, Set<String> campsModificats) {
        List<CampFormProperties> campFormPropertiesMap = new ArrayList<>();

        if (servei == null)
            return campFormPropertiesMap;

//        List<ServeiRegla> regles = serveiReglaRepository.findByServeiOrderByOrdreAsc(servei);
        List<ServeiRegla> regles = serveiReglaRepository.findReglesCamps(servei);
        if (regles == null || regles.isEmpty())
            return campFormPropertiesMap;

        List<ServeiCamp> camps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(servei.getCodi());
        if (camps == null || camps.isEmpty())
            return campFormPropertiesMap;


        Facts facts = new Facts();
        for(ServeiCamp camp: camps) {
            CampFormProperties campFormProperties = getDefaultCampFormProperties();
            for(ServeiRegla regla: regles) {
                VariableFact variableFact = VariableFact.builder()
                        .modificat(regla.getModificat())
                        .modificatValors(getPathValors(regla.getModificatValor()))
                        .afectatValors(getPathValors(regla.getAfectatValor()))
                        .accio(regla.getAccio())
                        .tipus(TipusVarEnum.CAMP)
                        .varId(camp.getId())
                        .varCodi(camp.getPath())
                        .campsModificats(campsModificats)
                        .visible(campFormProperties.isVisible())
                        .editable(campFormProperties.isEditable())
                        .obligatori(campFormProperties.isObligatori())
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(campRules, facts);
                campFormProperties = getCampFormProperties(variableFact);
            }
            campFormPropertiesMap.add(campFormProperties);
        }

        return campFormPropertiesMap;
    }

    private Set<String> getPathValors(Set<String> valors) {
        if (valors == null)
            return null;

        Set<String> paths = new HashSet<String>();
        for(String valor: valors) {
            if (valor.contains(" | "))
                paths.add(valor.split(" \\| ")[1]);
        }
        return paths;
    }

    public List<CampFormProperties> getGrupFormProperties(Servei servei, Set<String> grupsModificats) {
        List<CampFormProperties> campFormPropertiesMap = new ArrayList<>();

        if (servei == null)
            return campFormPropertiesMap;

//        List<ServeiRegla> regles = serveiReglaRepository.findByServeiOrderByOrdreAsc(servei);
        List<ServeiRegla> regles = serveiReglaRepository.findReglesGrups(servei);
        if (regles == null || regles.isEmpty())
            return campFormPropertiesMap;

        List<ServeiCampGrup> grups = serveiCampGrupRepository.findByServei(servei.getCodi());
        if (grups == null || grups.isEmpty())
            return campFormPropertiesMap;


        Facts facts = new Facts();
        for(ServeiCampGrup grup: grups) {
            CampFormProperties campFormProperties = getDefaultCampFormProperties();
            for(ServeiRegla regla: regles) {
                VariableFact variableFact = VariableFact.builder()
                        .modificat(regla.getModificat())
                        .modificatValors(regla.getModificatValor())
                        .afectatValors(regla.getAfectatValor())
                        .accio(regla.getAccio())
                        .tipus(TipusVarEnum.GRUP)
                        .varId(grup.getId())
                        .varCodi(grup.getNom())
                        .grupsModificats(grupsModificats)
                        .visible(campFormProperties.isVisible())
                        .editable(campFormProperties.isEditable())
                        .obligatori(campFormProperties.isObligatori())
                        .build();
                facts.put("fact", variableFact);
                rulesEngine.fire(grupRules, facts);
                campFormProperties = getCampFormProperties(variableFact);
            }
            campFormPropertiesMap.add(campFormProperties);
        }

        return campFormPropertiesMap;
    }

    private CampFormProperties getCampFormProperties(VariableFact fact) {
        return CampFormProperties.builder()
                .varId(fact.getVarId())
                .visible(fact.isVisible())
                .editable(fact.isEditable())
                .obligatori(fact.isObligatori())
                .build();
    }

    private CampFormProperties getDefaultCampFormProperties() {
        return CampFormProperties.builder()
                .visible(true)
                .editable(true)
                .obligatori(false)
                .build();
    }

}
