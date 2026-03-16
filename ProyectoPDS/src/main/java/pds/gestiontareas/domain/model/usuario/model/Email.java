package pds.gestiontareas.domain.model.usuario.model;

import java.util.Objects;

public class Email {
    private final String direccion;

    public Email(String direccion) {
        if (direccion == null || !direccion.contains("@")) {
            throw new IllegalArgumentException("El email no es válido");
        }
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(direccion, email.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direccion);
    }
}