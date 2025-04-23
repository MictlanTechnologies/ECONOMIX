package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.sql.hibernateimpl.GastosHiberImpl;
import org.economix.vista.catalogos.gastosCatalogo;

public class listaFunciones extends leerAcciones
{
    private static listaFunciones listaDisco;

    private listaFunciones()
    {
    }

    public static listaFunciones getInstance()
    {
        if(listaDisco==null)
        {
            listaDisco = new listaFunciones();
        }
        return listaDisco;
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t::: Catálogo de Funciones :::");
        System.out.println( "1.- Ingresos");
        System.out.println( "2.- Gastos");
        System.out.println( "3.- Salir");
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
        switch(opcion)
        {
            case 1:
            flag = false;
            break;
            case 2:
                ejecutable = gastosCatalogo.getInstance();
                break;
            case 3:
                flag = false;
                break;
            default:
                Menu.opcionInvalida();
                break;
        }
        if(ejecutable!=null)
        {
            ejecutable.setFlag(true);
            ejecutable.run();
        }
    }
}
