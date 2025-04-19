package org.economix.vista.consola;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.vista.listas.listaCatalogos;

public class Consola extends leerAcciones
{
    private static Consola consola;

    private Consola()
    {
    }

    public static Consola getInstance( )
    {
        if(consola==null)
        {
            consola = new Consola();
        }
        return consola;
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t::: Menú principal :::");
        System.out.println("\t> Selecciona una opción:");
        System.out.println("1. Menu Economix");
        System.out.println("2. Menu Ventana Economix");
        System.out.println("3. Salir");
        Menu.seleccionaOpcion();
    }

    @Override
    public int valorMinMenu()
    {
        return 1;
    }
    @Override
    public int valorMaxMenu()
    {
        return 3;
    }

    @Override
    public void procesaOpcion()
    {
        Ejecutable ejecutable = null;
        if(opcion==1)
        {
            ejecutable = listaCatalogos.getInstance();
            ejecutable.setFlag( true );
            ejecutable.run( );
        }
        if(opcion==2)
        {
            System.out.println("> No implementado.");
        }
    }
}

