package es.caib.pinbal.client.recobriment.model;

@SuppressWarnings("serial")
public class CampLengthException extends RuntimeException {

    enum Tipus {
        MAX,
        MIN
    }

    @SuppressWarnings("unused")
	private String camp;

    public CampLengthException(String camp, Integer length, Tipus tipus) {
        super("La longitud del camp '" + camp + "' és " + (Tipus.MAX.equals(tipus) ? "superior" : "inferior") + " a la permesa (" + length + ").");
        this.camp = camp;
    }
}
