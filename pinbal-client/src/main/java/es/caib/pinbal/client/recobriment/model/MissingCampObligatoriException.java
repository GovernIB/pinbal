package es.caib.pinbal.client.recobriment.model;

@SuppressWarnings("serial")
public class MissingCampObligatoriException extends RuntimeException {

    @SuppressWarnings("unused")
	private String camp;

    public MissingCampObligatoriException(String camp) {
        super("El camp '" + camp + "' és obligatori, i no s'ha informat.");
        this.camp = camp;
    }

    public MissingCampObligatoriException(String camp, String message) {
        super(message);
        this.camp = camp;
    }
}
