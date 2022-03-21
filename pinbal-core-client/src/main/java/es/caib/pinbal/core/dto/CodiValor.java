package es.caib.pinbal.core.dto;

public class CodiValor {

    private String codi;
    private String valor;

    public CodiValor(String codi, String valor) {
        this.codi = codi;
        this.valor = valor;
    }

    public CodiValor() {
    }

    public static CodiValorBuilder builder() {
        return new CodiValorBuilder();
    }

    public String getCodi() {
        return this.codi;
    }

    public String getValor() {
        return this.valor;
    }

    public void setCodi(String codi) {
        this.codi = codi;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CodiValor)) return false;
        final CodiValor other = (CodiValor) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$codi = this.getCodi();
        final Object other$codi = other.getCodi();
        if (this$codi == null ? other$codi != null : !this$codi.equals(other$codi)) return false;
        final Object this$valor = this.getValor();
        final Object other$valor = other.getValor();
        if (this$valor == null ? other$valor != null : !this$valor.equals(other$valor)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CodiValor;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $codi = this.getCodi();
        result = result * PRIME + ($codi == null ? 43 : $codi.hashCode());
        final Object $valor = this.getValor();
        result = result * PRIME + ($valor == null ? 43 : $valor.hashCode());
        return result;
    }

    public String toString() {
        return "CodiValor(codi=" + this.getCodi() + ", valor=" + this.getValor() + ")";
    }

    public static class CodiValorBuilder {
        private String codi;
        private String valor;

        CodiValorBuilder() {
        }

        public CodiValorBuilder codi(String codi) {
            this.codi = codi;
            return this;
        }

        public CodiValorBuilder valor(String valor) {
            this.valor = valor;
            return this;
        }

        public CodiValor build() {
            return new CodiValor(codi, valor);
        }

        public String toString() {
            return "CodiValor.CodiValorBuilder(codi=" + this.codi + ", valor=" + this.valor + ")";
        }
    }
}
