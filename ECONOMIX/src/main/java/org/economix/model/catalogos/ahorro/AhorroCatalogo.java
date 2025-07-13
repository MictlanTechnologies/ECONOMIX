package org.economix.model.catalogos.ahorro;

import org.economix.model.ahorro.Ahorro;
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.sql.hibernateimpl.ahorro.AhorroHiberImpl;
import org.economix.sql.hibernateimpl.usuario.UsuarioHiberImpl;
import org.economix.util.readUtil;
import org.economix.vista.acciones.gestorCatalogos;

import java.math.BigDecimal;

/**
 * Catálogo de ahorro para manejo desde la consola.
 */
public class AhorroCatalogo extends gestorCatalogos<Ahorro> {

    private static AhorroCatalogo instancia;
    private static final GenericSql<Ahorro> ahorroHiber = AhorroHiberImpl.getInstance();
    private static final GenericSql<Usuario> usuarioHiber = UsuarioHiberImpl.getInstance();

    public static AhorroCatalogo getInstance() {
        if (instancia == null) instancia = new AhorroCatalogo();
        return instancia;
    }

    private AhorroCatalogo() { super(AhorroHiberImpl.getInstance()); }

    @Override
    public Ahorro newT() { return new Ahorro(); }

    @Override
    public boolean processNewT(Ahorro a) {
        System.out.print("> Nombre del objetivo: ");
        a.setNombreObjetivo(readUtil.read());
        System.out.print("> Descripción: ");
        a.setDescripcionObjetivo(readUtil.read());
        System.out.print("> Meta: ");
        a.setMeta(readUtil.readBigDecimal());
        a.setMontoAhorrado(BigDecimal.ZERO);
        System.out.print("> ID de usuario propietario: ");
        Integer id = readUtil.readInt();
        Usuario u = usuarioHiber.findById(id);
        if (u == null) {
            System.out.println("> ✖ No existe ese usuario. No se guardó el objetivo.");
            return false;
        }
        a.setUsuario(u);
        ahorroHiber.save(a);
        return true;
    }

    @Override
    public boolean processEditT(Ahorro a) {
        System.out.print("> Nuevo nombre del objetivo: ");
        a.setNombreObjetivo(readUtil.read());
        System.out.print("> Nueva descripción: ");
        a.setDescripcionObjetivo(readUtil.read());
        System.out.print("> Nueva meta: ");
        a.setMeta(readUtil.readBigDecimal());
        System.out.print("> Monto ahorrado actual: ");
        a.setMontoAhorrado(readUtil.readBigDecimal());
        System.out.print("> ID de usuario (ENTER para dejar igual): ");
        String inp = readUtil.read();
        if (!inp.isBlank()) {
            Usuario u = usuarioHiber.findById(Integer.parseInt(inp));
            if (u != null) a.setUsuario(u);
        }
        ahorroHiber.update(a);
        return true;
    }
}