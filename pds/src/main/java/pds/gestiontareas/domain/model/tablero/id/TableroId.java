package pds.gestiontareas.domain.model.tablero.id;

import java.util.Objects;
import java.util.UUID;

public class TableroId {
    private final String valor;

    public TableroId() {
        this.valor = UUID.randomUUID().toString();
    }

    public TableroId(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableroId tableroId = (TableroId) o;
        return Objects.equals(valor, tableroId.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}