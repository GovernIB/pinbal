package es.caib.pinbal.client.recobriment.model;

public class MissingCampObligatoriException extends RuntimeException {

    private String camp;

    public MissingCampObligatoriException(String camp) {
        super("El camp '" + camp + "' és obligatori, i no s'ha informat.");
        this.camp = camp;
    }
}
