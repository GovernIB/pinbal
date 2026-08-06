package es.caib.pinbal.client.serveis;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.caib.pinbal.client.recobriment.v2.Titular;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServeiBasic {

    private String codi;
    private String descripcio;
    private Boolean actiu = false;
    /**
     * Indica si l'usuari que realitza la petició té permís per a consultar
     * aquest servei. Només s'informa si s'ha demanat explícitament; en cas
     * contrari queda a null i no s'inclou a la resposta, per a mantenir-la
     * idèntica a la de les versions anteriors de l'API.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean permis;
    /**
     * Tipus de document identificatiu del titular admesos pel servei. Només
     * s'informa si s'ha demanat explícitament (veure {@link #permis}).
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Titular.DocumentTipus> documentsTipusPermesos;

    /**
     * Constructor amb les dades bàsiques del servei. El permís de l'usuari
     * s'emplena posteriorment, ja que depèn de qui realitza la petició.
     *
     * @param codi
     *            Codi del servei.
     * @param descripcio
     *            Descripció del servei.
     * @param actiu
     *            Indica si el servei està actiu.
     */
    public ServeiBasic(String codi, String descripcio, Boolean actiu) {
        this.codi = codi;
        this.descripcio = descripcio;
        this.actiu = actiu;
    }

    /**
     * Constructor emprat per les consultes JPQL de ServeiRepository, que
     * projecten els indicadors de tipus de document permesos de la configuració
     * del servei. El permís de l'usuari s'emplena posteriorment, ja que depèn
     * de qui realitza la petició.
     *
     * @param codi
     *            Codi del servei.
     * @param descripcio
     *            Descripció del servei.
     * @param actiu
     *            Indica si el servei està actiu.
     * @param permesDni
     *            Indica si el servei admet documents de tipus DNI.
     * @param permesNif
     *            Indica si el servei admet documents de tipus NIF.
     * @param permesCif
     *            Indica si el servei admet documents de tipus CIF.
     * @param permesNie
     *            Indica si el servei admet documents de tipus NIE.
     * @param permesPassaport
     *            Indica si el servei admet documents de tipus passaport.
     */
    public ServeiBasic(
            String codi,
            String descripcio,
            Boolean actiu,
            Boolean permesDni,
            Boolean permesNif,
            Boolean permesCif,
            Boolean permesNie,
            Boolean permesPassaport) {
        this(codi, descripcio, actiu);
        this.documentsTipusPermesos = new ArrayList<>();
        if (Boolean.TRUE.equals(permesDni))
            this.documentsTipusPermesos.add(Titular.DocumentTipus.DNI);
        if (Boolean.TRUE.equals(permesNif))
            this.documentsTipusPermesos.add(Titular.DocumentTipus.NIF);
        if (Boolean.TRUE.equals(permesCif))
            this.documentsTipusPermesos.add(Titular.DocumentTipus.CIF);
        if (Boolean.TRUE.equals(permesNie))
            this.documentsTipusPermesos.add(Titular.DocumentTipus.NIE);
        if (Boolean.TRUE.equals(permesPassaport))
            this.documentsTipusPermesos.add(Titular.DocumentTipus.Pasaporte);
    }

}
