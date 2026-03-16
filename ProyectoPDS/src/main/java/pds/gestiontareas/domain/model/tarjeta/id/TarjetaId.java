package pds.gestiontareas.domain.model.tarjeta.id;

import java.util.Objects;
import java.util.UUID;

public class TarjetaId {
    private final String valor;

    public TarjetaId() {
        this.valor = UUID.randomUUID().toString();
    }

    public TarjetaId(String valor) {
        this.valor = valor;
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TarjetaId tarjetaId = (TarjetaId) o;
        return Objects.equals(valor, tarjetaId.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}