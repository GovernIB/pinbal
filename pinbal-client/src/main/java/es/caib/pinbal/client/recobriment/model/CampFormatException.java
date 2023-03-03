package es.caib.pinbal.client.recobriment.model;

@SuppressWarnings("serial")
public class CampFormatException extends RuntimeException {

    @SuppressWarnings("unused")
	private String camp;

    public CampFormatException(String camp, String format) {
        super("El camp '" + camp + "' no té el format correcte (" + format + ").");
        this.camp = camp;
    }
}
