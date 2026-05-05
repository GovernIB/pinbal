package es.caib.pinbal.plugin.dadescomuns;

import es.caib.pinbal.plugin.SistemaExternException;

import java.util.List;

public interface DadesComunsPlugin {

    public List<Pais> findPaisos(String idioma) throws SistemaExternException;
    public List<Provincia> findProvincies(String idioma) throws SistemaExternException;
    public List<Municipi> findMunicipisPerProvincia(String provinciaCodi) throws SistemaExternException;

}
