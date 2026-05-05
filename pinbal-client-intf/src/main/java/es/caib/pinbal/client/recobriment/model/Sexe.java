package es.caib.pinbal.client.recobriment.model;

public enum Sexe {
    HOME("V"),
    DONA("M");

    private final String valor;

    Sexe(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return this.valor;
    }
}
