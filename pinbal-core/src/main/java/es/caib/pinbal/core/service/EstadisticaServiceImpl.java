/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.comanda.ms.estadistica.model.Dimensio;
import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.Fet;
import es.caib.comanda.ms.estadistica.model.Format;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistreEstadistic;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.pinbal.core.model.explotacio.ExplotConsultaFets;
import es.caib.pinbal.core.model.explotacio.ExplotTempsEntity;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.core.repository.explotacio.ExplotTempsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class EstadisticaServiceImpl implements EstadisticaService {

    @Autowired
    private UsuariRepository usuariRepository;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private ServeiConfigRepository serveiConfigRepository;
    @Autowired
    private ExplotTempsRepository  explotTempsRepository;
    @Autowired
    private ExplotConsultaFetsRepository explotConsultaFetsRepository;

    @Resource
    private ConsultaService consultaService;

    public enum DimEnum {
        ENT ("Entitat", "Codi de l'entitat a la que s'ha realitzat la consulta"),
        PRC ("Procediment", "Procediment al que s'ha realitzat la consulta"),
        SRV ("Servei", "Servei al que s'ha realitzat la consulta"),
        USU ("Usuari", "Codi de l'usuari que ha realitzat la consulta"),
        TIP ("Tipus", "Tipus de consulta: síncrona o assíncrona"),
        ORI ("Origen", "Lloc des d'on s'ha realitzat la consulta: interfície web o API Rest");

        private String nom;
        private String descripcio;

        DimEnum(String nom, String descripcio) {
            this.nom = nom;
            this.descripcio = descripcio;
        }

        public String getNom() {
            return nom;
        }
        public String getDescripcio() {
            return descripcio;
        }
    }

    public enum FetEnum {
        PND ("Pendent", "La consulta està pendent de ser tramitada"),
        PRC ("Processant", "La consulta s'està processant"),
        TRA ("Tramitada", "La consulta ha estat tramitada correctament"),
        ERR ("Error", "La consulta ha donat error");

        private String nom;
        private String descripcio;

        FetEnum(String nom, String descripcio) {
            this.nom = nom;
            this.descripcio = descripcio;
        }

        public String getNom() {
            return nom;
        }
        public String getDescripcio() {
            return descripcio;
        }

    }

    public enum Tipus {
        SINCRONA,
        ASINCRONA
    }

    public enum Origen {
        WEB,
        REST
    }

    @Override
    public EstadistiquesInfo getEstadistiquesInfo() {

        List<DimensioDesc> dimensions = getDimensions();
        List<IndicadorDesc> indicadors = getIndicadors();
        return EstadistiquesInfo.builder().codi("PBL").dimensions(dimensions).indicadors(indicadors).build();
    }

    @Override
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return consultaEstadistiques(null);
    }

    @Override
    public RegistresEstadistics consultaEstadistiques(Date data) {

        log.debug("Consultant estadístiques per data " + data);

        // Si no han indicat una data, retornam les estadístiques del darrer dia (ahir)
        if (data == null) data = ahir();

        // Si ens envien una data futura, retornam una llista buida
        if (data.after(ahir())) {
            return RegistresEstadistics.builder()
                    .temps(data)
                    .fets(new ArrayList<RegistreEstadistic>()).build();
        }

        Date dataInici = diaAnterior(data);
        ExplotTempsEntity tempsFinal = getExplotTempsEntity(data);
        ExplotTempsEntity tempsInici = getExplotTempsEntity(dataInici);

        Map<EstadisticaKey, ExplotConsultaFets> fetsMap = new LinkedHashMap<>();
        List<ExplotConsultaFets> fetsAcumulatFinal = explotConsultaFetsRepository.findByTemps(tempsFinal);

        if (fetsAcumulatFinal == null || fetsAcumulatFinal.isEmpty()) {
            return RegistresEstadistics.builder().temps(data).build();
        }
        List<ExplotConsultaFets> fetsAcumulatInici = explotConsultaFetsRepository.findByTemps(tempsInici);

        for (ExplotConsultaFets fet: fetsAcumulatFinal) {
            fetsMap.put(new EstadisticaKey(fet.getEntitatCodi(), fet.getProcedimentCodi(), fet.getServeiCodi(), fet.getUsuariCodi()), fet);
        }

        if (fetsAcumulatInici != null && !fetsAcumulatInici.isEmpty()) {
            for (ExplotConsultaFets fet : fetsAcumulatInici) {
                EstadisticaKey key = new EstadisticaKey(fet.getEntitatCodi(), fet.getProcedimentCodi(), fet.getServeiCodi(), fet.getUsuariCodi());
                fetsMap.put(key, fetsMap.get(key).minus(fet));
            }
        }
        return toRegistreEstadistic(fetsMap, data);
    }

    @Override
    public List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi) {

        log.debug("Consultant estadístiques entre dates (" + dataInici + " - " + dataFi + ")");

        // S'han d'emplenar els dos camps de data
        if (dataInici == null || dataFi == null) {
            return Collections.emptyList();
        }

        // Si la data d'inici és posterior a la data de fi, els intercanviam
        if (dataInici.after(dataFi)) {
            Date temp = dataInici;
            dataInici = dataFi;
            dataFi = temp;
        }
        
        List<RegistresEstadistics> registresEstadistics = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data(dataInici));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(data(dataFi));

        while (!cal.after(endCal)) {
            Date currentDate = cal.getTime();
            registresEstadistics.add(consultaEstadistiques(currentDate));
            cal.add(Calendar.DATE, 1);
        }

        return registresEstadistics;
    }

    @Override
    public String generarEstadistiques(Date dataInici, Date dataFi) {

        // S'han d'emplenar els dos camps de data
        if (dataInici == null || dataFi == null) {
            return "Les dates no poden ser null";
        }

        // Si la data d'inici és posterior a la data de fi, els intercanviam
        if (dataInici.after(dataFi)) {
            Date temp = dataInici;
            dataInici = dataFi;
            dataFi = temp;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(data(dataInici));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(data(dataFi));

        while (!cal.after(endCal)) {
            Date currentDate = cal.getTime();
            consultaService.generarDadesExplotacio(currentDate);
            cal.add(Calendar.DATE, 1);
        }
        return "Done";
    }

    private ExplotTempsEntity getExplotTempsEntity(Date data) {
        ExplotTempsEntity tempsFinal = explotTempsRepository.findFirstByData(data);
        if (tempsFinal == null) {
            consultaService.generarDadesExplotacio(data);
            tempsFinal = explotTempsRepository.findFirstByData(data);
        }
        return tempsFinal;
    }

    private List<IndicadorDesc> getIndicadors() {
        List<IndicadorDesc> indicadors = new ArrayList<>();
        indicadors.add(IndicadorDesc.builder().codi(FetEnum.PND.name()).nom(FetEnum.PND.getNom()).descripcio(FetEnum.PND.getDescripcio()).format(Format.LONG).build());
        indicadors.add(IndicadorDesc.builder().codi(FetEnum.PRC.name()).nom(FetEnum.PRC.getNom()).descripcio(FetEnum.PRC.getDescripcio()).format(Format.LONG).build());
        indicadors.add(IndicadorDesc.builder().codi(FetEnum.TRA.name()).nom(FetEnum.TRA.getNom()).descripcio(FetEnum.TRA.getDescripcio()).format(Format.LONG).build());
        indicadors.add(IndicadorDesc.builder().codi(FetEnum.ERR.name()).nom(FetEnum.ERR.getNom()).descripcio(FetEnum.ERR.getDescripcio()).format(Format.LONG).build());
        return indicadors;
    }

    private List<DimensioDesc> getDimensions() {
        List<String> entitatCodis = entitatRepository.findAllCodis();
        List<String> procedimentCodis = procedimentRepository.findAllCodis();
        List<String> serveiCodis = serveiConfigRepository.findAllCodis();
        List<String> usuariCodis = usuariRepository.findAllCodis();
        List<String> tipus = Arrays.asList(Tipus.SINCRONA.name(), Tipus.ASINCRONA.name());
        List<String> origens = Arrays.asList(Origen.WEB.name(), Origen.REST.name());

        List<DimensioDesc> dimensions = new ArrayList<>();
        dimensions.add(DimensioDesc.builder().codi(DimEnum.ENT.name()).nom(DimEnum.ENT.getNom()).descripcio(DimEnum.ENT.getDescripcio()).valors(entitatCodis).build());
        dimensions.add(DimensioDesc.builder().codi(DimEnum.PRC.name()).nom(DimEnum.PRC.getNom()).descripcio(DimEnum.PRC.getDescripcio()).valors(procedimentCodis).build());
        dimensions.add(DimensioDesc.builder().codi(DimEnum.SRV.name()).nom(DimEnum.SRV.getNom()).descripcio(DimEnum.SRV.getDescripcio()).valors(serveiCodis).build());
        dimensions.add(DimensioDesc.builder().codi(DimEnum.USU.name()).nom(DimEnum.USU.getNom()).descripcio(DimEnum.USU.getDescripcio()).valors(usuariCodis).build());
        dimensions.add(DimensioDesc.builder().codi(DimEnum.TIP.name()).nom(DimEnum.TIP.getNom()).descripcio(DimEnum.TIP.getDescripcio()).valors(tipus).build());
        dimensions.add(DimensioDesc.builder().codi(DimEnum.ORI.name()).nom(DimEnum.ORI.getNom()).descripcio(DimEnum.ORI.getDescripcio()).valors(origens).build());
        return dimensions;
    }

    private RegistresEstadistics toRegistreEstadistic(Map<EstadisticaKey, ExplotConsultaFets> fetsMap, Date data) {
        List<RegistreEstadistic> registreEstadistics = new ArrayList<>();

        for (Map.Entry<EstadisticaKey, ExplotConsultaFets> entryFets : fetsMap.entrySet()) {
            processarCombinacionsConsultes(entryFets, registreEstadistics);
        }

        return RegistresEstadistics.builder()
                .temps(data)
                .fets(registreEstadistics)
                .build();
    }

    private void processarCombinacionsConsultes(
            Map.Entry<EstadisticaKey, ExplotConsultaFets> entryFets,
            List<RegistreEstadistic> registreEstadistics) {

        for (Tipus tipus : Tipus.values()) {
            for (Origen origen : Origen.values()) {
                List<Dimensio> dimensions = toDimensions(entryFets.getKey(), tipus, origen);
                List<Fet> fets = toFets(entryFets.getValue(), tipus, origen);

                if (fets != null && !fets.isEmpty()) {
                    RegistreEstadistic registreEstadistic = RegistreEstadistic.builder()
                            .dimensions(dimensions)
                            .fets(fets)
                            .build();
                    registreEstadistics.add(registreEstadistic);
                }
            }
        }
    }

    private List<Dimensio> toDimensions(EstadisticaKey key, Tipus tipus, Origen origen) {
        List<Dimensio> dimensions = new ArrayList<>();
        dimensions.add(Dimensio.builder().codi(DimEnum.ENT.name()).valor(key.getEntitatCodi()).build());
        dimensions.add(Dimensio.builder().codi(DimEnum.PRC.name()).valor(key.getProcedimentCodi()).build());
        dimensions.add(Dimensio.builder().codi(DimEnum.SRV.name()).valor(key.getServeiCodi()).build());
        dimensions.add(Dimensio.builder().codi(DimEnum.USU.name()).valor(key.getUsuariCodi()).build());
        dimensions.add(Dimensio.builder().codi(DimEnum.TIP.name()).valor(tipus.name()).build()); // Tipus.SINCRONA.name(), Tipus.ASINCRONA.name()
        dimensions.add(Dimensio.builder().codi(DimEnum.ORI.name()).valor(origen.name()).build()); // Origen.WEB.name(), Origen.REST.name()
        return dimensions;
    }

    private List<Fet> toFets(ExplotConsultaFets value, Tipus tipus, Origen origen) {
        ConsultaMetrics metrics = getMetricsPerTipusIOrigen(value, tipus, origen);

        if (!metrics.hiHaFets()) {
            return Collections.emptyList();
        }

        List<Fet> fets = new ArrayList<>();
        fets.add(Fet.builder().codi(FetEnum.PND.name()).valor((double) metrics.pendent).build());
        fets.add(Fet.builder().codi(FetEnum.PRC.name()).valor((double) metrics.processant).build());
        fets.add(Fet.builder().codi(FetEnum.TRA.name()).valor((double) metrics.tramitada).build());
        fets.add(Fet.builder().codi(FetEnum.ERR.name()).valor((double) metrics.error).build());
        return fets;
    }

    private ConsultaMetrics getMetricsPerTipusIOrigen(ExplotConsultaFets value, Tipus tipus, Origen origen) {
        if (Tipus.SINCRONA.equals(tipus)) {
            if (Origen.WEB.equals(origen)) {
                return new ConsultaMetrics(value.getWebPend(), value.getWebProc(), value.getWebOk(), value.getWebError());
            } else {
                return new ConsultaMetrics(value.getRecPend(), value.getRecProc(), value.getRecOk(), value.getRecError());
            }
        } else {
            if (Origen.WEB.equals(origen)) {
                return new ConsultaMetrics(value.getWebMassPend(), value.getWebMassProc(), value.getWebMassOk(), value.getWebMassError());
            } else {
                return new ConsultaMetrics(value.getRecMassPend(), value.getRecMassProc(), value.getRecMassOk(), value.getRecMassError());
            }
        }
    }

    private Date ahir() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return getDateZero(cal);
    }

    private Date diaAnterior(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return getDateZero(cal);
    }

    private Date data(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // Set the time components to zero
        return getDateZero(cal);
    }

    private static Date getDateZero(Calendar cal) {
        // Set the time components to zero
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class EstadisticaKey {
        String entitatCodi;
        String procedimentCodi;
        String serveiCodi;
        String usuariCodi;
    }

    @Data
    @AllArgsConstructor
    private static class ConsultaMetrics {
        long pendent;
        long processant;
        long tramitada;
        long error;

        boolean hiHaFets() {
            return pendent + processant + tramitada + error > 0;
        }
    }

}
