package org.economix.vista.acciones;

import org.economix.vista.consola.Consola;

public class seleccionEjecutable extends leerAcciones
{
    public static org.economix.vista.acciones.seleccionEjecutable seleccionEjecutable;

    private seleccionEjecutable()
    {
    }

    public static org.economix.vista.acciones.seleccionEjecutable getInstance()
    {
        if(seleccionEjecutable==null)
        {
            seleccionEjecutable = new seleccionEjecutable();
        }
        return seleccionEjecutable;
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t:::¡Bienvenido a ECONOMIX! :::");
        System.out.println("\t> Selecciona tu método de acceso:");
        System.out.println("1. Consola");
        System.out.println("2. Ventana");
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
            ejecutable = Consola.getInstance();
        }
        if(opcion==2)
        {
            ejecutable = null;
        }
        ejecutable.setFlag( true );
        ejecutable.run();
    }
}

