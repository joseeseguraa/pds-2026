package pds.gestiontareas.domain.model.tablero.id;

import java.util.Objects;

public class ListaTareasId {
    
    private final String valor;

    public ListaTareasId(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El valor del ID no puede ser nulo o vacío");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    // Es fundamental sobreescribir equals y hashCode en los IDs 
    // para que Java sepa comparar dos IDs con el mismo valor String.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaTareasId that = (ListaTareasId) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}