package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.Rol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Cliente del cine. Tiene una tarjeta virtual, una cuenta de puntos y su historial de compras. */
public class Cliente extends Usuario {

    private final String documento;
    private final TarjetaVirtual tarjeta;
    private final CuentaPuntos cuentaPuntos;
    private final List<Compra> compras = new ArrayList<>();

    public Cliente(String id, String nombre, String documento, String correo, String contrasena,
                   TarjetaVirtual tarjeta) {
        super(id, nombre, correo, contrasena);
        this.documento = documento;
        this.tarjeta = tarjeta;
        this.cuentaPuntos = new CuentaPuntos();
    }

    @Override
    public Rol getRol() {
        return Rol.CLIENTE;
    }

    public void agregarCompra(Compra compra) {
        compras.add(compra);
    }

    public String getDocumento() {
        return documento;
    }

    public TarjetaVirtual getTarjeta() {
        return tarjeta;
    }

    public CuentaPuntos getCuentaPuntos() {
        return cuentaPuntos;
    }

    public List<Compra> getCompras() {
        return Collections.unmodifiableList(compras);
    }

    @Override
    public String toString() {
        return getNombre() + " · CC " + documento + " · " + getCorreo();
    }
}
